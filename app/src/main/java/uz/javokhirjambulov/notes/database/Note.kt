package uz.javokhirjambulov.notes.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notes_table")
data class Note(
    @PrimaryKey
    val noteId: String,
    @ColumnInfo(name = "note_title")
    var title: String? = null,
    @ColumnInfo(name = "note_description")
    var description: String? = null,
    @ColumnInfo(name = "note_type_idea")
    var idea: Boolean? = false,
    @ColumnInfo(name = "note_type_todo")
    var todo: Boolean? = false,
    @ColumnInfo(name = "note_type_important")
    var important: Boolean? = false,
)