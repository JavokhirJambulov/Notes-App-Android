package uz.javokhirjambulov.notes.ui.screens

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import uz.javokhirjambulov.notes.MainActivity
import uz.javokhirjambulov.notes.R
import uz.javokhirjambulov.notes.database.Note
import uz.javokhirjambulov.notes.databinding.ActivityShowNoteBinding
import uz.javokhirjambulov.notes.ui.DeletedNotesViewModel
import java.text.SimpleDateFormat
import java.util.*

class ShowNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowNoteBinding
//    private lateinit var myRef: DatabaseReference
//    private lateinit var auth: FirebaseAuth

    private var note: Note? = null
    private lateinit var noteViewModel:NoteViewModel
    private lateinit var deletedNoteViewModel: DeletedNotesViewModel
   // private var note: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Tag","oncreate of show")
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        deletedNoteViewModel = ViewModelProvider(this)[DeletedNotesViewModel::class.java]
        val b = intent.extras
        var value = "" // or other values
        var deletedDatabase =false

        if (b != null) {
            value = b.getString("key").toString()
            deletedDatabase = b.getBoolean("deletedNotesDatabase")
        }

        /*auth = Firebase.auth
        val database = Firebase.database
        myRef = database.getReference("Notes")*/

        sendNoteSelected(value,deletedDatabase)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_show_note
        )
        binding.lifecycleOwner = this
        if(deletedDatabase)
            binding.btnEdit.visibility=View.GONE





        binding.btnCancel.setOnClickListener {
            startMainActivity()
        }
        binding.btnShare.setOnClickListener{
            val shareIntent = Intent.createChooser(Intent().apply {
                this.action=Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, "My Note: \nTitle: ${binding.txtTitle.text} \nDescription: ${binding.txtDescription.text}")
                this.type = "text/plain"
                // (Optional) Here we're setting the title of the content
                this.putExtra(Intent.EXTRA_TITLE, "${binding.txtTitle.text}")

            }, null)
            startActivity(shareIntent)

        }



    }
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }


    // Receive a note from the MainActivity class
   fun sendNoteSelected(value: String, deletedDatabase: Boolean) {

//        myRef.child(auth.currentUser?.uid.toString()).child(value.toString()).addValueEventListener(object :
//            ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val note = Note(value)
//                note?.description = dataSnapshot.child("description").getValue<String>()
//                note?.title = dataSnapshot.child("title").getValue<String>()
//                note?.idea = dataSnapshot.child("idea").getValue<Boolean>()
//                note?.important = dataSnapshot.child("important").getValue<Boolean>()
//                note?.todo = dataSnapshot.child("todo").getValue<Boolean>()
//
//                Log.i("Tag", note?.description.toString())
//                setNote(note)
//
//            }
//
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w("TAG", "Failed to read value.", error.toException())
//            }
//        })
        when(true){
            deletedDatabase->{
                setNote(deletedNoteViewModel.getNoteWithID(value,applicationContext))
            }
            else->{
                noteViewModel.getNoteWithID(value,applicationContext)?.let { setNote(it) }
            }
        }



    }
    private fun setNote(note11: Note) {
        this.note = note11

        this.note?.noteId.let { it1 ->

            binding.txtTitle.text = note?.title
            binding.txtTitle.movementMethod = ScrollingMovementMethod()

            binding.txtDescription.text = note?.description
            binding.txtDescription.movementMethod = ScrollingMovementMethod()


            val outputDataFormat= SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = note?.noteId?.toLong() ?: 0
            binding.tvDate.text =outputDataFormat.format(calendar.time)

            if (!note?.important!!){
                binding.textViewImportant.visibility = View.GONE
            }

            if (!note?.todo!!){
                binding.textViewTodo.visibility = View.GONE
            }

            if (!note?.idea!!){
                binding.textViewIdea.visibility = View.GONE
            }
            binding.btnEdit.setOnClickListener{
                note?.let { it1 -> editNote(it1) }
            }

        }


    }
    fun editNote(note: Note) {
        val intent =  Intent(this, EditNoteActivity::class.java)
        val b = Bundle()
        b.putString("key", note.noteId) //Your id

        intent.putExtras(b) //Put your id to your next Intent
        startActivity(intent)
    }
}