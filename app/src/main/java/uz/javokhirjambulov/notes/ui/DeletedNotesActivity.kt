package uz.javokhirjambulov.notes.ui

import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import uz.javokhirjambulov.notes.MainActivity
import uz.javokhirjambulov.notes.NoteAdapter
import uz.javokhirjambulov.notes.R
import uz.javokhirjambulov.notes.SwipeGesture
import uz.javokhirjambulov.notes.database.Note
import uz.javokhirjambulov.notes.ui.screens.ShowNoteActivity


class DeletedNotesActivity : AppCompatActivity(), NoteAdapter.ItemListener {
    private lateinit var myRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val adapter: NoteAdapter by lazy {
        NoteAdapter(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deleted_notes)
//        val actionBar: ActionBar? = actionBar
////        actionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.title = getString(R.string.deletedNotes)

        auth = Firebase.auth
        val database = Firebase.database
        myRef = database.getReference("DeletedNotes")
        val recyclerViewDeletedNotes = findViewById<RecyclerView>(R.id.recyclerViewDeletedNotes)


        recyclerViewDeletedNotes.adapter = adapter
        myRef.child(auth.currentUser?.uid.toString()).addValueEventListener(object :
                ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val noteList1: MutableList<Note> = mutableListOf()

                for (postSnapshot in dataSnapshot.children) {
                    //getNote = Note()
                    val noteId = postSnapshot.key.toString()
                    val note = Note(noteId)
                    note.description = postSnapshot.child("description").getValue<String>()
                    note.title = postSnapshot.child("title").getValue<String>()
                    note.idea = postSnapshot.child("idea").getValue<Boolean>()
                    note.important = postSnapshot.child("important").getValue<Boolean>()
                    note.todo = postSnapshot.child("todo").getValue<Boolean>()

                    noteList1.add(note)
                }
                adapter.setNote(noteList1)
            }


            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        val swipeGesture = object : SwipeGesture(this) {

            @Suppress("DEPRECATION")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        /*   val archiveItem = adapter?.getItem(viewHolder.adapterPosition)
                           //createNewNote(archiveItem!!)*/

                    }
                    ItemTouchHelper.LEFT -> {
                        val position = viewHolder.adapterPosition
                        val deletedItem = adapter.getItem(position)

                        adapter.deleteNote(position)
                        Snackbar.make(recyclerViewDeletedNotes!!, "${deletedItem.title.toString()} is deleted", Snackbar.LENGTH_LONG)
                                .setAction("Undo") {
                                    // adding on click listener to our action of snack bar.
                                    // below line is to add our item to array list with a position.

                                    deletedItem.let { it1 -> adapter.addNote(position, it1) }


                                }.addCallback(object : Snackbar.Callback() {
                                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                        super.onDismissed(transientBottomBar, event)
                                        if (event != DISMISS_EVENT_ACTION) {
                                            // Snackbar closed on its own
                                            deleteFromDeletedNotesDatabase(deletedItem)

                                        }
                                    }

                                    override fun onShown(sb: Snackbar?) {
                                        super.onShown(sb)

                                    }
                                }).show()


                    }
                }


            }

        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(recyclerViewDeletedNotes)


    }

  /*  override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val myIntent = Intent(applicationContext, MainActivity::class.java)
                startActivity(myIntent)
                return true
            }
        }
        return true

    }*/

    private fun deleteFromDeletedNotesDatabase(deletedNote: Note) {

        myRef.child(auth.currentUser?.uid.toString()).child(deletedNote.noteId).removeValue()
    }

    private fun showNote(noteToShow: Int) {
        val intent = Intent(this, ShowNoteActivity::class.java)
        val b = Bundle()
        b.putString("key", adapter.getItem(noteToShow).noteId) //Your id

        intent.putExtras(b) //Put your id to your next Intent

        startActivity(intent)
    }

    override fun onClick(view: View, itemPosition: Int) {
        showNote(itemPosition)
    }
}