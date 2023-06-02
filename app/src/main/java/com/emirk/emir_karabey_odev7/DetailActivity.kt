package com.emirk.emir_karabey_odev7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class DetailActivity : AppCompatActivity() {
    private lateinit var buttonDelete: Button
    private lateinit var textViewTitle: TextView
    private lateinit var textViewDetail: TextView
    private lateinit var textViewDate: TextView
    private lateinit var databaseHelper: DatabaseHelper
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        databaseHelper = DatabaseHelper(this)

        buttonDelete = findViewById(R.id.buttonDelete)
        textViewTitle = findViewById(R.id.textViewTitle)
        textViewDetail = findViewById(R.id.textViewDetail)
        textViewDate = findViewById(R.id.textViewDate)

        buttonDelete.setOnClickListener {
            deleteNote()
        }

        val intent = intent
        noteId = intent.getIntExtra("noteId", -1)

        if (noteId != -1) {
            val note = databaseHelper.getNoteById(noteId)
            if (note != null) {
                textViewTitle.text = note.title
                textViewDetail.text = note.detail
                textViewDate.text = note.date
            }
        }
    }

    private fun deleteNote() {
        if (noteId != -1) {
            val deletedRows = databaseHelper.deleteNoteById(noteId)
            if (deletedRows > 0) {
                Toast.makeText(applicationContext, "Başarıyla silindi", Toast.LENGTH_SHORT).show()
            }
        }
        finish()
    }

}