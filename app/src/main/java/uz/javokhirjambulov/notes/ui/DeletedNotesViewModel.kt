package uz.javokhirjambulov.notes.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.javokhirjambulov.notes.database.DeletedNoteDatabase
import uz.javokhirjambulov.notes.database.Note
import uz.javokhirjambulov.notes.database.NoteDatabase

class DeletedNotesViewModel(private val deletedNoteDatabase: DeletedNoteDatabase) : ViewModel() {

    //Database Operations in view model
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _progress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean> get() = _progress
    fun insert(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                setProgress(true)
                deletedNoteDatabase.noteDao().save(note)
            } catch (e: Exception) {
                _errorMessage.postValue(e.toString())
            } finally {
                setProgress(false)
            }
        }
    }
    private fun setProgress(b: Boolean) {
        _progress.postValue(b)
    }
    fun deleteById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                setProgress(true)
                deletedNoteDatabase.noteDao().deleteById(id)
            } catch (e: Exception) {
                _errorMessage.postValue(e.toString())
            } finally {
                setProgress(false)
            }
        }
    }

    fun update(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                setProgress(true)
                deletedNoteDatabase.noteDao().update(note)

            } catch (e: Exception) {
                _errorMessage.postValue(e.toString())
            } finally {
                setProgress(false)
            }
        }
    }

    fun getAllNotes(): LiveData<List<Note>> {
        return deletedNoteDatabase.noteDao().getAllNotes()
    }
    suspend fun getNoteWithID(id: String): Note {
        val note: Note = withContext(Dispatchers.IO) {
            deletedNoteDatabase.noteDao().getNoteWithId(id)
        }
        return note

    }
}