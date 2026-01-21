package com.focuszone.app.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import java.lang.reflect.Method

/**
 * Broadcast receiver for handling incoming calls during focus mode
 */
class CallBlockingReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "CallBlockingReceiver"
        var isBlockingEnabled = false
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (!isBlockingEnabled) return
        
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                Log.d(TAG, "Incoming call detected, attempting to block")
                blockCall(context)
            }
        }
    }
    
    private fun blockCall(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // Android 9.0+ method
                val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                if (telecomManager.isInCall) {
                    telecomManager.endCall()
                    Log.d(TAG, "Call blocked using TelecomManager")
                }
            } else {
                // Older Android versions - use reflection
                val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val telephonyClass = Class.forName(telephonyManager.javaClass.name)
                val method: Method = telephonyClass.getDeclaredMethod("endCall")
                method.isAccessible = true
                method.invoke(telephonyManager)
                Log.d(TAG, "Call blocked using reflection")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to block call: ${e.message}", e)
        }
    }
}
