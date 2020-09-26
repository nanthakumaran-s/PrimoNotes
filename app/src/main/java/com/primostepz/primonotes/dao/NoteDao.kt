package com.primostepz.primonotes.dao

import androidx.room.*
import com.primostepz.primonotes.entity.Note

//Dao for DataBase
@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): MutableList<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: Note)

    @Delete
    fun deleteNote(note: Note)

}