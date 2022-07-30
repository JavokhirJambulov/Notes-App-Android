package uz.javokhirjambulov.notes.ui.screens

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.javokhirjambulov.notes.MainActivity
import uz.javokhirjambulov.notes.R
import uz.javokhirjambulov.notes.commons.Dialog
import uz.javokhirjambulov.notes.database.Note
import uz.javokhirjambulov.notes.database.NoteDatabase
import uz.javokhirjambulov.notes.databinding.ActivityNewNoteBinding
import java.text.SimpleDateFormat
import java.util.*


class NewNoteActivity : AppCompatActivity() {

    //    private lateinit var uid: String
//    private lateinit var user: FirebaseUser
//    private lateinit var myRef: DatabaseReference
//    private lateinit var database: FirebaseDatabase
    private lateinit var binding: ActivityNewNoteBinding
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noteViewModel = ViewModelProvider(
            this,
            NoteViewModelFactory(NoteDatabase.getDataBase())
        )[NoteViewModel::class.java]

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_new_note
        )
        binding.lifecycleOwner = this
        val currentTime = System.currentTimeMillis()
        val outputDataFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        binding.tvDateTime.text = outputDataFormat.format(calendar.time)
        // Handle the cancel button
        binding.btnCancel.setOnClickListener {
            startMainActivity()
        }


        binding.btnOK.setOnClickListener {
            // Create a new note
            val newNote = Note(currentTime.toString())

            // Set its properties to match the
            // user's entries on the form
            when {
                TextUtils.isEmpty(binding.editTitle.text.toString()) -> {
                    binding.editTitle.error = getString(R.string.enter_title)
                }
                TextUtils.isEmpty(binding.editDescription.text.toString()) -> {
                    binding.editDescription.error = getString(R.string.enter_description)
                }
                !binding.checkBoxIdea.isChecked && !binding.checkBoxTodo.isChecked && !binding.checkBoxImportant.isChecked -> {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.select_note_type),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                else -> {
                    newNote.title = binding.editTitle.text.toString()

                    newNote.description = binding.editDescription.text.toString()

                    newNote.idea = binding.checkBoxIdea.isChecked
                    newNote.todo = binding.checkBoxTodo.isChecked
                    newNote.important = binding.checkBoxImportant.isChecked


                    noteViewModel.insert(newNote, onDoneFunction = {
                        startMainActivity()
                    })
                }
            }
        }
        initObjects()

    }

    var progressDialog: AlertDialog? = null
    private fun initObjects() {
        noteViewModel.progress.observe(this) {
            if (it == true) {
                progressDialog = Dialog.progress().cancelable(false).show(this)
            } else {
                progressDialog?.dismiss()
            }
        }

        noteViewModel.errorMessage.observe(this) {
            if (it?.isNotEmpty() == true)
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }
}