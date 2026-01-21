package com.focuszone.app.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.focuszone.app.R
import com.focuszone.app.databinding.ActivityMainBinding
import com.focuszone.app.services.CallBlockingReceiver
import com.focuszone.app.services.FocusTimerService
import com.focuszone.app.utils.PermissionHelper
import com.focuszone.app.utils.ThemeManager
import com.focuszone.app.utils.TimeUtils
import com.focuszone.app.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var themeManager: ThemeManager
    
    private val timerUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val timeRemaining = intent?.getLongExtra(FocusTimerService.EXTRA_TIME_REMAINING, 0) ?: 0
            viewModel.updateTimeRemaining(timeRemaining)
        }
    }
    
    private val timerFinishedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.completeSession()
            Toast.makeText(this@MainActivity, R.string.focus_completed, Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        themeManager = ThemeManager(this)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        setupObservers()
        setupListeners()
        checkPermissions()
        
        // Register broadcast receivers
        registerReceiver(timerUpdateReceiver, IntentFilter(FocusTimerService.BROADCAST_TIMER_UPDATE))
        registerReceiver(timerFinishedReceiver, IntentFilter(FocusTimerService.BROADCAST_TIMER_FINISHED))
    }
    
    private fun setupUI() {
        // Setup number pickers
        binding.pickerHours.apply {
            minValue = 0
            maxValue = 23
            value = 0
        }
        
        binding.pickerMinutes.apply {
            minValue = 0
            maxValue = 59
            value = 25
        }
        
        binding.pickerSeconds.apply {
            minValue = 0
            maxValue = 59
            value = 0
        }
    }
    
    private fun setupObservers() {
        viewModel.timeRemaining.observe(this) { time ->
            binding.tvTimer.text = TimeUtils.formatTime(time)
        }
        
        viewModel.isTimerRunning.observe(this) { isRunning ->
            binding.cardTimePicker.visibility = if (isRunning) android.view.View.GONE else android.view.View.VISIBLE
            binding.btnStartFocus.visibility = if (isRunning) android.view.View.GONE else android.view.View.VISIBLE
            binding.btnStopFocus.visibility = if (isRunning) android.view.View.VISIBLE else android.view.View.GONE
            binding.btnSelectApps.isEnabled = !isRunning
        }
        
        viewModel.blockedAppsCount.observe(this) { count ->
            binding.tvBlockedAppsInfo.text = if (count > 0) {
                getString(R.string.apps_selected, count)
            } else {
                getString(R.string.no_apps_selected)
            }
        }
    }
    
    private fun setupListeners() {
        binding.btnStartFocus.setOnClickListener {
            startFocusSession()
        }
        
        binding.btnStopFocus.setOnClickListener {
            showForceStopDialog()
        }
        
        binding.btnSelectApps.setOnClickListener {
            startActivity(Intent(this, AppSelectionActivity::class.java))
        }
        
        binding.btnThemeToggle.setOnClickListener {
            lifecycleScope.launch {
                themeManager.toggleTheme()
            }
        }
    }
    
    private fun startFocusSession() {
        val hours = binding.pickerHours.value
        val minutes = binding.pickerMinutes.value
        val seconds = binding.pickerSeconds.value
        
        val duration = TimeUtils.toMilliseconds(hours, minutes, seconds)
        
        if (duration <= 0) {
            Toast.makeText(this, R.string.error_timer_invalid, Toast.LENGTH_SHORT).show()
            return
        }
        
        if (!PermissionHelper.hasAllPermissions(this)) {
            Toast.makeText(this, R.string.error_permission, Toast.LENGTH_LONG).show()
            return
        }
        
        // Enable call blocking
        CallBlockingReceiver.isBlockingEnabled = true
        
        // Start timer service
        val intent = Intent(this, FocusTimerService::class.java).apply {
            action = FocusTimerService.ACTION_START
            putExtra(FocusTimerService.EXTRA_DURATION, duration)
        }
        startService(intent)
        
        viewModel.startSession(duration)
        Toast.makeText(this, R.string.focus_started, Toast.LENGTH_SHORT).show()
    }
    
    private fun showForceStopDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_force_stop_title)
            .setMessage(R.string.confirm_force_stop_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                forceStopSession()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
    
    private fun forceStopSession() {
        // Disable call blocking
        CallBlockingReceiver.isBlockingEnabled = false
        
        // Stop timer service
        val intent = Intent(this, FocusTimerService::class.java).apply {
            action = FocusTimerService.ACTION_FORCE_STOP
        }
        startService(intent)
        
        viewModel.forceStopSession()
        Toast.makeText(this, R.string.focus_stopped, Toast.LENGTH_SHORT).show()
    }
    
    private fun checkPermissions() {
        val missingPermissions = mutableListOf<String>()
        
        if (!PermissionHelper.hasPhonePermissions(this)) {
            missingPermissions.add("Phone")
        }
        if (!PermissionHelper.hasNotificationListenerPermission(this)) {
            missingPermissions.add("Notification Access")
        }
        if (!PermissionHelper.hasUsageStatsPermission(this)) {
            missingPermissions.add("Usage Access")
        }
        if (!PermissionHelper.hasOverlayPermission(this)) {
            missingPermissions.add("Overlay")
        }
        if (!PermissionHelper.hasPostNotificationPermission(this)) {
            missingPermissions.add("Post Notifications")
        }
        
        if (missingPermissions.isNotEmpty()) {
            showPermissionsDialog()
        }
    }
    
    private fun showPermissionsDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_required)
            .setMessage("This app requires several permissions to function properly. Please grant all permissions.")
            .setPositiveButton(R.string.open_settings) { _, _ ->
                requestPermissions()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun requestPermissions() {
        // Request phone permissions
        if (!PermissionHelper.hasPhonePermissions(this)) {
            PermissionHelper.requestPhonePermissions(this)
        }
        
        // Request notification permission
        if (!PermissionHelper.hasPostNotificationPermission(this)) {
            PermissionHelper.requestPostNotificationPermission(this)
        }
        
        // Open settings for special permissions
        if (!PermissionHelper.hasNotificationListenerPermission(this)) {
            PermissionHelper.requestNotificationListenerPermission(this)
        }
        
        if (!PermissionHelper.hasUsageStatsPermission(this)) {
            PermissionHelper.requestUsageStatsPermission(this)
        }
        
        if (!PermissionHelper.hasOverlayPermission(this)) {
            PermissionHelper.requestOverlayPermission(this)
        }
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.refreshBlockedAppsCount()
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
