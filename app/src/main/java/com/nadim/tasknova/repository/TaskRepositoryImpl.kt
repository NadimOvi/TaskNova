package com.nadim.tasknova.repository

import com.nadim.tasknova.data.local.TaskDao
import com.nadim.tasknova.data.model.Task
import com.nadim.tasknova.data.model.toEntity
import com.nadim.tasknova.data.model.toModel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val postgrest: Postgrest
) : TaskRepository {

    override fun getAllTasks(userId: String): Flow<List<Task>> =
        taskDao.getAllTasks(userId).map { it.map { e -> e.toModel() } }

    override fun getTasksByStatus(userId: String, status: String): Flow<List<Task>> =
        taskDao.getTasksByStatus(userId, status).map { it.map { e -> e.toModel() } }

    override suspend fun saveTask(task: Task) {
        val entity = task.copy(
            id = task.id.ifEmpty { UUID.randomUUID().toString() }
        ).toEntity()
        taskDao.insertTask(entity)
        try {
            postgrest.from("tasks").upsert(
                mapOf(
                    "id"           to entity.id,
                    "user_id"      to entity.userId,
                    "title"        to entity.title,
                    "description"  to entity.description,
                    "priority"     to entity.priority,
                    "status"       to entity.status,
                    "due_date"     to entity.dueDate,
                    "completed_at" to entity.completedAt
                )
            )
        } catch (e: Exception) { }
    }

    override suspend fun updateTaskStatus(id: String, status: String) {
        val completedAt = if (status == "done") System.currentTimeMillis() else null
        taskDao.updateTaskStatus(id, status, completedAt)
        try {
            postgrest.from("tasks").update(
                mapOf("status" to status, "completed_at" to completedAt)
            ) { filter { eq("id", id) } }
        } catch (e: Exception) { }
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
        try {
            postgrest.from("tasks").delete { filter { eq("id", task.id) } }
        } catch (e: Exception) { }
    }

    override suspend fun syncToSupabase(userId: String) { }
}