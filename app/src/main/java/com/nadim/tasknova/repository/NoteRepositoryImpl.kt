package com.nadim.tasknova.repository

import com.nadim.tasknova.data.local.NoteDao
import com.nadim.tasknova.data.model.Note
import com.nadim.tasknova.data.model.toEntity
import com.nadim.tasknova.data.model.toModel
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val postgrest: Postgrest
) : NoteRepository {

    override fun getAllNotes(userId: String): Flow<List<Note>> =
        noteDao.getAllNotes(userId).map { it.map { e -> e.toModel() } }

    override suspend fun saveNote(note: Note) {
        val entity = note.copy(
            id = note.id.ifEmpty { UUID.randomUUID().toString() }
        ).toEntity()
        noteDao.insertNote(entity)
        try {
            postgrest.from("notes").upsert(
                mapOf(
                    "id"           to entity.id,
                    "user_id"      to entity.userId,
                    "title"        to entity.title,
                    "content"      to entity.content,
                    "summary"      to entity.summary,
                    "converted_to" to entity.convertedTo
                )
            )
        } catch (e: Exception) { }
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.toEntity())
        try {
            postgrest.from("notes").update(
                mapOf(
                    "title"        to note.title,
                    "content"      to note.content,
                    "summary"      to note.summary,
                    "converted_to" to note.convertedTo
                )
            ) { filter { eq("id", note.id) } }
        } catch (e: Exception) { }
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note.toEntity())
        try {
            postgrest.from("notes").delete { filter { eq("id", note.id) } }
        } catch (e: Exception) { }
    }
}