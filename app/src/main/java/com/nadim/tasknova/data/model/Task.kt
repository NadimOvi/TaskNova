package com.nadim.tasknova.data.model

data class Task(
    val id: String,
    val userId: String,
    val title: String,
    val description: String? = null,
    val priority: String = "medium",
    val status: String = "pending",
    val dueDate: Long? = null,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// Extension to convert Entity → Model
fun com.nadim.tasknova.data.local.TaskEntity.toModel() = Task(
    id = id, userId = userId, title = title,
    description = description, priority = priority,
    status = status, dueDate = dueDate,
    completedAt = completedAt, createdAt = createdAt
)

// Extension to convert Model → Entity
fun Task.toEntity() = com.nadim.tasknova.data.local.TaskEntity(
    id = id, userId = userId, title = title,
    description = description, priority = priority,
    status = status, dueDate = dueDate,
    completedAt = completedAt, createdAt = createdAt
)