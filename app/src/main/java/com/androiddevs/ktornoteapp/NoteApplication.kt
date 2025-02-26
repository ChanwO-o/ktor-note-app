package com.androiddevs.ktornoteapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class NoteApplication : Application() {

	override fun onCreate() {
		super.onCreate()

		// enable debug logs
		Timber.plant(Timber.DebugTree())


	}
}