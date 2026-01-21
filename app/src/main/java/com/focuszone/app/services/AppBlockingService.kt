package com.focuszone.app.services

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.focuszone.app.data.AppDatabase
import com.focuszone.app.ui.BlockingOverlayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Service that monitors app usage and shows blocking overlay for blocked apps
 */
class AppBlockingService : Service() {
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val handler = Handler(Looper.getMainLooper())
    private var isMonitoring = false
    private var blockedPackages = setOf<String>()
    private var lastCheckedPackage: String? = null
    
    companion object {
        private const val TAG = "AppBlockingService"
        const val ACTION_START_MONITORING = "com.focuszone.app.START_MONITORING"
        const val ACTION_STOP_MONITORING = "com.focuszone.app.STOP_MONITORING"
        private const val CHECK_INTERVAL = 500L // Check every 500ms
    }
    
    private val monitoringRunnable = object : Runnable {
        override fun run() {
            if (isMonitoring) {
                checkForegroundApp()
                handler.postDelayed(this, CHECK_INTERVAL)
            }
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        loadBlockedApps()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_MONITORING -> {
                startMonitoring()
            }
            ACTION_STOP_MONITORING -> {
                stopMonitoring()
            }
        }
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun loadBlockedApps() {
        serviceScope.launch(Dispatchers.IO) {
            val database = AppDatabase.getDatabase(applicationContext)
            val apps = database.appDao().getBlockedAppsList()
            blockedPackages = apps.map { it.packageName }.toSet()
            Log.d(TAG, "Loaded ${blockedPackages.size} blocked apps")
        }
    }
    
    private fun startMonitoring() {
        if (isMonitoring) return
        
        isMonitoring = true
        loadBlockedApps()
        handler.post(monitoringRunnable)
        Log.d(TAG, "App monitoring started")
    }
    
    private fun stopMonitoring() {
        isMonitoring = false
        handler.removeCallbacks(monitoringRunnable)
        lastCheckedPackage = null
        Log.d(TAG, "App monitoring stopped")
    }
    
    private fun checkForegroundApp() {
        val foregroundApp = getForegroundApp()
        
        if (foregroundApp != null && 
            foregroundApp != packageName && 
            foregroundApp != lastCheckedPackage &&
            blockedPackages.contains(foregroundApp)) {
            
            Log.d(TAG, "Blocked app detected: $foregroundApp")
            lastCheckedPackage = foregroundApp
            showBlockingOverlay()
        }
    }
    
    private fun getForegroundApp(): String? {
        try {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val currentTime = System.currentTimeMillis()
            
            // Query events from the last 1 second
            val usageEvents = usageStatsManager.queryEvents(currentTime - 1000, currentTime)
            val event = UsageEvents.Event()
            
            var foregroundApp: String? = null
            
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    foregroundApp = event.packageName
                }
            }
            
            return foregroundApp
        } catch (e: Exception) {
            Log.e(TAG, "Error getting foreground app: ${e.message}", e)
            return null
        }
    }
    
    private fun showBlockingOverlay() {
        val intent = Intent(this, BlockingOverlayActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(intent)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopMonitoring()
    }
}
