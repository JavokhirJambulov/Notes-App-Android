package uz.javokhirjambulov.notes.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.javokhirjambulov.notes.MainActivity
import uz.javokhirjambulov.notes.R
import uz.javokhirjambulov.notes.commons.Constants
import uz.javokhirjambulov.notes.commons.Dialog
import uz.javokhirjambulov.notes.database.DeletedNoteDatabase
import uz.javokhirjambulov.notes.database.Note
import uz.javokhirjambulov.notes.database.NoteDatabase
import uz.javokhirjambulov.notes.databinding.ActivitySettingsBinding
import uz.javokhirjambulov.notes.ui.screens.NoteViewModel
import uz.javokhirjambulov.notes.ui.screens.NoteViewModelFactory
import java.lang.Exception

@Suppress("DEPRECATION")
class Settings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var myRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var myDeletedNotesRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var deletedNoteViewModel: DeletedNotesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database
        myRef = database.getReference("Notes")
        myDeletedNotesRef = database.getReference("DeletedNotes")
        auth = Firebase.auth
        noteViewModel = ViewModelProvider(
            this,
            NoteViewModelFactory(NoteDatabase.getDataBase())
        )[NoteViewModel::class.java]
        deletedNoteViewModel = ViewModelProvider(this,DeletedNotesViewModelFactory(
            DeletedNoteDatabase.getDataBase()))[DeletedNotesViewModel::class.java]
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
        //initObjects()


        binding.expCloud.setOnClickListener {
            if (!haveNetworkConnection()){
                Snackbar.make(
                    binding.root,
                    getString(R.string.no_net_no_upload),
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            else if(auth.currentUser ==null){
                Snackbar.make(
                    binding.root,
                    getString(R.string.no_signin_no_upload),
                    Snackbar.LENGTH_LONG
                ).show()

            }
            else {
                try {
                    noteViewModel.getAllNotes().observe(this) { lisOfNotes ->
                    lisOfNotes?.let {
                        for (i in it) {
                            if (i.todo == null || i.todo == false) {
                                val note = Note(i.noteId)
                                note.title = i.title
                                note.description = i.description
                                note.idea = i.idea
                                note.important = i.important
                                note.todo = false
                                noteViewModel.update(note)
                                myRef.child(auth.currentUser?.uid.toString()).child(i.noteId)
                                    .setValue(note)
                            } else {
                                myRef.child(auth.currentUser?.uid.toString()).child(i.noteId)
                                    .setValue(i)
                            }
                        }
                    }
                    }



                deletedNoteViewModel.getAllNotes().observe(this) { lisOfNotes ->
                    lisOfNotes?.let {
                            for (i in it) {
                                if (i.todo == null || i.todo == false) {
                                    val note = Note(i.noteId)
                                    note.title = i.title
                                    note.description = i.description
                                    note.idea = i.idea
                                    note.important = i.important
                                    note.todo = false
                                    deletedNoteViewModel.update(note)
                                    myDeletedNotesRef.child(auth.currentUser?.uid.toString())
                                        .child(i.noteId)
                                        .setValue(note)
                                } else {
                                    myDeletedNotesRef.child(auth.currentUser?.uid.toString())
                                        .child(i.noteId)
                                        .setValue(i)
                                }

                            }
                    }
                }

                    Toast.makeText(
                        applicationContext,
                        getString(R.string.uploadedToCloudToast),
                        Toast.LENGTH_SHORT
                    ).show()
                }catch (e:Exception){
                    noteViewModel.setErrorMessage(e.toString())
                }

                // startMainActivity()
            }
        }
        binding.impCloud.setOnClickListener {
//            var finished = false
//            if (!finished) {
            if (!haveNetworkConnection()){
                Snackbar.make(
                    binding.root,
                    getString(R.string.no_net_no_import),
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            else if(auth.currentUser ==null){
                Snackbar.make(
                    binding.root,
                    getString(R.string.no_signin_no_import),
                    Snackbar.LENGTH_LONG
                ).show()

            }else {
                try {
                    implCloud()
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.importedFromCloudToast),
                        Toast.LENGTH_SHORT
                    ).show()
                }catch (e:Exception) {
                    noteViewModel.setErrorMessage(e.toString())
                }
            }
        }

        binding.appIntro.setOnClickListener {
            // show app intro
            val i = Intent(this, MainIntroActivity::class.java)
            startActivity(i)
        }



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

    private fun haveNetworkConnection():Boolean{
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm =  getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.allNetworkInfo
        for(i in netInfo){
            if(i.typeName.equals("WIFI",true)){
                if(i.isConnected){
                    haveConnectedWifi =true
                }
            }
            if(i.typeName.equals("MOBILE",true)){
                if(i.isConnected){
                    haveConnectedMobile =true
                }
            }
        }
        return haveConnectedMobile||haveConnectedWifi
    }



    private fun implCloud() {
        myRef.child(auth.currentUser?.uid.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (postSnapshot in dataSnapshot.children) {
                    val noteId = postSnapshot.key.toString()
                    val note = Note(noteId)
                    note.description = postSnapshot.child("description").getValue<String>()
                    note.title = postSnapshot.child("title").getValue<String>()
                    note.idea = postSnapshot.child("idea").getValue<Boolean>() ?: false
                    note.important = postSnapshot.child("important").getValue<Boolean>() ?: false
                    note.todo = postSnapshot.child("todo").getValue<Boolean>() ?: false

                    noteViewModel.insert(note)
                }

            }


            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
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
                        note.idea =
                            postSnapshot.child("idea").getValue<Boolean>() ?: false
                        note.important =
                            postSnapshot.child("important").getValue<Boolean>() ?: false
                        note.todo =
                            postSnapshot.child("todo").getValue<Boolean>() ?: false

                        deletedNoteViewModel.insert(note)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("TAG", "Failed to read value.", error.toException())
                }
            })
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