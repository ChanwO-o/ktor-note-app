package com.androiddevs.ktornoteapp.data.remote.requests

// request type for logins & account creation
data class AccountRequest(
	val email: String,
	val password: String
)
