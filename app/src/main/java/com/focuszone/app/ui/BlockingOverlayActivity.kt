package com.focuszone.app.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.focuszone.app.databinding.ActivityBlockingOverlayBinding
import com.focuszone.app.services.FocusTimerService
import com.focuszone.app.utils.TimeUtils

class BlockingOverlayActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityBlockingOverlayBinding
    
    private val timerUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val timeRemaining = intent?.getLongExtra(FocusTimerService.EXTRA_TIME_REMAINING, 0) ?: 0
            updateTimeDisplay(timeRemaining)
        }
    }
    
    private val timerFinishedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finish()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityBlockingOverlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Register broadcast receivers
        registerReceiver(timerUpdateReceiver, IntentFilter(FocusTimerService.BROADCAST_TIMER_UPDATE))
        registerReceiver(timerFinishedReceiver, IntentFilter(FocusTimerService.BROADCAST_TIMER_FINISHED))
        
        // Prevent back button from closing
        onBackPressedDispatcher.addCallback(this) {
            // Do nothing - prevent user from going back
        }
    }
    
    private fun updateTimeDisplay(timeRemaining: Long) {
        binding.tvTimeRemaining.text = TimeUtils.formatTime(timeRemaining)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(timerUpdateReceiver)
            unregisterReceiver(timerFinishedReceiver)
        } catch (e: Exception) {
            // Receiver not registered
        }
    }
}
