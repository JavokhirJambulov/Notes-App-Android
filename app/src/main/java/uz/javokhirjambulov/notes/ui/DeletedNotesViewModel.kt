package uz.javokhirjambulov.notes.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.javokhirjambulov.notes.database.DeletedNoteDatabase
import uz.javokhirjambulov.notes.database.Note

class DeletedNotesViewModel : ViewModel() {

    //Database Operations in view model

    // Method #1
    fun insert(note: Note, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            DeletedNoteDatabase.getDataBase(context).noteDao().save(note)
        }
    }

    // Method #2
//    fun delete(note: Note, context: Context) {
//        viewModelScope.launch(Dispatchers.IO) {
//            DeletedNoteDatabase.getDataBase(context).noteDao().deleteById(note.noteId)
//        }
//    }

    // Method #3
    fun deleteById(id: String, context: Context) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                DeletedNoteDatabase.getDataBase(context).noteDao().deleteById(id)
            }
        } catch (e: Exception) {

        }
    }

    // Method #4
    fun update(note: Note, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            DeletedNoteDatabase.getDataBase(context).noteDao().update(note)
        }
    }

    // Method #5
    fun getAllNotes(context: Context): LiveData<List<Note>> {
        return DeletedNoteDatabase.getDataBase(context).noteDao().getAllNotes()

    }

    suspend fun getNoteWithID(id: String, context: Context): Note {
        val note: Note = withContext(Dispatchers.IO) {

            DeletedNoteDatabase.getDataBase(context).noteDao().getNoteWithId(id)
            //Log.i("Tag","note inside viewmodel ${note1.title}")

        }
        Log.i("Tag", "note inside deleted viewmodel ${note.title}")
        return note

    }
}