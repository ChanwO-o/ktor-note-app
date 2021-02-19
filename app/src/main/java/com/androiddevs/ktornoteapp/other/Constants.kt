package com.androiddevs.ktornoteapp.other

object Constants {

	const val DATABASE_NAME = "notes_db"

	const val KEY_LOGGED_IN_EMAIL = "KEY_LOGGED_IN_EMAIL"

	const val KEY_LOGGED_IN_PASSWORD = "KEY_LOGGED_IN_PASSWORD"

	const val ENCRYPTED_SHARED_PREF_NAME = "enc_shared_pref"

	// ip of host machine running the ktor server
	const val BASE_URL = "http://10.0.2.2:8001"

	// API urls that don't need auth (anyone should be able to login/register)
	val IGNORE_AUTH_URLS = listOf("/login", "/register")
}