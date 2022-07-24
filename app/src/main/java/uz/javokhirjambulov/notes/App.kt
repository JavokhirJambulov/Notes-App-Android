package uz.javokhirjambulov.notes

import android.app.Application
import uz.javokhirjambulov.notes.database.NoteDatabase

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        NoteDatabase.initDatabase(context = this)
    }
}