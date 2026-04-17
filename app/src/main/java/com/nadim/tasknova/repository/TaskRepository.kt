package com.nadim.tasknova.repository

import com.nadim.tasknova.data.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(userId: String): Flow<List<Task>>
    fun getTasksByStatus(userId: String, status: String): Flow<List<Task>>
    suspend fun saveTask(task: Task)
    suspend fun updateTaskStatus(id: String, status: String)
    suspend fun deleteTask(task: Task)
    suspend fun syncToSupabase(userId: String)
}