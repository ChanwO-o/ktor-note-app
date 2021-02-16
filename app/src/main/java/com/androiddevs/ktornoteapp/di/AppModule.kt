package com.androiddevs.ktornoteapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.androiddevs.ktornoteapp.data.local.NotesDatabase
import com.androiddevs.ktornoteapp.data.remote.BasicAuthInterceptor
import com.androiddevs.ktornoteapp.data.remote.NoteApi
import com.androiddevs.ktornoteapp.other.Constants.BASE_URL
import com.androiddevs.ktornoteapp.other.Constants.DATABASE_NAME
import com.androiddevs.ktornoteapp.other.Constants.ENCRYPTED_SHARED_PREF_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// holds functions that will create our dependencies
@Module
// modules must be installed in component that tells how long dependency will live
@InstallIn(ApplicationComponent::class) // everything here will live as long as our application does. other options: Activity, Service, Fragment etc
object AppModule {

	@Singleton // only a single instance of NotesDatabase will exist in app lifetime. Or else will create a new inst upon every injection
	@Provides // mark function as a dependency provider
	fun provideNotesDatabase(
		@ApplicationContext context: Context
	) = Room.databaseBuilder(context, NotesDatabase::class.java, DATABASE_NAME).build()

	@Singleton
	@Provides
	fun provideNoteDao(db: NotesDatabase) = db.noteDao()

	// why don't we just create AuthInterceptor inside providesNoteApi()? The constructor doesn't even need parameters
	// reason: the AuthInterceptor takes member variables(email & pw) which are assigned after login
	// after that, we need exactly that instance of AuthInterceptor.
	// if we create instance inside providesNoteApi(), we can't access the instance from the outside!
	// by creating this as a @provides function, we can inject our only instance of AuthInterceptor into our fragment later
	@Singleton
	@Provides
	fun provideBasicAuthInterceptor() = BasicAuthInterceptor()

	@Singleton
	@Provides
	fun provideNoteApi(basicAuthInterceptor: BasicAuthInterceptor) : NoteApi {
		// first create http client
		// every request with this client attached will have authorization header attached to it
		val client = OkHttpClient.Builder()
			.addInterceptor(basicAuthInterceptor)
			.build()
		return Retrofit.Builder()
			.baseUrl(BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.client(client)
			.build()
			.create(NoteApi::class.java)
	}

	@Singleton
	@Provides
	fun provideEncryptedSharedPreferences(
		@ApplicationContext context: Context
	): SharedPreferences {
		// master key: a key that is in the Android keystore needed to encrypt our data
		val masterKey = MasterKey.Builder(context)
			.setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
			.build()
		return EncryptedSharedPreferences.create(
			context,
			ENCRYPTED_SHARED_PREF_NAME, // filename for our shared preferences
			masterKey,
			EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
			EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
		)
	}
}