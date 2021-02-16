package com.androiddevs.ktornoteapp.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// used to translate json strings to list of strings
class Converters {

	@TypeConverter
	fun fromList(list: List<String>): String {
		return Gson().toJson(list)
	}

	@TypeConverter
	fun toList(string: String): List<String> {
		// tell Gson library which type we want to convert to
		return Gson().fromJson(string, object : TypeToken<List<String>>() {}.type)
	}
}