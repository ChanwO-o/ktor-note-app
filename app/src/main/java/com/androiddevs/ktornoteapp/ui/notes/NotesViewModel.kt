package com.androiddevs.ktornoteapp.ui.notes

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.androiddevs.ktornoteapp.data.local.entities.Note
import com.androiddevs.ktornoteapp.other.Event
import com.androiddevs.ktornoteapp.other.Resource
import com.androiddevs.ktornoteapp.repositories.NoteRepository


// getting all notes via this ViewModel:
// 1. we want to retrieve the data once, so that we don't make the request again when the device is rotated
// 2. we want to be able to sync when we want, where we will be making a request
// solution: use _forceUpdate: post a value to _forceUpdate to get actual LiveData (the Flow LD that returns List of Notes)

class NotesViewModel @ViewModelInject constructor(
	private val repository: NoteRepository
): ViewModel() {

	private val _forceUpdate = MutableLiveData<Boolean>(false)

	private val _allNotes = _forceUpdate.switchMap {
		// whenever a value is posted to _forceUpdate, emit this block
		repository.getAllNotes().asLiveData(viewModelScope.coroutineContext)
		// to make sure this gets triggered once, we wrap it around our Event class (to handle one-time events)
		// to do this, use another switchMap on the LiveData again!
	}.switchMap {
		// map to MutableLD of type event
		MutableLiveData(Event(it)) // 'it' refers to the resource of list of Notes from the database
	}
	// now make an immutable version of that LD
	val allNotes: LiveData<Event<Resource<List<Note>>>> = _allNotes

	fun syncAllNotes() = _forceUpdate.postValue(true)
}