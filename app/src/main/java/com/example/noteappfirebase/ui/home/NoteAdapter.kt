package com.example.noteappfirebase.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteappfirebase.core.crop
import com.example.noteappfirebase.data.model.Note
import com.example.noteappfirebase.databinding.ItemNoteBinding

class NoteAdapter(
    private var notes: List<Note>
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNoteBinding.inflate(inflater, parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
    }

    override fun getItemCount() = notes.size

    fun setNotes(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    inner class NoteViewHolder(
        private val binding: ItemNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.run {
                tvTitle.text = note.title.crop(12)
                tvDesc.text = note.desc.crop(74)
                mcvNote.setCardBackgroundColor(note.color)
                mcvNote.setOnClickListener { listener?.onClickNote(note.id!!) }
                mcvNote.setOnLongClickListener {
                    listener?.onLongClickNote(note.id!!)
                    true
                }
            }
        }
    }

    interface Listener {
        fun onClickNote(id: String)
        fun onLongClickNote(id: String)
    }
}