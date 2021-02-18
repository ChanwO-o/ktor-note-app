package com.androiddevs.ktornoteapp.other

// open class: can be inherited. Kotlin classes are final by default
open class Event<out T>(private val content: T) {

	// whether the event has been consumed or not. used for displaying snackbars once and once only
	var hasBeenHandled = false

	// allowed to change values from within this class only
	private set

	fun getContentIfNotHandled() = if(hasBeenHandled) {
		null
	} else { // set to true and emit content
		hasBeenHandled = true
		content
	}

	// get content
	fun peekContent() = content
}