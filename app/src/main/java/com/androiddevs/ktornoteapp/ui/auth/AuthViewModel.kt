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

	fun register(email: String, password: String, repeatedPassword: String) {
		// emit loading status
		_registerStatus.postValue(Resource.loading(null))

		if (email.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()) {
			_registerStatus.postValue(Resource.error("Please fill out all the fields", null))
			return
		}
		if (password != repeatedPassword) {
			_registerStatus.postValue(Resource.error("The passwords do not match", null))
			return
		}

		// launch coroutine inside of ViewModel scope
		viewModelScope.launch {
			val result = repository.register(email, password) // get result of Retrofit call
			_registerStatus.postValue(result)
		}

	}
}