package com.nadim.tasknova.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String? = null,
    val content: String,
    val summary: String? = null,
    val convertedTo: String? = null,   // task / email / null
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)