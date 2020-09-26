package com.primostepz.primonotes.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.primostepz.primonotes.R
import com.primostepz.primonotes.entity.Note
import com.primostepz.primonotes.listeners.NotesListener
import java.util.*
import kotlin.collections.ArrayList


class NotesAdapter : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>{
    var notes: List<Note>
    var notesListener : NotesListener
    var timer : Timer? = null
    var noteSource : List<Note>

    constructor(notes: List<Note>, notesListener : NotesListener) {
        this.notes = notes
        this.notesListener = notesListener
        noteSource = notes
    }

    class NotesViewHolder : RecyclerView.ViewHolder{
        var textTitle : TextView
        var textSubtitle : TextView
        var textDateTime : TextView
        var layoutNote : LinearLayout
        var imageNote : RoundedImageView

        constructor(itemView: View) : super(itemView) {
            textTitle = itemView.findViewById(R.id.textTitle)
            textSubtitle = itemView.findViewById(R.id.textSubTitle)
            textDateTime = itemView.findViewById(R.id.textDateTime)
            layoutNote = itemView.findViewById(R.id.layoutNote)
            imageNote = itemView.findViewById(R.id.noteImage)
        }

        fun setNote(note: Note){
            textTitle.text = note.title
            if (note.subTitle.trim().isEmpty()){
                textSubtitle.visibility = View.GONE
            } else{
                textSubtitle.text = note.subTitle
            }
            textDateTime.text = note.dateTime

            layoutNote.setBackgroundColor(Color.parseColor(note.color))

            if (note.imagePath != null){
                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.imagePath))
                imageNote.visibility = View.VISIBLE
            }else{
                imageNote.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_container_note,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.setNote(notes[position])
        holder.layoutNote.setOnClickListener {
            notesListener.oNoteClicked(notes[position], position)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun searchNotes(searchKeyWord : String){
        timer = Timer()
        timer!!.schedule(object: TimerTask(){
            override fun run() {
                notes = if (searchKeyWord.trim().isEmpty()){
                    noteSource
                } else{
                    val temp = ArrayList<Note>()
                    for (note:Note in noteSource){
                        if (note.title.toLowerCase().contains(searchKeyWord.toLowerCase())
                            || note.subTitle.toLowerCase().contains(searchKeyWord.toLowerCase())
                            || note.dateTime.toLowerCase().contains(searchKeyWord.toLowerCase())){
                            temp.add(note)
                        }
                    }
                    temp
                }

                Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }

            }
        }, 500)
    }

    fun cancelTimer(){
        if (timer != null){
            timer!!.cancel()
        }
    }
}