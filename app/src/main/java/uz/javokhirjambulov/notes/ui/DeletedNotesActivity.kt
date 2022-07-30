package uz.javokhirjambulov.notes.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import uz.javokhirjambulov.notes.NoteAdapter
import uz.javokhirjambulov.notes.R
import uz.javokhirjambulov.notes.SwipeGesture
import uz.javokhirjambulov.notes.database.DeletedNoteDatabase
import uz.javokhirjambulov.notes.database.Note
import uz.javokhirjambulov.notes.ui.screens.ShowNoteActivity


class DeletedNotesActivity : AppCompatActivity(), NoteAdapter.ItemListener {
//    private lateinit var myRef: DatabaseReference
//    private lateinit var auth: FirebaseAuth
    private val adapter: NoteAdapter by lazy {
        NoteAdapter(this)
    }
    private lateinit var deletedNoteViewModel:DeletedNotesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deleted_notes)
        deletedNoteViewModel = ViewModelProvider(this,DeletedNotesViewModelFactory(
            DeletedNoteDatabase.getDataBase()))[DeletedNotesViewModel::class.java]
        val recyclerViewDeletedNotes = findViewById<RecyclerView>(R.id.recyclerViewDeletedNotes)


        recyclerViewDeletedNotes.adapter = adapter
        deletedNoteViewModel.getAllNotes().observe(this) { lisOfNotes ->
            lisOfNotes?.let {
                adapter.setNote(it)
            }
        }

        val swipeGesture = object : SwipeGesture(this) {

            @Suppress("DEPRECATION")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val position = viewHolder.adapterPosition
                        val deletedItem = adapter.getItem(position)
                        //adapter.deleteNote(position)
                        deleteFromDeletedNotesDatabase(deletedItem)
                        Snackbar.make(recyclerViewDeletedNotes!!,
                            deletedItem.title.toString() + getString(R.string.is_deleted), Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.undo)) {
                                   insertToDatabase(deletedItem)


                                }.addCallback(object : Snackbar.Callback() {
                                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                        super.onDismissed(transientBottomBar, event)
                                        if (event != DISMISS_EVENT_ACTION) {
                                            Toast.makeText(applicationContext,
                                                deletedItem.title.toString() + getString(R.string.is_deleted_permanently), Toast.LENGTH_SHORT).show()
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
    private fun insertToDatabase(deletedNote: Note) {
      deletedNoteViewModel.insert(deletedNote)
    }

    private fun deleteFromDeletedNotesDatabase(deletedNote: Note) {
        deletedNoteViewModel.deleteById(deletedNote.noteId)
    }


    private fun showNote(noteToShow: Int) {
        val intent = Intent(this, ShowNoteActivity::class.java)
        val b = Bundle()
        b.putString("key", adapter.getItem(noteToShow).noteId) //Your id
        b.putBoolean("deletedNotesDatabase",true)
        intent.putExtras(b) //Put your id to your next Intent

        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onClick(view: View, itemPosition: Int) {
        showNote(itemPosition)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }
}