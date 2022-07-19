package uz.javokhirjambulov.notes.ui.screens

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uz.javokhirjambulov.notes.database.Note
import uz.javokhirjambulov.notes.database.NoteDatabase

class NoteViewModel:ViewModel() {

    //Database Operations in view model


    // Method #1
    fun insert(note: Note,context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
         NoteDatabase.getDataBase(context).noteDao().insert(note)
        }
    }

    // Method #2
    fun delete(note: Note,context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            NoteDatabase.getDataBase(context).noteDao().deleteById(note.noteId)
        }
    }

    // Method #3
    fun deleteById(id:String,context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            NoteDatabase.getDataBase(context).noteDao().deleteById(id)
        }
    }

    // Method #4
    fun update(note: Note,context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            NoteDatabase.getDataBase(context).noteDao().update(note)
        }
    }

    // Method #5
    fun getAllNotes(context: Context): LiveData<List<Note>> {
            return NoteDatabase.getDataBase(context).noteDao().getAllNotes()

    }
    fun getNoteWithID(id:String,context: Context): Note? {
        var note: Note? = null
        viewModelScope.launch(Dispatchers.IO) {
            note = NoteDatabase.getDataBase(context).noteDao().getNoteWithId(id)
        }
        return note

    }
}