package uz.javokhirjambulov.notes.ui.screens

import androidx.lifecycle.ViewModel

import androidx.lifecycle.ViewModelProvider
import uz.javokhirjambulov.notes.database.NoteDatabase

class NoteViewModelFactory(private val noteDatabase: NoteDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModel(noteDatabase) as T
    }
}