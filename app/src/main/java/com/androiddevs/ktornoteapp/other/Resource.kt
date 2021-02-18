package com.androiddevs.ktornoteapp.other

// set generic type T to wrap this class around any response
// keyword 'out': if T is assigned as Number, we can assign a Resource of type Integer since int inherits from number
data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
	companion object {
		fun <T> success(data: T?): Resource<T> { // data: data that is attached to the response
			return Resource(Status.SUCCESS, data, null)
		}
		fun <T> error(msg: String, data: T?): Resource<T> {
			return Resource(Status.ERROR, data, msg)
		}
		fun <T> loading(data: T?): Resource<T> {
			return Resource(Status.LOADING, data, null)
		}
	}
}

enum class Status {
	SUCCESS,
	ERROR,
	LOADING
}