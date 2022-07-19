package uz.javokhirjambulov.notes.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.javokhirjambulov.notes.MainActivity
import uz.javokhirjambulov.notes.R
import uz.javokhirjambulov.notes.database.Note
import uz.javokhirjambulov.notes.databinding.ActivityNewNoteBinding
import java.text.SimpleDateFormat
import java.util.*


class NewNoteActivity : AppCompatActivity() {

//    private lateinit var uid: String
//    private lateinit var user: FirebaseUser
//    private lateinit var myRef: DatabaseReference
//    private lateinit var database: FirebaseDatabase
    private lateinit var binding:ActivityNewNoteBinding
    private lateinit var noteViewModel:NoteViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_new_note
        )
        binding.lifecycleOwner = this

//        user = Firebase.auth.currentUser!!
//        user?.let {
//
//            uid = user.uid
//        }
        /*database = Firebase.database
        myRef = database.getReference("Notes")*/
        val currentTime =System.currentTimeMillis()
        val outputDataFormat= SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        binding.tvDateTime.text =outputDataFormat.format(calendar.time)
        // Handle the cancel button
        binding.btnCancel.setOnClickListener {
            startMainActivity()
        }


        binding.btnOK.setOnClickListener {
            // Create a new note
            val newNote = Note(currentTime.toString())

            // Set its properties to match the
            // user's entries on the form
            newNote.title = binding.editTitle.text.toString()

            newNote.description = binding.editDescription.text.toString()

            newNote.idea = binding.checkBoxIdea.isChecked
            newNote.todo = binding.checkBoxTodo.isChecked
            newNote.important = binding.checkBoxImportant.isChecked


//            myRef.child(uid).child(newNote.noteId).setValue(newNote)
            noteViewModel.insert(newNote,applicationContext)
            //val callingActivity=activity as MainActivity?
            //callingActivity?.addNote(newNote)
            startMainActivity()

        }

    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }
}