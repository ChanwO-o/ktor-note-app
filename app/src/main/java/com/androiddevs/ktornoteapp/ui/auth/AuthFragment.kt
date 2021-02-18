package com.androiddevs.ktornoteapp.ui.auth

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.other.Status
import com.androiddevs.ktornoteapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auth.*

// inject ViewModel into this fragment
@AndroidEntryPoint
class AuthFragment: BaseFragment(R.layout.fragment_auth) {

	private val viewModel: AuthViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// disable landscape mode in this fragment
		requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
		subscribeToObservers()

		btnRegister.setOnClickListener {
			val email = etRegisterEmail.text.toString()
			val password = etRegisterPassword.text.toString()
			val confirmedPassword = etRegisterPasswordConfirm.text.toString()
			viewModel.register(email, password, confirmedPassword)
		}
	}

	// observe registration status from AuthViewModel
	private fun subscribeToObservers() {
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