package com.androiddevs.ktornoteapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androiddevs.ktornoteapp.data.local.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertNote(note: Note)

	@Query("DELETE FROM notes WHERE id = :noteId")
	suspend fun deleteNoteById(noteId: String)

	@Query("DELETE FROM notes WHERE isSynced = 1")
	suspend fun deleteAllSyncedNotes()

	// LiveData is already asynchronous by default. So this cannot be a suspend function
	@Query("SELECT * FROM notes WHERE id = :noteId")
	fun observeNoteById(noteId: String): LiveData<Note>

	@Query("SELECT * FROM notes WHERE id = :noteId")
	suspend fun getNoteById(noteId: String): Note?

	@Query("SELECT * FROM notes ORDER BY date DESC") // order by date, new notes on top
	fun getAllNotes(): Flow<List<Note>>

	@Query("SELECT * FROM notes WHERE isSynced = 0")
	suspend fun getAllUnsyncedNotes(): List<Note>
}