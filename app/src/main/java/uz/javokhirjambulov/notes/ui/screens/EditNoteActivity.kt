package uz.javokhirjambulov.notes.ui.screens

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.javokhirjambulov.notes.MainActivity
import uz.javokhirjambulov.notes.R
import uz.javokhirjambulov.notes.database.Note
import uz.javokhirjambulov.notes.database.NoteDatabase
import uz.javokhirjambulov.notes.databinding.ActivityNewNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class EditNoteActivity : AppCompatActivity() {

    private lateinit var value: String
    private lateinit var binding: ActivityNewNoteBinding
    private lateinit var noteViewModel:NoteViewModel
    private var editNote: Note? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteViewModel = ViewModelProvider(this,  NoteViewModelFactory(NoteDatabase.getDataBase()))[NoteViewModel::class.java]
        val b = intent.extras
        value = "" // or other values

        if (b != null)
            value = b.getString("key").toString()


        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_new_note
        )
        binding.lifecycleOwner = this
        Log.i("Tag", value)
        sendNoteSelected(value)


        // Handle the cancel button
        binding.btnCancel.setOnClickListener {
            editNote?.let { it1 -> startShowActivity(it1.noteId) }
        }

        binding.btnOK.setOnClickListener {
            // Create a new note
            val newNote = editNote?.let { it1 -> Note(it1.noteId) }

            // Set its properties to match the
            // user's entries on the form
            newNote?.title = binding.editTitle.text.toString()

            newNote?.description = binding.editDescription.text.toString()

            newNote?.idea = binding.checkBoxIdea.isChecked
            newNote?.todo = binding.checkBoxTodo.isChecked
            newNote?.important = binding.checkBoxImportant.isChecked

            newNote?.let { it1 ->
                noteViewModel.update(it1)
                // Quit the dialog
                startShowActivity(it1.noteId)
            }




        }

    }

    private fun startShowActivity(noteToShow: String) {
        val intent = Intent(this, ShowNoteActivity::class.java)
        val b = Bundle()
        b.putString("key", noteToShow) //Your id

        intent.putExtras(b) //Put your id to your next Intent

        startActivity(intent)
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }
    // Receive a note from the MainActivity class
    private fun sendNoteSelected(value: String) {
        lifecycleScope.launch(Dispatchers.IO){
            Log.i("Tag","note${noteViewModel.getNoteWithID(value)}")
            setNote(noteViewModel.getNoteWithID(value))
        }

    }

    private fun setNote(note: Note) {
        editNote = note
        Log.i("Tag", editNote?.description.toString())

        editNote?.noteId.let { it1 ->
            if (it1 != null) {
                val outputDataFormat= SimpleDateFormat("dd-MM-yyyy", Locale.US)
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = it1.toLong()
                binding.tvDateTime.text =outputDataFormat.format(calendar.time)
                binding.editTitle.setText(editNote?.title)
                binding.editDescription.setText(editNote?.description)
                binding.checkBoxIdea.isChecked = editNote?.idea == true
                binding.checkBoxTodo.isChecked = editNote?.todo == true
                binding.checkBoxImportant.isChecked = editNote?.important == true

            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

}