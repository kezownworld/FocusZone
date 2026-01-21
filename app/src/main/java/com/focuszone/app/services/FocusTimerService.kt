package com.focuszone.app.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.focuszone.app.R
import com.focuszone.app.ui.MainActivity
import com.focuszone.app.utils.TimeUtils

/**
 * Foreground service that manages the focus timer
 */
class FocusTimerService : Service() {
    
    private var countDownTimer: CountDownTimer? = null
    private var timeRemaining: Long = 0
    private var totalTime: Long = 0
    private var isRunning = false
    
    companion object {
        const val ACTION_START = "com.focuszone.app.ACTION_START"
        const val ACTION_STOP = "com.focuszone.app.ACTION_STOP"
        const val ACTION_FORCE_STOP = "com.focuszone.app.ACTION_FORCE_STOP"
        const val EXTRA_DURATION = "extra_duration"
        
        const val BROADCAST_TIMER_UPDATE = "com.focuszone.app.TIMER_UPDATE"
        const val BROADCAST_TIMER_FINISHED = "com.focuszone.app.TIMER_FINISHED"
        const val EXTRA_TIME_REMAINING = "extra_time_remaining"
        
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "focus_timer_channel"
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val duration = intent.getLongExtra(EXTRA_DURATION, 0)
                if (duration > 0) {
                    startTimer(duration)
                }
            }
            ACTION_STOP, ACTION_FORCE_STOP -> {
                stopTimer(intent.action == ACTION_FORCE_STOP)
            }
        }
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun startTimer(duration: Long) {
        totalTime = duration
        timeRemaining = duration
        isRunning = true
        
        // Start as foreground service
        startForeground(NOTIFICATION_ID, createNotification())
        
        // Start blocking services
        startBlockingServices()
        
        countDownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                updateNotification()
                broadcastTimerUpdate()
            }
            
            override fun onFinish() {
                timeRemaining = 0
                isRunning = false
                broadcastTimerFinished()
                stopBlockingServices()
                stopSelf()
            }
        }.start()
    }
    
    private fun stopTimer(forceStopped: Boolean) {
        countDownTimer?.cancel()
        isRunning = false
        stopBlockingServices()
        
        if (forceStopped) {
            broadcastTimerUpdate()
        }
        
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    private fun startBlockingServices() {
        // Start notification blocker
        val notificationIntent = Intent(this, NotificationBlockerService::class.java)
        notificationIntent.action = NotificationBlockerService.ACTION_START_BLOCKING
        startService(notificationIntent)
        
        // Start app blocker
        val appBlockingIntent = Intent(this, AppBlockingService::class.java)
        appBlockingIntent.action = AppBlockingService.ACTION_START_MONITORING
        startService(appBlockingIntent)
    }
    
    private fun stopBlockingServices() {
        // Stop notification blocker
        val notificationIntent = Intent(this, NotificationBlockerService::class.java)
        notificationIntent.action = NotificationBlockerService.ACTION_STOP_BLOCKING
        startService(notificationIntent)
        
        // Stop app blocker
        val appBlockingIntent = Intent(this, AppBlockingService::class.java)
        appBlockingIntent.action = AppBlockingService.ACTION_STOP_MONITORING
        startService(appBlockingIntent)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val stopIntent = Intent(this, FocusTimerService::class.java).apply {
            action = ACTION_FORCE_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text, TimeUtils.formatTime(timeRemaining)))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_stop,
                getString(R.string.force_stop),
                stopPendingIntent
            )
            .build()
    }
    
    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }
    
    private fun broadcastTimerUpdate() {
        val intent = Intent(BROADCAST_TIMER_UPDATE).apply {
            putExtra(EXTRA_TIME_REMAINING, timeRemaining)
        }
        sendBroadcast(intent)
    }
    
    private fun broadcastTimerFinished() {
        val intent = Intent(BROADCAST_TIMER_FINISHED)
        sendBroadcast(intent)
    }
}
