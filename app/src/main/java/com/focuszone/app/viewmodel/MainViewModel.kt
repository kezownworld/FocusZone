package com.focuszone.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.focuszone.app.data.AppDatabase
import com.focuszone.app.data.FocusSession
import kotlinx.coroutines.launch

/**
 * ViewModel for MainActivity
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val dao = database.appDao()
    
    private val _timeRemaining = MutableLiveData<Long>(0L)
    val timeRemaining: LiveData<Long> = _timeRemaining
    
    private val _isTimerRunning = MutableLiveData<Boolean>(false)
    val isTimerRunning: LiveData<Boolean> = _isTimerRunning
    
    private val _timerDuration = MutableLiveData<Long>(0L)
    val timerDuration: LiveData<Long> = _timerDuration
    
    private val _blockedAppsCount = MutableLiveData<Int>(0)
    val blockedAppsCount: LiveData<Int> = _blockedAppsCount
    
    private var sessionStartTime: Long = 0
    private var currentSessionId: Long? = null
    
    init {
        loadBlockedAppsCount()
    }
    
    fun setTimerDuration(duration: Long) {
        _timerDuration.value = duration
    }
    
    fun updateTimeRemaining(time: Long) {
        _timeRemaining.value = time
    }
    
    fun setTimerRunning(running: Boolean) {
        _isTimerRunning.value = running
    }
    
    fun startSession(duration: Long) {
        sessionStartTime = System.currentTimeMillis()
        _isTimerRunning.value = true
        _timerDuration.value = duration
        _timeRemaining.value = duration
        
        viewModelScope.launch {
            val session = FocusSession(
                startTime = sessionStartTime,
                endTime = 0,
                plannedDuration = duration,
                actualDuration = 0,
                wasCompleted = false,
                wasForceStopped = false,
                blockedAppsCount = _blockedAppsCount.value ?: 0
            )
            currentSessionId = dao.insertSession(session)
        }
    }
    
    fun completeSession() {
        val sessionId = currentSessionId ?: return
        val actualDuration = System.currentTimeMillis() - sessionStartTime
        
        viewModelScope.launch {
            val session = dao.getSession(sessionId)
            session?.let {
                val updatedSession = it.copy(
                    endTime = System.currentTimeMillis(),
                    actualDuration = actualDuration,
                    wasCompleted = true
                )
                dao.updateSession(updatedSession)
            }
        }
        
        _isTimerRunning.value = false
        currentSessionId = null
    }
    
    fun forceStopSession() {
        val sessionId = currentSessionId ?: return
        val actualDuration = System.currentTimeMillis() - sessionStartTime
        
        viewModelScope.launch {
            val session = dao.getSession(sessionId)
            session?.let {
                val updatedSession = it.copy(
                    endTime = System.currentTimeMillis(),
                    actualDuration = actualDuration,
                    wasCompleted = false,
                    wasForceStopped = true
                )
                dao.updateSession(updatedSession)
            }
        }
        
        _isTimerRunning.value = false
        currentSessionId = null
    }
    
    private fun loadBlockedAppsCount() {
        viewModelScope.launch {
            val apps = dao.getBlockedAppsList()
            _blockedAppsCount.value = apps.size
        }
    }
    
    fun refreshBlockedAppsCount() {
        loadBlockedAppsCount()
    }
}
