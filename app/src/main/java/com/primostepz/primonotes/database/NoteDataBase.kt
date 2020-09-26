package com.primostepz.primonotes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.primostepz.primonotes.dao.NoteDao
import com.primostepz.primonotes.entity.Note

//DataBase
@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NoteDataBase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}

object DataBaseProvider {
    private var noteDataBase : NoteDataBase? = null

    fun getDataBase(context: Context): NoteDataBase {
        if (noteDataBase == null) {
            noteDataBase = Room.databaseBuilder(
                context.applicationContext,
                NoteDataBase::class.java,
                "notes_db"
            )
                .build()
        }
        return noteDataBase as NoteDataBase
    }
}