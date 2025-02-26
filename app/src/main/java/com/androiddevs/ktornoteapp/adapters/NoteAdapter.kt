package com.androiddevs.ktornoteapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.data.local.entities.Note
import kotlinx.android.synthetic.main.item_note.view.*
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

	inner class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

	private val diffCallback = object : DiffUtil.ItemCallback<Note>() {
		override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
			return oldItem.id == newItem.id // notes are unique by id
		}

		override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
			return oldItem.hashCode() == newItem.hashCode() // if contents are same, hashcode will be the same
		}
	}

	private var onItemClickListener: ((Note) -> Unit)? = null // constructor can be null, set to null initially

	private val differ = AsyncListDiffer(this, diffCallback) // async so that it does its job in the background

	var notes: List<Note>
		get() = differ.currentList // inline getter & setter
		set(value) = differ.submitList(value)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
		return NoteViewHolder(
			LayoutInflater.from(parent.context).inflate(
				R.layout.item_note,
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
		val note = notes[position]
		holder.itemView.apply {
			tvTitle.text = note.title
			if (!note.isSynced) {
				ivSynced.setImageResource(R.drawable.ic_cross)
				tvSynced.text = "Not Synced"
			} else {
				ivSynced.setImageResource(R.drawable.ic_check)
				tvSynced.text = "Synced"
			}

			val dateFormat = SimpleDateFormat("dd.MM.yy, HH:mm", Locale.getDefault()) // get timezone from android device
			val dateString = dateFormat.format(note.date)
			tvDate.text = dateString

			// set note color
			val drawable = ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null)
			drawable?.let {
				val wrappedDrawable = DrawableCompat.wrap(it)
				val color = Color.parseColor("#${note.color}")
				DrawableCompat.setTint(wrappedDrawable, color)
				viewNoteColor.background = it
			}

			// assign an OnClickListener to the itemview, trigger listener
			setOnClickListener {
				onItemClickListener?.let { click ->
					click(note)
				}
			}
		}
	}

	override fun getItemCount(): Int {
		return notes.size
	}

	fun setOnItemClickListener(onItemClickListener: (Note) -> Unit) {
		this.onItemClickListener = onItemClickListener
	}
}