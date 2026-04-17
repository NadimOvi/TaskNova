package com.nadim.tasknova.data.model

data class Reminder(
    val id: String,
    val userId: String,
    val title: String,
    val type: String = "time",
    val status: String = "pending",
    val remindAt: Long? = null,
    val recurrence: String? = null,
    val triggeredAt: Long? = null,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

fun com.nadim.tasknova.data.local.ReminderEntity.toModel() = Reminder(
    id = id, userId = userId, title = title,
    type = type, status = status, remindAt = remindAt,
    recurrence = recurrence, triggeredAt = triggeredAt,
    completedAt = completedAt, createdAt = createdAt
)

fun Reminder.toEntity() = com.nadim.tasknova.data.local.ReminderEntity(
    id = id, userId = userId, title = title,
    type = type, status = status, remindAt = remindAt,
    recurrence = recurrence, triggeredAt = triggeredAt,
    completedAt = completedAt, createdAt = createdAt
)