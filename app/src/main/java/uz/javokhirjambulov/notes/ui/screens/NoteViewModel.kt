package uz.javokhirjambulov.notes.ui.screens

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import uz.javokhirjambulov.notes.database.DeletedNoteDatabase
import uz.javokhirjambulov.notes.database.Note
import uz.javokhirjambulov.notes.database.NoteDatabase

class NoteViewModel:ViewModel() {

    //Database Operations in view model
    private val _mTitle = MutableLiveData<Boolean>(false)
       val mTitle: LiveData<Boolean> get() = _mTitle
    private val _mOld = MutableLiveData<Boolean>(false)
        val mOld: LiveData<Boolean> get() = _mOld

    private val _mNew = MutableLiveData<Boolean>(false)
       val mNew: LiveData<Boolean> get() = _mNew
    fun title(){
        _mTitle.value = true
        _mOld.value = false
        _mNew.value = false
    }
    fun old(){
        _mTitle.value = false
        _mOld.value = true
        _mNew.value = false
    }
    fun new(){
        _mTitle.value = false
        _mOld.value = false
        _mNew.value = true
    }

    // Method #1
    fun insert(note: Note,context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
         NoteDatabase.getDataBase(context).noteDao().insert(note)
        }
    }

   /* // Method #2
    fun delete(note: Note,context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            NoteDatabase.getDataBase(context).noteDao().deleteById(note.noteId)
        }
    }*/

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
    fun getAllNotesByIdOld(context: Context): LiveData<List<Note>> {
            return NoteDatabase.getDataBase(context).noteDao().getAllNotesByIdOld()

    }
    fun getAllNotesByIdNew(context: Context): LiveData<List<Note>> {
        return NoteDatabase.getDataBase(context).noteDao().getAllNotesByIdNew()

    }
    fun getAllNotesByTitle(context: Context): LiveData<List<Note>> {
        return NoteDatabase.getDataBase(context).noteDao().getAllNotesByTitle()

    }
    suspend fun getNoteWithID(id:String,context: Context): Note? {
        var note: Note? = null
        note =  withContext(Dispatchers.IO){

            NoteDatabase.getDataBase(context).noteDao().getNoteWithId(id)
            //Log.i("Tag","note inside viewmodel ${note1.title}")

        }
        Log.i("Tag","note inside viewmodel ${note.title}")
        return note

    }

//    private fun returnNote(note1: Note):Note {
//        return note1
//
//    }
}