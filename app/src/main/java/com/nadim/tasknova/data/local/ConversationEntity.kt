package com.nadim.tasknova.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val role: String,                  // user / assistant
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)