package com.nadim.tasknova.repository

import com.nadim.tasknova.data.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(userId: String): Flow<List<Note>>
    suspend fun saveNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
}