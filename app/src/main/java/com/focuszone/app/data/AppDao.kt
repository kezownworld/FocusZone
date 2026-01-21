package com.focuszone.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for database operations
 */
@Dao
interface AppDao {
    
    // Blocked Apps operations
    @Query("SELECT * FROM blocked_apps WHERE isBlocked = 1")
    fun getBlockedApps(): Flow<List<BlockedApp>>
    
    @Query("SELECT * FROM blocked_apps WHERE isBlocked = 1")
    suspend fun getBlockedAppsList(): List<BlockedApp>
    
    @Query("SELECT * FROM blocked_apps WHERE packageName = :packageName")
    suspend fun getBlockedApp(packageName: String): BlockedApp?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedApp(app: BlockedApp)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedApps(apps: List<BlockedApp>)
    
    @Delete
    suspend fun deleteBlockedApp(app: BlockedApp)
    
    @Query("DELETE FROM blocked_apps WHERE packageName = :packageName")
    suspend fun deleteBlockedAppByPackage(packageName: String)
    
    @Query("DELETE FROM blocked_apps")
    suspend fun deleteAllBlockedApps()
    
    @Query("UPDATE blocked_apps SET isBlocked = :isBlocked WHERE packageName = :packageName")
    suspend fun updateBlockedStatus(packageName: String, isBlocked: Boolean)
    
    // Focus Sessions operations
    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<FocusSession>>
    
    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC LIMIT :limit")
    fun getRecentSessions(limit: Int = 10): Flow<List<FocusSession>>
    
    @Query("SELECT * FROM focus_sessions WHERE id = :id")
    suspend fun getSession(id: Long): FocusSession?
    
    @Insert
    suspend fun insertSession(session: FocusSession): Long
    
    @Update
    suspend fun updateSession(session: FocusSession)
    
    @Delete
    suspend fun deleteSession(session: FocusSession)
    
    @Query("DELETE FROM focus_sessions")
    suspend fun deleteAllSessions()
    
    // Statistics queries
    @Query("SELECT COUNT(*) FROM focus_sessions WHERE wasCompleted = 1")
    suspend fun getCompletedSessionsCount(): Int
    
    @Query("SELECT SUM(actualDuration) FROM focus_sessions WHERE wasCompleted = 1")
    suspend fun getTotalFocusTime(): Long?
    
    @Query("SELECT AVG(actualDuration) FROM focus_sessions WHERE wasCompleted = 1")
    suspend fun getAverageFocusTime(): Long?
}
