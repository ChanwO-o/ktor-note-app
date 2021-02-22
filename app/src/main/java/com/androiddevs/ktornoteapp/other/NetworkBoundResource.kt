package com.androiddevs.ktornoteapp.other

import kotlinx.coroutines.flow.*

// inline: tells compiler to 'copy & paste' this code block where it's called
// this will be a generic implementation that we can use anywhere. 'How we want to load sth from db'
// we don't hardcode the logic to cache our notes
// <>: generic parameters. ResultType: from database (Note) RequestType: the response type received from API (could be anything)
inline fun <ResultType, RequestType> networkBoundResource(
	// crossinline: must use crossinline keyword for lambda function parameters in a inline function
	crossinline query: () -> Flow<ResultType>, // (lambda function) how we want to load something from database
	crossinline fetch: suspend () -> RequestType, // logic to get data from API
	crossinline saveFetchResult: suspend (RequestType) -> Unit, // once we have data using fetch function, use this to insert data into db. Unit: return nothing
	crossinline onFetchFailed: (Throwable) -> Unit = { Unit }, // logic when sth goes wrong while fetching. returns lambda function that returns nothing
	crossinline shouldFetch: (ResultType) -> Boolean = { true } // determine if we want to fetch data. always fetch data by default
) = flow {
	// emit(): emit a value to the outside as a LiveData object
	emit(Resource.loading(null)) // set loading first
	val data = query().first() // get data from db (first emission of that flow to data)


	// CREATING A NEW FLOW that will process the data
	val flow = if (shouldFetch(data)) {
		emit(Resource.loading(data))

		try {
			val fetchedResult = fetch() // e.g. fetch function could be NoteApi.getNotes()
			saveFetchResult(fetchedResult) // data that we fetched from API will be inserted into db

			// now we can call the new data from the database
			query().map { Resource.success(it) } // map the data around Resource.success. it: e.g. list of fetched notes
		} catch (t: Throwable) {
			onFetchFailed(t)
			query().map {
				Resource.error("Couldn't reach server. It might be down", it)
			}
		}
	} else { // not fetching data from API: just return data from database
		query().map { Resource.success(it) }
	}


	// emit everything we have inside the flow object we defined
	emitAll(flow)
}