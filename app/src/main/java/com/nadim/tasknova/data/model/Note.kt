package com.nadim.tasknova.data.model

data class Note(
    val id: String,
    val userId: String,
    val title: String? = null,
    val content: String,
    val summary: String? = null,
    val convertedTo: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

fun com.nadim.tasknova.data.local.NoteEntity.toModel() = Note(
    id = id, userId = userId, title = title,
    content = content, summary = summary,
    convertedTo = convertedTo,
    createdAt = createdAt, updatedAt = updatedAt
)

fun Note.toEntity() = com.nadim.tasknova.data.local.NoteEntity(
    id = id, userId = userId, title = title,
    content = content, summary = summary,
    convertedTo = convertedTo,
    createdAt = createdAt, updatedAt = updatedAt
)