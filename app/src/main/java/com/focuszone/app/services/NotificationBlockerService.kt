package com.focuszone.app.services

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

/**
 * Notification listener service that blocks notifications during focus mode
 */
class NotificationBlockerService : NotificationListenerService() {
    
    companion object {
        private const val TAG = "NotificationBlocker"
        const val ACTION_START_BLOCKING = "com.focuszone.app.START_BLOCKING"
        const val ACTION_STOP_BLOCKING = "com.focuszone.app.STOP_BLOCKING"
        
        private var isBlocking = false
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_BLOCKING -> {
                isBlocking = true
                Log.d(TAG, "Notification blocking started")
            }
            ACTION_STOP_BLOCKING -> {
                isBlocking = false
                Log.d(TAG, "Notification blocking stopped")
            }
        }
        return START_STICKY
    }
    
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (!isBlocking) return
        
        // Don't block our own notifications
        if (sbn.packageName == packageName) {
            return
        }
        
        // Block the notification
        try {
            cancelNotification(sbn.key)
            Log.d(TAG, "Blocked notification from: ${sbn.packageName}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel notification: ${e.message}", e)
        }
    }
    
    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Handle notification removal if needed
    }
    
    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Notification listener connected")
    }
    
    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "Notification listener disconnected")
    }
}
