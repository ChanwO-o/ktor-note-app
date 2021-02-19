package com.androiddevs.ktornoteapp.data.remote

import com.androiddevs.ktornoteapp.other.Constants
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

// interceptor: tells Retrofit to modify every outgoing request slightly in some way
class BasicAuthInterceptor : Interceptor {

	var email: String? = null
	var password: String? = null

	// chain: collection of several interceptors to the http client. each will change our request
	// each interceptor will return a Response, either for the next interceptor in the chain, or our ktor server
	override fun intercept(chain: Interceptor.Chain): Response {
		val request = chain.request()

		// we don't want to add this authentication to every request because /register and /login don't need auth
		if (request.url.encodedPath in Constants.IGNORE_AUTH_URLS) {
			return chain.proceed(request) // pass request to next interceptor or server
		}

		// add authentication header to our request
		val authenticatedRequest = request.newBuilder() // .newBuilder(): modify this request
			.header(
				// authorization: are you allowed to make request? authentication: make sure that you are actually you
				"Authorization",
				// we use basic authentication
				Credentials.basic(email ?: "", password ?: "") // empty strings if email/pw are null
			)
			.build()
		return chain.proceed(authenticatedRequest)
	}
}