package com.focuszone.app.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

/**
 * Data class representing app information
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    val isSystemApp: Boolean
)

/**
 * Provider for retrieving installed app information
 */
class AppInfoProvider(private val context: Context) {
    
    /**
     * Get list of all installed apps (excluding system apps by default)
     */
    fun getInstalledApps(includeSystemApps: Boolean = false): List<AppInfo> {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        
        return packages
            .filter { appInfo ->
                // Filter out this app itself
                appInfo.packageName != context.packageName &&
                // Filter system apps if needed
                (includeSystemApps || (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0)
            }
            .map { appInfo ->
                AppInfo(
                    packageName = appInfo.packageName,
                    appName = appInfo.loadLabel(packageManager).toString(),
                    icon = appInfo.loadIcon(packageManager),
                    isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                )
            }
            .sortedBy { it.appName.lowercase() }
    }
    
    /**
     * Get app info for a specific package
     */
    fun getAppInfo(packageName: String): AppInfo? {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            AppInfo(
                packageName = packageName,
                appName = appInfo.loadLabel(packageManager).toString(),
                icon = appInfo.loadIcon(packageManager),
                isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            )
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
    
    /**
     * Check if an app is installed
     */
    fun isAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
