package com.androiddevs.ktornoteapp.ui.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.ktornoteapp.other.Resource
import com.androiddevs.ktornoteapp.repositories.NoteRepository
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
	private val repository: NoteRepository
): ViewModel() {

	// _registerStatus: allow edits inside this class
	private val _registerStatus = MutableLiveData<Resource<String>>() // underscore: convention for private data
	// registerStatus: can't edit outside this class
	val registerStatus: LiveData<Resource<String>> = _registerStatus

	private val _loginStatus = MutableLiveData<Resource<String>>()
	val loginStatus: LiveData<Resource<String>> = _loginStatus

	fun register(email: String, password: String, repeatedPassword: String) {
		_registerStatus.postValue(Resource.loading(null)) // emit loading status

		if (email.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()) {
			_registerStatus.postValue(Resource.error("Please fill out all the fields", null))
			return
		}
		if (password != repeatedPassword) {
			_registerStatus.postValue(Resource.error("The passwords do not match", null))
			return
		}

		// can add more conditions here e.g. password must be > 8, contain special character, etc.

		// launch coroutine inside of ViewModel scope
		viewModelScope.launch {
			val result = repository.register(email, password) // get result of Retrofit call
			_registerStatus.postValue(result)
		}

	}

	fun login(email: String, password: String) {
		_loginStatus.postValue(Resource.loading(null)) // emit loading status

		if (email.isEmpty() || password.isEmpty()) {
			_loginStatus.postValue(Resource.error("Please fill out all the fields", null))
			return
		}

		viewModelScope.launch { // launch coroutine inside of ViewModel scope
			val result = repository.login(email, password) // get result of Retrofit call
			_loginStatus.postValue(result)
		}

	}
}