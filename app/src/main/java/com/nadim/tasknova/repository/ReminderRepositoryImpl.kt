package com.nadim.tasknova.repository

import com.nadim.tasknova.data.local.ReminderDao
import com.nadim.tasknova.data.model.Reminder
import com.nadim.tasknova.data.model.toEntity
import com.nadim.tasknova.data.model.toModel
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao,
    private val postgrest: Postgrest
) : ReminderRepository {

    override fun getAllReminders(userId: String): Flow<List<Reminder>> =
        reminderDao.getAllReminders(userId).map { it.map { e -> e.toModel() } }

    override fun getRemindersByStatus(userId: String, status: String): Flow<List<Reminder>> =
        reminderDao.getRemindersByStatus(userId, status).map { it.map { e -> e.toModel() } }

    override suspend fun saveReminder(reminder: Reminder) {
        val entity = reminder.copy(
            id = reminder.id.ifEmpty { UUID.randomUUID().toString() }
        ).toEntity()
        reminderDao.insertReminder(entity)
        try {
            postgrest.from("reminders").upsert(
                mapOf(
                    "id"         to entity.id,
                    "user_id"    to entity.userId,
                    "title"      to entity.title,
                    "type"       to entity.type,
                    "status"     to entity.status,
                    "remind_at"  to entity.remindAt,
                    "recurrence" to entity.recurrence
                )
            )
        } catch (e: Exception) { }
    }

    override suspend fun updateReminderStatus(id: String, status: String) {
        val completedAt = if (status == "done") System.currentTimeMillis() else null
        reminderDao.updateReminderStatus(id, status, completedAt)
        try {
            postgrest.from("reminders").update(
                mapOf("status" to status, "completed_at" to completedAt)
            ) { filter { eq("id", id) } }
        } catch (e: Exception) { }
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder.toEntity())
        try {
            postgrest.from("reminders").delete { filter { eq("id", reminder.id) } }
        } catch (e: Exception) { }
    }

    override suspend fun getDueReminders(): List<Reminder> =
        reminderDao.getDueReminders(System.currentTimeMillis()).map { it.toModel() }
}