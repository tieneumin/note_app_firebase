package com.example.noteappfirebase.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteappfirebase.core.crop
import com.example.noteappfirebase.data.model.Note
import com.example.noteappfirebase.databinding.ItemNoteBinding

class NoteAdapter(
    private var notes: List<Note>
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    private var clickListener: ClickListener? = null
    private var longClickListener: LongClickListener? = null

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

    fun setClickListener(listener: ClickListener) {
        clickListener = listener
    }

    interface ClickListener {
        fun onClickItem(item: Note)
    }

    fun setLongClickListener(listener: LongClickListener) {
        longClickListener = listener
    }

    interface LongClickListener {
        fun onLongClickItem(item: Note)
    }

    inner class NoteViewHolder(
        private val binding: ItemNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.run {
                tvTitle.text = note.title.crop(12)
                tvDesc.text = note.desc.crop(51)
                note.color?.let { mcvNote.setCardBackgroundColor(it) }

                root.setOnClickListener {
                    clickListener?.onClickItem(note)
                }

                root.setOnLongClickListener {
                    longClickListener?.onLongClickItem(note)
                    true
                }
            }
        }
    }
}