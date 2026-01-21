package com.focuszone.app.utils

import java.util.concurrent.TimeUnit

/**
 * Utility object for time-related operations
 */
object TimeUtils {
    
    /**
     * Format milliseconds to HH:MM:SS string
     */
    fun formatTime(milliseconds: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
        
        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }
    
    /**
     * Format milliseconds to readable string (e.g., "2 hours 30 minutes")
     */
    fun formatTimeReadable(milliseconds: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
        
        return buildString {
            if (hours > 0) {
                append("$hours hour${if (hours > 1) "s" else ""}")
            }
            if (minutes > 0) {
                if (isNotEmpty()) append(" ")
                append("$minutes minute${if (minutes > 1) "s" else ""}")
            }
            if (hours == 0L && minutes == 0L && seconds > 0) {
                append("$seconds second${if (seconds > 1) "s" else ""}")
            }
        }.ifEmpty { "0 seconds" }
    }
    
    /**
     * Convert hours, minutes, seconds to milliseconds
     */
    fun toMilliseconds(hours: Int, minutes: Int, seconds: Int): Long {
        return TimeUnit.HOURS.toMillis(hours.toLong()) +
                TimeUnit.MINUTES.toMillis(minutes.toLong()) +
                TimeUnit.SECONDS.toMillis(seconds.toLong())
    }
}
