package uz.javokhirjambulov.notes.ui.screens

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.javokhirjambulov.notes.database.Note
import uz.javokhirjambulov.notes.database.NoteDatabase

class NoteViewModel(private val noteDatabase: NoteDatabase) : ViewModel() {

    //Database Operations in view model
    private val _mTitle = MutableLiveData<Boolean>(false)
    val mTitle: LiveData<Boolean> get() = _mTitle
    private val _mOld = MutableLiveData<Boolean>(false)
    val mOld: LiveData<Boolean> get() = _mOld

    private val _mNew = MutableLiveData<Boolean>(false)
    val mNew: LiveData<Boolean> get() = _mNew

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _progress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean> get() = _progress

    fun title() {
        _mTitle.value = true
        _mOld.value = false
        _mNew.value = false
    }

    fun old() {
        _mTitle.value = false
        _mOld.value = true
        _mNew.value = false
    }

    fun new() {
        _mTitle.value = false
        _mOld.value = false
        _mNew.value = true
    }

    // Method #1
    fun insert(note: Note, onDoneFunction: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                noteDatabase.noteDao().save(note)
                onDoneFunction?.invoke()
            } catch (e: Exception) {
                setErrorMessage(e.toString())
            }
        }
    }

    /* // Method #2
     fun delete(note: Note,context: Context) {
         viewModelScope.launch(Dispatchers.IO) {
             noteDatabase.noteDao().deleteById(note.noteId)
         }
     }*/

    // Method #3
    fun deleteById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                setProgress(true)

                noteDatabase.noteDao().deleteById(id)

            } catch (e: Exception) {
                setErrorMessage(e.toString())
            } finally {
                setProgress(false)
            }
        }

    }

    fun setProgress(b: Boolean) {
        _progress.postValue(b)
    }
    fun setErrorMessage(e: String) {
        _errorMessage.postValue(e)
    }


    // Method #4
    fun update(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                setProgress(true)
                noteDatabase.noteDao().update(note)

            } catch (e: Exception) {
                setErrorMessage(e.toString())
            } finally {
                setProgress(false)
            }
        }

    }

    // Method #5
    fun getAllNotes(): LiveData<List<Note>> {
        return noteDatabase.noteDao().getAllNotes()

    }

    fun getAllNotesByIdOld(): LiveData<List<Note>> {
        return noteDatabase.noteDao().getAllNotesByIdOld()

    }

    fun getAllNotesByIdNew(): LiveData<List<Note>> {
        return noteDatabase.noteDao().getAllNotesByIdNew()

    }

    fun getAllNotesByTitle(): LiveData<List<Note>> {
        return noteDatabase.noteDao().getAllNotesByTitle()

    }

    suspend fun getNoteWithID(id: String): Note? {
        var note: Note?
        note = withContext(Dispatchers.IO) {

            noteDatabase.noteDao().getNoteWithId(id)
            //Log.i("Tag","note inside viewmodel ${note1.title}")

        }
        Log.i("Tag", "note inside viewmodel ${note.title}")
        return note

    }

}