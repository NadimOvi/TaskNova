package com.nadim.tasknova.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE userId = :userId ORDER BY remindAt ASC")
    fun getAllReminders(userId: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE userId = :userId AND status = :status ORDER BY remindAt ASC")
    fun getRemindersByStatus(userId: String, status: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: String): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE status = 'pending' AND remindAt <= :now")
    suspend fun getDueReminders(now: Long): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("UPDATE reminders SET status = :status, completedAt = :completedAt WHERE id = :id")
    suspend fun updateReminderStatus(id: String, status: String, completedAt: Long?)

    @Query("DELETE FROM reminders WHERE userId = :userId")
    suspend fun deleteAllRemindersForUser(userId: String)
}