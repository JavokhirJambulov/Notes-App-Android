package uz.javokhirjambulov.notes

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.javokhirjambulov.notes.database.Note
import java.text.SimpleDateFormat
import java.util.*
import uz.javokhirjambulov.notes.R;
import kotlin.collections.ArrayList

class UsersDiffCallback(
    private val oldList: List<Note>,
    private val newList: List<Note>
): DiffUtil.Callback() {
   
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldNote = oldList[oldItemPosition]
        val newNote = newList[newItemPosition]
        return oldNote.noteId == newNote.noteId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldNote = oldList[oldItemPosition]
        val newNote = newList[newItemPosition]
        return oldNote == newNote
    }
}

class NoteAdapter(
    private val listener: ItemListener,
    initialList: List<Note> = emptyList(),
) : RecyclerView.Adapter<NoteAdapter.ListItemHolder>() {
    private var noteList = ArrayList<Note>()
    private  var comparator: Comparator<Note>?=null

    init {
        noteList.addAll(initialList)
    }


    fun setNote(note: List<Note>) {
        val diffCallback=UsersDiffCallback(noteList,note)
        val diffResult=DiffUtil.calculateDiff(diffCallback)
        noteList.clear()
        noteList.addAll(note)
       // comparator?.let { noteList.sortWith(it) }
        diffResult.dispatchUpdatesTo(this)


    }

    fun deleteNote(adapterPosition: Int) {


        noteList.removeAt(adapterPosition)
        notifyItemRangeChanged(adapterPosition, itemCount)
        //notifyDataSetChanged()

    }

    fun addNote(adapterPosition: Int, note: Note) {
        noteList.add(adapterPosition,note)
        notifyItemRangeChanged(adapterPosition, itemCount)

    }
//    fun sort(comparator: Comparator<Note>) {
//        this.comparator=comparator
//        val noteListSorted= ArrayList(noteList)
//        noteListSorted.sortWith(comparator)
//        val diffCallback=UsersDiffCallback(noteList,noteListSorted)
//        val diffResult=DiffUtil.calculateDiff(diffCallback)
//        //noteList.clear()
//        noteList=noteListSorted
//
//        diffResult.dispatchUpdatesTo(this)
//
//    }



    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int,
    ): ListItemHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.listitem, parent, false)

        return ListItemHolder(itemView)
    }

    override fun getItemCount(): Int {
        return noteList.size
        /*// error
        return -1*/
    }


    override fun onBindViewHolder(holder: ListItemHolder, position: Int) {


        val note = noteList[position]
        /* if (note.title?.length!! > 30) {
             holder.mTitle.text = note.title?.substring(0,30)
         }else{*/
        holder.mTitle.text = note.title
        //}
        // Show the first 30 characters of the actual note
        /* if (note.description!!.length > 30) {
             holder.mDescription.text =
                     note.description?.substring(0, 30)
         } else {*/
        holder.mDescription.text = note.description

        val outputDataFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = note.noteId.toLong()
        holder.mDate.text = outputDataFormat.format(calendar.time)


        //}
        val toDoText = holder.itemView.context.resources.getString(R.string.todo_text)
        val importantText = holder.itemView.context.resources.getString(R.string.important_text)
        val ideaText = holder.itemView.context.resources.getString(R.string.idea_text)
        // What is the status of the note?
        when {


            note.idea == true && note.important == true && note.todo == true ->
                holder.mStatus.text =
                    holder.itemView.context.resources.getString(R.string.idea_todo_important)
            (note.idea == true && note.important == true) ->
                holder.mStatus.text =
                    holder.itemView.context.resources.getString(R.string.idea_important)
            (note.important == true && note.todo == true) ->
                holder.mStatus.text =
                    holder.itemView.context.resources.getString(R.string.todo_important)
            (note.idea == true && note.todo == true) ->
                holder.mStatus.text =
                    holder.itemView.context.resources.getString(R.string.idea_todo)
            note.idea == true ->
                holder.mStatus.text = ideaText

            note.important == true ->
                holder.mStatus.text = importantText

            note.todo == true ->
                holder.mStatus.text = toDoText
            else ->
                holder.mStatus.text =
                    holder.itemView.context.resources.getString(R.string.note_type)

        }


    }

    fun getItem(adapterPosition: Int): Note = noteList[adapterPosition]



    interface ItemListener {
        fun onClick(view: View, itemPosition: Int)
    }

    @Suppress("DEPRECATION")
    inner class ListItemHolder(view: View) : RecyclerView.ViewHolder(view) {


        internal var mTitle =
            view.findViewById<View>(
                R.id.textViewTitle
            ) as TextView

        internal var mDescription =
            view.findViewById<View>(
                R.id.textViewDescription
            ) as TextView

        internal var mStatus =
            view.findViewById<View>(
                R.id.textViewStatus
            ) as TextView
        internal var mDate =
            view.findViewById<View>(
                R.id.textViewDate
            ) as TextView


        init {
            view.isClickable = true
            view.setOnClickListener {
                listener.onClick(view, adapterPosition)
            }

        }
    }
}

