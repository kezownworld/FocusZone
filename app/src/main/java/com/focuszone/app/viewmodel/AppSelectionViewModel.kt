package com.focuszone.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.focuszone.app.data.AppDatabase
import com.focuszone.app.data.BlockedApp
import com.focuszone.app.utils.AppInfo
import com.focuszone.app.utils.AppInfoProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for AppSelectionActivity
 */
class AppSelectionViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val dao = database.appDao()
    private val appInfoProvider = AppInfoProvider(application)
    
    private val _installedApps = MutableLiveData<List<AppInfo>>()
    val installedApps: LiveData<List<AppInfo>> = _installedApps
    
    private val _selectedApps = MutableLiveData<Set<String>>(emptySet())
    val selectedApps: LiveData<Set<String>> = _selectedApps
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    init {
        loadInstalledApps()
        loadSelectedApps()
    }
    
    private fun loadInstalledApps() {
        _isLoading.value = true
        
        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) {
                appInfoProvider.getInstalledApps(includeSystemApps = false)
            }
            _installedApps.value = apps
            _isLoading.value = false
        }
    }
    
    private fun loadSelectedApps() {
        viewModelScope.launch {
            val blockedApps = dao.getBlockedAppsList()
            _selectedApps.value = blockedApps.map { it.packageName }.toSet()
        }
    }
    
    fun toggleAppSelection(packageName: String, appName: String) {
        val currentSelection = _selectedApps.value ?: emptySet()
        _selectedApps.value = if (currentSelection.contains(packageName)) {
            currentSelection - packageName
        } else {
            currentSelection + packageName
        }
    }
    
    fun selectAll() {
        val allPackages = _installedApps.value?.map { it.packageName }?.toSet() ?: emptySet()
        _selectedApps.value = allPackages
    }
    
    fun deselectAll() {
        _selectedApps.value = emptySet()
    }
    
    fun saveSelection(onComplete: () -> Unit) {
        viewModelScope.launch {
            // Clear existing blocked apps
            dao.deleteAllBlockedApps()
            
            // Insert newly selected apps
            val selectedPackages = _selectedApps.value ?: emptySet()
            val blockedApps = _installedApps.value
                ?.filter { selectedPackages.contains(it.packageName) }
                ?.map { BlockedApp(packageName = it.packageName, appName = it.appName) }
                ?: emptyList()
            
            dao.insertBlockedApps(blockedApps)
            
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }
    
    fun searchApps(query: String) {
        viewModelScope.launch {
            val allApps = withContext(Dispatchers.IO) {
                appInfoProvider.getInstalledApps(includeSystemApps = false)
            }
            
            val filteredApps = if (query.isBlank()) {
                allApps
            } else {
                allApps.filter { 
                    it.appName.contains(query, ignoreCase = true) ||
                    it.packageName.contains(query, ignoreCase = true)
                }
            }
            
            _installedApps.value = filteredApps
        }
    }
}
