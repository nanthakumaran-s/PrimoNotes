package com.primostepz.primonotes.activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.primostepz.primonotes.R
import com.primostepz.primonotes.adapter.NotesAdapter
import com.primostepz.primonotes.database.DataBaseProvider
import com.primostepz.primonotes.entity.Note
import com.primostepz.primonotes.listeners.NotesListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NotesListener {

    private lateinit var noteList : MutableList<Note>
    private lateinit var notesAdapter: NotesAdapter
    private var noteClickedPos : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageAddMain.setOnClickListener {
            startActivityForResult(
                Intent(applicationContext, CreateNoteActivity::class.java),
                REQUEST_CODE_FOR_ADD_NOTE
            )
        }

        notesRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        noteList = ArrayList()
        notesAdapter = NotesAdapter(noteList, this)
        notesRecyclerView.adapter = notesAdapter

        getNotes(REQUEST_CODE_FOR_SHOW_NOTE, false)

        inputSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                notesAdapter.cancelTimer()
            }

            override fun afterTextChanged(s: Editable?) {
                if (noteList.size != 0){
                    notesAdapter.searchNotes(s.toString())
                }
            }
        })
    }

    private fun getNotes(requestCode : Int, isNoteDeleted : Boolean){

        class GetNotes : AsyncTask<Void, Void, List<Note>>(){

            override fun doInBackground(vararg params: Void?): List<Note> {
                return DataBaseProvider.getDataBase(applicationContext).noteDao().getAllNotes()
            }

            override fun onPostExecute(result: List<Note>) {
                super.onPostExecute(result)
                if (requestCode == REQUEST_CODE_FOR_SHOW_NOTE){
                    noteList.addAll(result)
                    notesAdapter.notifyDataSetChanged()
                } else if (requestCode == REQUEST_CODE_FOR_ADD_NOTE){
                    noteList.add(0, result[0])
                    notesAdapter.notifyItemInserted(0)
                    notesRecyclerView.smoothScrollToPosition(0)
                } else if (requestCode == REQUEST_CODE_FOR_UPDATING_NOTES){
                    noteList.removeAt(noteClickedPos!!)

                    if (isNoteDeleted){
                        notesAdapter.notifyItemRemoved(noteClickedPos!!)
                    }else{
                        noteList.add(noteClickedPos!!, result[noteClickedPos!!])
                        notesAdapter.notifyItemChanged(noteClickedPos!!)
                    }
                }
            }

        }

        try {
            GetNotes().execute()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FOR_ADD_NOTE && resultCode == RESULT_OK){
            getNotes(REQUEST_CODE_FOR_ADD_NOTE, false)
        } else if(requestCode == REQUEST_CODE_FOR_UPDATING_NOTES && resultCode == RESULT_OK){
            if (data != null){
                getNotes(REQUEST_CODE_FOR_UPDATING_NOTES, data.getBooleanExtra("isNoteDeleted", false))
            }
        }
    }

    override fun oNoteClicked(note: Note, position: Int) {
        noteClickedPos = position
        val intent = Intent(applicationContext, CreateNoteActivity::class.java)
        intent.putExtra("isViewOrUpdate", true)
        intent.putExtra("note", note)
        intent.putExtra("noteID", note.id)
        intent.putExtra("noteTitle", note.title)
        intent.putExtra("noteSubTitle", note.subTitle)
        intent.putExtra("noteText", note.noteText)
        intent.putExtra("noteDateTime", note.dateTime)
        intent.putExtra("noteColor", note.color)
        intent.putExtra("noteWebLink", note.webLink)
        intent.putExtra("noteImage", note.imagePath)
        startActivityForResult(intent, REQUEST_CODE_FOR_UPDATING_NOTES)
    }

    companion object{
        const val REQUEST_CODE_FOR_ADD_NOTE: Int = 1
        const val REQUEST_CODE_FOR_UPDATING_NOTES = 2
        const val REQUEST_CODE_FOR_SHOW_NOTE = 3
    }
}