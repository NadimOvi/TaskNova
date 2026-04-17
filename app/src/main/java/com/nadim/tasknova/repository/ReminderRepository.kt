package com.nadim.tasknova.repository

import com.nadim.tasknova.data.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getAllReminders(userId: String): Flow<List<Reminder>>
    fun getRemindersByStatus(userId: String, status: String): Flow<List<Reminder>>
    suspend fun saveReminder(reminder: Reminder)
    suspend fun updateReminderStatus(id: String, status: String)
    suspend fun deleteReminder(reminder: Reminder)
    suspend fun getDueReminders(): List<Reminder>
}