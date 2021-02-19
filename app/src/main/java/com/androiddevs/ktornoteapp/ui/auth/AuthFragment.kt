package com.androiddevs.ktornoteapp.ui.auth

import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.data.remote.BasicAuthInterceptor
import com.androiddevs.ktornoteapp.other.Constants
import com.androiddevs.ktornoteapp.other.Constants.KEY_LOGGED_IN_EMAIL
import com.androiddevs.ktornoteapp.other.Constants.KEY_LOGGED_IN_PASSWORD
import com.androiddevs.ktornoteapp.other.Constants.NO_EMAIL
import com.androiddevs.ktornoteapp.other.Constants.NO_PASSWORD
import com.androiddevs.ktornoteapp.other.Status
import com.androiddevs.ktornoteapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auth.*
import javax.inject.Inject

// inject ViewModel into this fragment
@AndroidEntryPoint
class AuthFragment: BaseFragment(R.layout.fragment_auth) {

	private val viewModel: AuthViewModel by viewModels()

	@Inject // AppModule only has one instance of SharedPref, so Dagger will automatically inject that one
	lateinit var sharedPref: SharedPreferences // for storing id & pw so that we can make other requests (that require auth)

	@Inject
	lateinit var basicAuthInterceptor: BasicAuthInterceptor // for authenticating email & pw

	private var curEmail: String? = null
	private var curPassword: String? = null

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (isLoggedIn()) {
			authenticateApi(curEmail ?: "", curPassword ?: "")
			redirectLogin()
		}

		// disable landscape mode in this fragment
		requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
		subscribeToObservers()

		btnRegister.setOnClickListener {
			val email = etRegisterEmail.text.toString()
			val password = etRegisterPassword.text.toString()
			val confirmedPassword = etRegisterPasswordConfirm.text.toString()
			viewModel.register(email, password, confirmedPassword)
		}

		btnLogin.setOnClickListener {
			val email = etLoginEmail.text.toString()
			val password = etLoginPassword.text.toString()
			curEmail = email
			curPassword = password
			viewModel.login(email, password)
		}
	}

	private fun isLoggedIn(): Boolean {
		curEmail = sharedPref.getString(KEY_LOGGED_IN_EMAIL, NO_EMAIL) ?: NO_EMAIL
		curPassword = sharedPref.getString(KEY_LOGGED_IN_PASSWORD, NO_PASSWORD) ?: NO_PASSWORD
		return curEmail != NO_EMAIL && curPassword != NO_PASSWORD
	}

	private fun authenticateApi(email: String, password: String) {
		basicAuthInterceptor.email = email
		basicAuthInterceptor.password = password
	}

	// redirect to NoteFragment after login, then pop backstack so that user can't return back to AuthFragment
	private fun redirectLogin() {
		val navOptions = NavOptions.Builder()
			.setPopUpTo(R.id.authFragment, true) // pop up to AuthFragment
			.build()
		findNavController().navigate(
			AuthFragmentDirections.actionAuthFragmentToNotesFragment(),
			navOptions
		)
	}

	// observe registration status from AuthViewModel
	private fun subscribeToObservers() {
		viewModel.loginStatus.observe(viewLifecycleOwner, Observer { result ->
			result?.let {
				when(result.status) {
					Status.SUCCESS -> {
						loginProgressBar.visibility = View.GONE
						showSnackbar(result.data ?: "Successfully logged in")
						sharedPref.edit().putString(Constants.KEY_LOGGED_IN_EMAIL, curEmail).apply()
						sharedPref.edit().putString(Constants.KEY_LOGGED_IN_PASSWORD, curPassword).apply()

						authenticateApi(curEmail ?: "", curPassword ?: "")
						redirectLogin()
					}
					Status.ERROR -> {
						loginProgressBar.visibility = View.GONE
						showSnackbar(result.message ?: "An unknown error occurred")
					}
					Status.LOADING -> {
						loginProgressBar.visibility = View.VISIBLE
					}
				}
			}
		})
		viewModel.registerStatus.observe(viewLifecycleOwner, Observer { result ->
			result?.let {
				when(result.status) {
					Status.SUCCESS -> {
						registerProgressBar.visibility = View.GONE
						showSnackbar(result.data ?: "Successfully registered an account")
					}
					Status.ERROR -> {
						registerProgressBar.visibility = View.GONE
						showSnackbar(result.message ?: "An unknown error occurred") // when error, response only has a message and no data
					}
					Status.LOADING -> {
						registerProgressBar.visibility = View.VISIBLE
					}
				}
			}
		})
	}
}