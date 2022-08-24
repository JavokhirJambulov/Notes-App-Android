package uz.javokhirjambulov.notes

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.javokhirjambulov.notes.database.Note
import java.text.SimpleDateFormat
import java.util.*


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



    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int,
    ): ListItemHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.listitem, parent, false)

        return ListItemHolder(itemView)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }


    override fun onBindViewHolder(holder: ListItemHolder, position: Int) {


        val note = noteList[position]
        holder.mTitle.text = note.title
        holder.mDescription.text = note.description
        val outputDataFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = note.noteId.toLong()
        holder.mDate.text = outputDataFormat.format(calendar.time)


        val toDoText = holder.itemView.context.resources.getString(R.string.todo_text)
        val importantText = holder.itemView.context.resources.getString(R.string.important_text)
        val ideaText = holder.itemView.context.resources.getString(R.string.idea_text)
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

        if(noteList.size==1){
            val animationLeft =
                ObjectAnimator.ofFloat(holder.itemView, "translationX", 0f, -80f, 0f)
            animationLeft.duration = 2000
            animationLeft.start()
//            val animation = AnimationUtils.loadAnimation(holder.itemView.context,R.anim.swipe_animation)
//            holder.itemView.startAnimation(animation)
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

