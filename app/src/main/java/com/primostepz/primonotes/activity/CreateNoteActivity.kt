package com.primostepz.primonotes.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.primostepz.primonotes.R
import com.primostepz.primonotes.database.DataBaseProvider.getDataBase
import com.primostepz.primonotes.entity.Note
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.layout_add_url.view.*
import kotlinx.android.synthetic.main.layout_actions.*
import kotlinx.android.synthetic.main.layout_delete_note.*
import kotlinx.android.synthetic.main.layout_delete_note.view.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
class CreateNoteActivity : AppCompatActivity() {
    private var id: Int = 0
    private lateinit var selectedNoteColor : String
    private lateinit var selectedImagePath : String
    private lateinit var layoutActions : View
    private lateinit var bottomSheetBehaviour : BottomSheetDialog
    private lateinit var dialogAddUrl : Dialog
    private lateinit var dialogDelete : Dialog
    private var webLink : String? = null
    private var alreadyAvailableNote = false

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        imageBack.setOnClickListener {
            onBackPressed()
        }

        textDateTime.text =
            SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())

        imageSave.setOnClickListener {
            saveNote()
        }

        layoutActions = layoutInflater.inflate(R.layout.layout_actions, null)
        bottomSheetBehaviour  = BottomSheetDialog(this)
        bottomSheetBehaviour.setContentView(layoutActions)
        bottomSheetBehaviour.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        selectedNoteColor = "#333333"
        setSubTitleIndicatorColor()

        selectedImagePath = ""

        color1.setOnClickListener {
            selectedNoteColor = "#333333"
            imageColor1.setImageResource(R.drawable.ic_done)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            imageColor6.setImageResource(0)
            setSubTitleIndicatorColor()
        }

        color2.setOnClickListener {
            selectedNoteColor = "#3369FF"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(R.drawable.ic_done)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            imageColor6.setImageResource(0)
            setSubTitleIndicatorColor()
        }

        color3.setOnClickListener {
            selectedNoteColor = "#FF493E"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(R.drawable.ic_done)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            imageColor6.setImageResource(0)
            setSubTitleIndicatorColor()
        }

        color4.setOnClickListener {
            selectedNoteColor = "#AE3B76"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(R.drawable.ic_done)
            imageColor5.setImageResource(0)
            imageColor6.setImageResource(0)
            setSubTitleIndicatorColor()
        }

        color5.setOnClickListener {
            selectedNoteColor = "#000000"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(R.drawable.ic_done)
            imageColor6.setImageResource(0)
            setSubTitleIndicatorColor()
        }

        color6.setOnClickListener {
            selectedNoteColor = "#FF7746"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            imageColor6.setImageResource(R.drawable.ic_done)
            setSubTitleIndicatorColor()
        }

        layoutAddImage.setOnClickListener {
            if (isReadStorageAllowed()){
                selectImage()
            }else{
                requestStoragePermission()
            }
        }

        layoutAddUrl.setOnClickListener{
            showAddUrlDialog()
        }

        if (intent.getBooleanExtra("isViewOrUpdate", false)){
            alreadyAvailableNote = true
            layoutDeleteNote.visibility = View.VISIBLE
            setViewOrUpdate()
        }

        imageRemoveWebUrl.setOnClickListener {
            textWebUrl.text = null
            webLink = null
            layout_web_url.visibility = View.GONE
        }

        imageRemoveImage.setOnClickListener {
            imageNote.setImageBitmap(null)
            imageNote.visibility = View.GONE
            imageRemoveImage.visibility = View.GONE
            selectedImagePath = ""
        }

        layoutDeleteNote.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun setViewOrUpdate(){
        inputNoteTitle.text = intent.getStringExtra("noteTitle").toEditable()
        inputNoteSubTitle.text = intent.getStringExtra("noteSubTitle").toEditable()
        inputNotes.text = intent.getStringExtra("noteText").toEditable()
        textDateTime.text = intent.getStringExtra("noteDateTime").toEditable()

        if (intent.getStringExtra("noteImage") != null && intent.getStringExtra("noteImage").trim().isNotEmpty()){
            imageNote.setImageBitmap(BitmapFactory.decodeFile(intent.getStringExtra("noteImage")))
            imageNote.visibility = View.VISIBLE
            imageRemoveImage.visibility = View.VISIBLE
            selectedImagePath = intent.getStringExtra("noteImage")
        }
        if (intent.getStringExtra("noteWebLink") != null && intent.getStringExtra("noteWebLink").trim().isNotEmpty()){
            textWebUrl.text = intent.getStringExtra("noteWebLink")
            layout_web_url.visibility = View.VISIBLE
        }
        selectedNoteColor = intent.getStringExtra("noteColor")
        setSubTitleIndicatorColor()
        when(selectedNoteColor){
            "#3369FF" -> color2.performClick()
            "#FF493E" -> color3.performClick()
            "#AE3B76" -> color4.performClick()
            "#000000" -> color5.performClick()
            "#FF7746" -> color6.performClick()
        }
    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    private fun saveNote(){
        if(inputNoteTitle.text.isEmpty()){
            Toast.makeText(this, "Note title can't be empty", Toast.LENGTH_SHORT).show()
        }else if (inputNoteSubTitle.text.isEmpty() ) {
            Toast.makeText(this, "Give a Sub Title for your note...", Toast.LENGTH_SHORT).show()
        }else if (inputNotes.text.isEmpty()){
            Toast.makeText(this, "There's nothing to save in your note! Fill something...", Toast.LENGTH_SHORT).show()
        }else {

            if (layout_web_url.visibility == View.VISIBLE){
                webLink = textWebUrl.text.toString()
            }
            if (alreadyAvailableNote){
                id = intent.getIntExtra("noteID", 0)
            }

            val note = Note(
                id = id.also {
                    if (!alreadyAvailableNote) {
                        id++
                    }
                },
                title = inputNoteTitle.text.toString(),
                subTitle = inputNoteSubTitle.text.toString(),
                noteText = inputNotes.text.toString(),
                dateTime = textDateTime.text.toString(),
                color = selectedNoteColor,
                imagePath = selectedImagePath,
                webLink = webLink
            )

            class SaveNoteTask : AsyncTask<Void, Void, Void>() {

                override fun doInBackground(vararg params: Void?): Void? {
                    getDataBase(applicationContext).noteDao().insertNote(note)
                    return null
                }

                override fun onPostExecute(result: Void?) {
                    super.onPostExecute(result)
                    setResult(RESULT_OK, Intent())
                    finish()
                }
            }

            SaveNoteTask().execute()
        }
    }

    private fun setSubTitleIndicatorColor(){
        viewSubTitleIndicator.setBackgroundColor(Color.parseColor(selectedNoteColor))
    }

    private fun selectImage(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY)
    }

    private fun showAddUrlDialog(){
        val dialogView = layoutInflater.inflate(R.layout.layout_add_url, null)
        dialogAddUrl = AlertDialog.Builder(this@CreateNoteActivity)
            .setView(dialogView)
            .setCancelable(false)
            .show()

        dialogAddUrl.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.inputUrl.requestFocus()
        dialogView.textCancel.setOnClickListener {
            dialogAddUrl.dismiss()
        }
        dialogView.textAdd.setOnClickListener {
            if (dialogView.inputUrl.text.isEmpty()){
                Toast.makeText(this@CreateNoteActivity, "Enter A URL", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.WEB_URL.matcher(dialogView.inputUrl.text.toString()).matches()){
                Toast.makeText(this@CreateNoteActivity, "Enter a valid URL", Toast.LENGTH_SHORT).show()
            } else{
                textWebUrl.text = dialogView.inputUrl.text
                layout_web_url.visibility = View.VISIBLE
                dialogAddUrl.dismiss()
            }
        }
    }

    private fun showDeleteDialog(){
        val dialogView = layoutInflater.inflate(R.layout.layout_delete_note, null)
        dialogDelete = AlertDialog.Builder(this@CreateNoteActivity)
            .setView(dialogView)
            .setCancelable(false)
            .show()

        dialogDelete.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogView.textCancelInDeleteDialog.setOnClickListener {
            dialogDelete.dismiss()
        }
        dialogDelete.textDelete.setOnClickListener {
            class DeleteNoteTask : AsyncTask<Void, Void, Void>() {
                override fun doInBackground(vararg params: Void?): Void? {
                    getDataBase(applicationContext).noteDao().deleteNote(intent.getSerializableExtra("note") as Note)
                    return null
                }

                override fun onPostExecute(result: Void?) {
                    super.onPostExecute(result)
                    val intent = Intent()
                    intent.putExtra("isNoteDeleted", true)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
            DeleteNoteTask().execute()
        }
        dialogDelete.show()
    }

    private fun getPathUri(contentUri : Uri): String {
        lateinit var filePath: String
        val cursor: Cursor? = contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null){
            filePath = contentUri.path.toString()
        }else{
            cursor.moveToFirst()
            val index: Int = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY && resultCode == RESULT_OK){
            if (data != null){
                val selectedImageUri = data.data
                if (selectedImageUri != null){
                    try {
                        val inputStream = contentResolver.openInputStream(selectedImageUri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        imageNote.setImageBitmap(bitmap)
                        imageNote.visibility = View.VISIBLE

                        selectedImagePath = getPathUri(selectedImageUri)
                        imageRemoveImage.visibility = View.VISIBLE

                    } catch (e: Exception){
                        e.printStackTrace()
                        Toast.makeText(applicationContext, "Some Error Occurred. Try Again", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(
                    applicationContext,
                    "Permission Granted, You can add Image now",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                Toast.makeText(
                    applicationContext,
                    "Permission Denied. You can change in settings",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE).toString())){
            Toast.makeText(this, "Need Permission to add Gallery Image", Toast.LENGTH_SHORT).show()
        }
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_CODE)
    }

    private fun isReadStorageAllowed(): Boolean{
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) == PackageManager.PERMISSION_GRANTED
    }

    companion object{
        const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 2
    }
}
