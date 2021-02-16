package com.androiddevs.ktornoteapp.data.remote.requests

data class DeleteNoteRequest(
	// warning: name variables exactly as you want serialized to json!
	// server uses name 'id' so we call this 'id' too
	val id: String
)
