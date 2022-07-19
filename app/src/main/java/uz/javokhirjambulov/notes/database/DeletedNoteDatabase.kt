package uz.javokhirjambulov.notes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 1)
abstract class DeletedNoteDatabase: RoomDatabase(){
    companion object {

        private var notesDataBase: DeletedNoteDatabase? = null

        @Synchronized
        fun getDataBase(context: Context): DeletedNoteDatabase {

            if (notesDataBase == null) {
                notesDataBase = Room.databaseBuilder(context, DeletedNoteDatabase::class.java, "deleted_notes.db").build()
            }
            return notesDataBase!!
        }
    }

    abstract fun noteDao(): NotesDatabaseDao
}