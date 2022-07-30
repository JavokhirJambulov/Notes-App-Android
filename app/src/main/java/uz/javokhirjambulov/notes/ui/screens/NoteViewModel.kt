package uz.javokhirjambulov.notes.ui.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.javokhirjambulov.notes.database.Note
import uz.javokhirjambulov.notes.database.NoteDatabase

class NoteViewModel(private val noteDatabase: NoteDatabase) : ViewModel() {

    //Database Operations in view model
    private val _mTitle = MutableLiveData(false)
    val mTitle: LiveData<Boolean> get() = _mTitle
    private val _mOld = MutableLiveData(false)
    val mOld: LiveData<Boolean> get() = _mOld

    private val _mNew = MutableLiveData(false)
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

    suspend fun getNoteWithID(id: String): Note {
        val note: Note?
        note = withContext(Dispatchers.IO) {
            noteDatabase.noteDao().getNoteWithId(id)
        }
        return note

    }

}