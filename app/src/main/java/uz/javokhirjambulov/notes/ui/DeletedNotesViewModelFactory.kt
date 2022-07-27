package uz.javokhirjambulov.notes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.javokhirjambulov.notes.database.DeletedNoteDatabase

class DeletedNotesViewModelFactory(private val deletedNoteDatabase: DeletedNoteDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DeletedNotesViewModel(deletedNoteDatabase) as T
    }
}