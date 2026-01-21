package com.focuszone.app.utils

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manager for app theme (Light/Dark mode)
 */
class ThemeManager(private val context: Context) {
    
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")
        private val THEME_KEY = intPreferencesKey("theme_mode")
        
        const val THEME_LIGHT = AppCompatDelegate.MODE_NIGHT_NO
        const val THEME_DARK = AppCompatDelegate.MODE_NIGHT_YES
        const val THEME_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
    
    /**
     * Get current theme mode as Flow
     */
    val themeMode: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: THEME_SYSTEM
    }
    
    /**
     * Set theme mode
     */
    suspend fun setThemeMode(mode: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = mode
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
    
    /**
     * Toggle between light and dark mode
     */
    suspend fun toggleTheme() {
        val currentMode = getCurrentThemeMode()
        val newMode = if (currentMode == THEME_LIGHT) THEME_DARK else THEME_LIGHT
        setThemeMode(newMode)
    }
    
    /**
     * Get current theme mode synchronously
     */
    private fun getCurrentThemeMode(): Int {
        val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return when (nightMode) {
            Configuration.UI_MODE_NIGHT_YES -> THEME_DARK
            Configuration.UI_MODE_NIGHT_NO -> THEME_LIGHT
            else -> THEME_SYSTEM
        }
    }
    
    /**
     * Check if dark mode is currently active
     */
    fun isDarkMode(): Boolean {
        val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightMode == Configuration.UI_MODE_NIGHT_YES
    }
}
