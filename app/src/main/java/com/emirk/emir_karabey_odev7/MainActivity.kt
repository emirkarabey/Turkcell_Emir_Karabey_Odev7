package com.emirk.emir_karabey_odev7

import android.app.DatePickerDialog
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDetail: EditText
    private lateinit var buttonAdd: Button
    private lateinit var buttonDate: Button
    private lateinit var listViewNotes: ListView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var selectedDate: String
    private lateinit var notesAdapter: ArrayAdapter<Note>
    private lateinit var notesList: ArrayList<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper = DatabaseHelper(this)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDetail = findViewById(R.id.editTextDetail)
        buttonAdd = findViewById(R.id.buttonAdd)
        buttonDate = findViewById(R.id.buttonDate)
        listViewNotes = findViewById(R.id.listViewNotes)

        notesList = ArrayList()
        notesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notesList)
        listViewNotes.adapter = notesAdapter

        buttonAdd.setOnClickListener {
            val title = editTextTitle.text.toString()
            val detail = editTextDetail.text.toString()
            val date = selectedDate
            val id = databaseHelper.addNote(title, detail, date)
            Log.d("MainActivity", "Eklendiğin verinin ID'si: $id")
            if (id > -1) {
                editTextTitle.text.clear()
                editTextDetail.text.clear()
                selectedDate = ""
                displayNotes()
            }
        }

        buttonDate.setOnClickListener {
            showDatePickerDialog()
        }

        listViewNotes.setOnItemClickListener { parent, view, position, id ->
            val note = notesList[position]
            val noteId = note.id
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("noteId", noteId)
            startActivity(intent)
        }

        displayNotes()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDate = format.format(selectedCalendar.time)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun displayNotes() {
        notesList.clear()
        val cursor = databaseHelper.getAllNotes()
        while (cursor.moveToNext()) {
            val noteId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE))
            val detail =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DETAIL))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE))
            val note = Note(noteId, title, detail, date)
            notesList.add(note)
        }
        cursor.close()
        notesAdapter.notifyDataSetChanged()
    }

    //bu activityden başka bir activitye geçip tekrar geri döndüğünde çağırılır
    // o yüzden displayNotes fonksionunu bir kere daha çağırıyoruz ve verileri güncelliyoruz.
    override fun onResume() {
        super.onResume()
        displayNotes()
    }
}
