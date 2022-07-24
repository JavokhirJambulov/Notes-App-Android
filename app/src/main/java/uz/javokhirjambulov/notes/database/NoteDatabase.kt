package uz.javokhirjambulov.notes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    companion object {

        private var notesDataBase: NoteDatabase? = null

        @Synchronized
        fun getDataBase(context: Context? = null): NoteDatabase {

            if (notesDataBase == null && context != null) {
                notesDataBase =
                    Room.databaseBuilder(context, NoteDatabase::class.java, "notes.db").build()
            }
            return notesDataBase!!
        }

        fun initDatabase(context: Context? = null) {
            getDataBase(context = context)
        }

    }

    abstract fun noteDao(): NotesDatabaseDao
}