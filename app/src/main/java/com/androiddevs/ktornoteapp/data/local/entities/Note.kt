package com.androiddevs.ktornoteapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import java.util.*

@Entity(tableName = "notes")
data class Note(
	val title: String,
	val content: String,
	val date: Long,
	val owners: List<String>,
	val color: String,

	// client-side data that tells whether this note is synced with remote db
	// this field shouldn't be included in the request
	@Expose(deserialize = false, serialize = false)
	val isSynced: Boolean = false,

	@PrimaryKey(autoGenerate = false) // generate new id like this manually
	val id: String = UUID.randomUUID().toString()
)