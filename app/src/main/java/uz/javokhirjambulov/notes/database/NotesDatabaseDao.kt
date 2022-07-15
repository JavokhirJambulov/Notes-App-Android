package uz.javokhirjambulov.notes.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDatabaseDao {
    @Insert
    suspend fun insert(note: Note)
    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     */
    @Update
    suspend fun update(note: Note)
    /**
     * Selects and returns the row that matches the supplied noteId, which is our key.
     */
    @Query("SELECT * from notes_table WHERE noteId = :key")
    suspend fun get(key: String): Note?
    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM notes_table")
    suspend fun clear()
    /**
     * Selects and returns all rows in the table,
     *
     * sorted by noteId in descending order.
     */
    @Query("SELECT * FROM notes_table ORDER BY noteId DESC")
    fun getAllNotes(): LiveData<List<Note>>
    /**
     * Selects and returns the note with given noteId.
     */
    @Query("SELECT * from notes_table WHERE noteId = :key")
    fun getNoteWithId(key: String): LiveData<Note>
}