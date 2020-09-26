package com.primostepz.primonotes.listeners

import com.primostepz.primonotes.entity.Note

//Image Click Listener
interface NotesListener {
    fun oNoteClicked(note: Note, position: Int)
}