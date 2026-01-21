package com.focuszone.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a focus session
 */
@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val plannedDuration: Long, // in milliseconds
    val actualDuration: Long, // in milliseconds
    val wasCompleted: Boolean,
    val wasForceStopped: Boolean = false,
    val blockedAppsCount: Int = 0
)
