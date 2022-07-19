package uz.javokhirjambulov.notes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase: RoomDatabase(){
    companion object {

        private var notesDataBase: NoteDatabase? = null

        @Synchronized
        fun getDataBase(context: Context): NoteDatabase {

            if (notesDataBase == null) {
                notesDataBase = Room.databaseBuilder(context, NoteDatabase::class.java, "notes.db").build()
            }
            return notesDataBase!!
        }
    }

    abstract fun noteDao(): NotesDatabaseDao
}