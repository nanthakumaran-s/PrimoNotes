package com.primostepz.primonotes.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//Entities for the DataBase
@Entity(tableName = "notes")
data class Note (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "title") val  title: String,
    @ColumnInfo(name = "date_time") val  dateTime: String,
    @ColumnInfo(name = "subtitle") val  subTitle: String,
    @ColumnInfo(name = "note_text") val  noteText: String,
    @ColumnInfo(name = "image_path") val  imagePath: String?,
    @ColumnInfo(name = "color") val  color: String,
    @ColumnInfo(name = "web_link") val  webLink: String?
): Serializable{
    @NonNull
    override fun toString(): String {
        return "$title : $dateTime"
    }
}