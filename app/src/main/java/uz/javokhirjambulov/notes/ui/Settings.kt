package uz.javokhirjambulov.notes.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.javokhirjambulov.notes.MainActivity
import uz.javokhirjambulov.notes.R
import uz.javokhirjambulov.notes.commons.Constants
import uz.javokhirjambulov.notes.database.Note
import uz.javokhirjambulov.notes.databinding.ActivitySettingsBinding
import uz.javokhirjambulov.notes.ui.screens.NoteViewModel

class Settings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var myRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var myDeletedNotesRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var noteViewModel:NoteViewModel
    private lateinit var deletedNoteViewModel:DeletedNotesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database
        myRef = database.getReference("Notes")
        myDeletedNotesRef = database.getReference("DeletedNotes")
        auth = Firebase.auth
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        deletedNoteViewModel = ViewModelProvider(this)[DeletedNotesViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.viewSourceCode.setOnClickListener {
            val openGithub = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.sourceCodeURL))
            startActivity(openGithub)
        }
        binding.sendFeedback.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO,
                Uri.parse(String.format(Constants.feedbackURL, getString(R.string.app_name)))
            )

            startActivity(emailIntent)
        }
        binding.expCloud.setOnClickListener {

            noteViewModel.getAllNotes(applicationContext).observe(this){ lisOfNotes ->
                lisOfNotes?.let {
                    for(i in it) {
                        myRef.child(auth.currentUser?.uid.toString()).child(i.noteId)
                            .setValue(i)
                    }
                }
            }
            deletedNoteViewModel.getAllNotes(applicationContext).observe(this){ lisOfNotes ->
                lisOfNotes?.let {
                    for(i in it) {
                        myDeletedNotesRef.child(auth.currentUser?.uid.toString()).child(i.noteId)
                            .setValue(i)
                    }
                }
            }


            Toast.makeText(applicationContext,"All notes are successfully uploaded to the cloud",Toast.LENGTH_SHORT).show()
           // startMainActivity()

        }
        binding.impCloud.setOnClickListener{
            var noteFinished = false
            if(!noteFinished){
                myRef.child(auth.currentUser?.uid.toString()).addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        for (postSnapshot in dataSnapshot.children) {
                            val noteId = postSnapshot.key.toString()
                            val note = Note(noteId)
                            note.description = postSnapshot.child("description").getValue<String>()
                            note.title = postSnapshot.child("title").getValue<String>()
                            note.idea = postSnapshot.child("idea").getValue<Boolean>()
                            note.important = postSnapshot.child("important").getValue<Boolean>()
                            note.todo = postSnapshot.child("to do").getValue<Boolean>()

                            noteViewModel.insert(note, applicationContext)
                        }
                        noteFinished = true
                    }


                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        Log.w("TAG", "Failed to read value.", error.toException())
                    }
                })
            }else{


                myDeletedNotesRef.child(auth.currentUser?.uid.toString())
                    .addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            for (postSnapshot in dataSnapshot.children) {
                                val noteId = postSnapshot.key.toString()
                                val note = Note(noteId)
                                note.description =
                                    postSnapshot.child("description").getValue<String>()
                                note.title = postSnapshot.child("title").getValue<String>()
                                note.idea = postSnapshot.child("idea").getValue<Boolean>()
                                note.important = postSnapshot.child("important").getValue<Boolean>()
                                note.todo = postSnapshot.child("to do").getValue<Boolean>()

                                deletedNoteViewModel.insert(note, applicationContext)
                            }
                        }


                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            Log.w("TAG", "Failed to read value.", error.toException())
                        }
                    })
            }

            Toast.makeText(applicationContext,"All notes are successfully imported from the cloud",Toast.LENGTH_SHORT).show()
        }

//        binding.appIntro.setOnClickListener {
//            // show app intro
//            val i = Intent(this, MainIntroActivity::class.java)
//            startActivity(i)
//        }

    }
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}