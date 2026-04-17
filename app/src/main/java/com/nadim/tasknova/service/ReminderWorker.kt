package com.nadim.tasknova.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nadim.tasknova.repository.ReminderRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val reminderRepository: ReminderRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val dueReminders = reminderRepository.getDueReminders()
            dueReminders.forEach { reminder ->
                // Mark as triggered
                reminderRepository.updateReminderStatus(reminder.id, "done")
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}