package com.androiddevs.ktornoteapp.ui.notes

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.adapters.NoteAdapter
import com.androiddevs.ktornoteapp.other.Constants.KEY_LOGGED_IN_EMAIL
import com.androiddevs.ktornoteapp.other.Constants.KEY_LOGGED_IN_PASSWORD
import com.androiddevs.ktornoteapp.other.Constants.NO_EMAIL
import com.androiddevs.ktornoteapp.other.Constants.NO_PASSWORD
import com.androiddevs.ktornoteapp.other.Status
import com.androiddevs.ktornoteapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_notes.*
import javax.inject.Inject

@AndroidEntryPoint
class NotesFragment: BaseFragment(R.layout.fragment_notes) {

	private val viewModel: NotesViewModel by viewModels()

	@Inject
	lateinit var sharedPref: SharedPreferences

	private lateinit var noteAdapter: NoteAdapter

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		setHasOptionsMenu(true) // implement menu on fragment
		return super.onCreateView(inflater, container, savedInstanceState)

		// re-enable screen rotation: we can't do it directly inside a fragment. so
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// in AuthFragment we disabled screen rotation. Now we want to re-enable it
		requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER

		setupRecyclerView()
		subscribeToObservers()

		noteAdapter.setOnItemClickListener {
			findNavController().navigate(
				NotesFragmentDirections.actionNotesFragmentToNoteDetailFragment(it.id)
			)
		}

		fabAddNote.setOnClickListener {
			findNavController().navigate(NotesFragmentDirections.actionNotesFragmentToAddEditNoteFragment(""))
		}
	}

	private fun subscribeToObservers() {
		viewModel.allNotes.observe(viewLifecycleOwner, Observer {
			it?.let { event ->
				// save content of the event to variable
				val result = event.peekContent() // use peekContent() so that we don't consume the event
				when (result.status) {
					Status.SUCCESS -> {
						noteAdapter.notes = result.data!! // assume != null since Success state never gives null
						swipeRefreshLayout.isRefreshing = false // stop loading
					}
					Status.ERROR -> {
						// this is the only scenario where we want to consume the event (show on snackbar)
						event.getContentIfNotHandled()?.let { errorResource ->
							// we only go inside this let block the first time we get an Error resource
							errorResource.message?.let { message ->
								showSnackbar(message)
							}
						}
						// when fetch data fails, we still get data from db
						result.data?.let { notes ->
							noteAdapter.notes = notes
						}
						swipeRefreshLayout.isRefreshing = false // stop loading
					}
					Status.LOADING -> {
						result.data?.let { notes ->
							// when we fetch data, we already got data from database, so we can display it even though we're still loading
							noteAdapter.notes = notes
						}
						swipeRefreshLayout.isRefreshing = true
					}
				}
			}
		})
	}

	private fun setupRecyclerView() = rvNotes.apply {
		noteAdapter = NoteAdapter()
		adapter = noteAdapter
		layoutManager = LinearLayoutManager(requireContext())
	}

	// navigate back to AuthFragment, delete login entry in SharedPreferences
	private fun logout() {
		sharedPref.edit().putString(KEY_LOGGED_IN_EMAIL, NO_EMAIL).apply()
		sharedPref.edit().putString(KEY_LOGGED_IN_PASSWORD, NO_PASSWORD).apply()
		// pop everything except AuthFragment
		val navOptions = NavOptions.Builder()
			.setPopUpTo(R.id.notesFragment, true)
			.build()
		findNavController().navigate(
			NotesFragmentDirections.actionNotesFragmentToAuthFragment(),
			navOptions
		)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.menu_notes, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.miLogout -> logout()
		}
		return super.onOptionsItemSelected(item)
	}
}