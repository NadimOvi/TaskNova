package com.nadim.tasknova.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val type: String = "time",         // time / location / recurring
    val status: String = "pending",    // pending / done / missed
    val remindAt: Long? = null,
    val recurrence: String? = null,    // daily / weekly / null
    val triggeredAt: Long? = null,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)