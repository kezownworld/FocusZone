package com.focuszone.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a blocked application
 */
@Entity(tableName = "blocked_apps")
data class BlockedApp(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val isBlocked: Boolean = true,
    val addedAt: Long = System.currentTimeMillis()
)
