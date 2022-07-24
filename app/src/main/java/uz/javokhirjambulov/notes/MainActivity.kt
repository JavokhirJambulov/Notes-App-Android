

package uz.javokhirjambulov.notes

//import android.annotation.SuppressLint

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import uz.javokhirjambulov.notes.commons.Constants
import uz.javokhirjambulov.notes.database.Note
import uz.javokhirjambulov.notes.database.NoteDatabase
import uz.javokhirjambulov.notes.login.LoginActivity
import uz.javokhirjambulov.notes.ui.DeletedNotesActivity
import uz.javokhirjambulov.notes.ui.DeletedNotesViewModel
import uz.javokhirjambulov.notes.ui.MainIntroActivity
import uz.javokhirjambulov.notes.ui.Settings
import uz.javokhirjambulov.notes.ui.screens.NewNoteActivity
import uz.javokhirjambulov.notes.ui.screens.NoteViewModel
import uz.javokhirjambulov.notes.ui.screens.NoteViewModelFactory
import uz.javokhirjambulov.notes.ui.screens.ShowNoteActivity
import java.util.*


private const val TAG = ""

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    NoteAdapter.ItemListener {


   /* private lateinit var newFirst:MenuItem
    private lateinit var oldFirst:MenuItem
    private lateinit var titleFirst:MenuItem*/

    //    private lateinit var myDeletedNotesRef: DatabaseReference
//    private lateinit var myRef: DatabaseReference
//    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout
    private var recyclerView: RecyclerView? = null
    private val adapter: NoteAdapter by lazy {
        NoteAdapter(this)
    }
    private lateinit var fab:FloatingActionButton
    private lateinit var imageUser: ImageView
    private lateinit var txtUserName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var preferencesPrivate: SharedPreferences
    private lateinit var noteViewModel:NoteViewModel
    private lateinit var deletedNoteViewModel:DeletedNotesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        preferencesPrivate = this.getSharedPreferences(
            this.packageName + "_private_preferences",
            Context.MODE_PRIVATE
        )
        if (isFirstRun()) {
            // show app intro
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            consumeFirstRun()
        }
        noteViewModel = ViewModelProvider(this,  NoteViewModelFactory(NoteDatabase.getDataBase()))[NoteViewModel::class.java]
        deletedNoteViewModel = ViewModelProvider(this)[DeletedNotesViewModel::class.java]



        auth = Firebase.auth


        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent =  Intent(this, NewNoteActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            //hide the keyboard
//            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(fab.windowToken,0)

        }

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val header = navView.getHeaderView(0)
        imageUser = header.findViewById(R.id.imageUser)
        txtUserName = header.findViewById(R.id.txtUserName)
        txtEmail = header.findViewById(R.id.txtEmail)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

//        // Write a message to the database
//        database = Firebase.database
//        myRef = database.getReference("Notes")
//        myDeletedNotesRef = database.getReference("DeletedNotes")

        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView?.adapter = adapter
//        lifecycleScope.launch(Dispatchers.IO){
//            recyclerView!!.smoothSnapToPosition(0)
//        }



        /*val newestFirst = findViewById<RadioButton>(R.id.mNewFirst)
        val oldestFirst = findViewById<RadioButton>(R.id.mOldFirst)
        val titleFirst = findViewById<RadioButton>(R.id.mTitle)*/

       /* myRef.child(auth.currentUser?.uid.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val noteList1: MutableList<Note> = mutableListOf()

                for (postSnapshot in dataSnapshot.children) {
                    //getNote = Note()
                    val noteId = postSnapshot.key.toString()
                    val note = Note(noteId)
                    note.description = postSnapshot.child("description").getValue<String>()
                    note.title = postSnapshot.child("title").getValue<String>()
                    note.idea = postSnapshot.child("idea").getValue<Boolean>()
                    note.important = postSnapshot.child("important").getValue<Boolean>()
                    note.to do = postSnapshot.child("to do").getValue<Boolean>()

                    noteList1.add(note)
                }

                adapter.setNote(noteList1)
            }


            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })*/
//        val menu: Menu? = null
//        menuInflater.inflate(R.menu.main, menu)
//        if (menu != null) {
//            newFirst = menu.findItem(R.id.mNewFirst)
//        }
//        if (menu != null) {
//            oldFirst = menu.findItem(R.id.mOldFirst)
//        }
//        if (menu != null) {
//            titleFirst = menu.findItem(R.id.mTitle)
        //}
        when {
            isNewFirst() -> {
                noteViewModel.new()
                //newFirst.isChecked = true
            }
            isOldFirst()->{
                noteViewModel.old()
                //oldFirst.isChecked = true

            }
            isTitleFirst()->{
                noteViewModel.title()
                //titleFirst.isChecked = true
            }
        }

        noteViewModel.mNew.observe(this){it->
            if(it == true){

                preferencesPrivate.edit().putBoolean(Constants.new, true).apply()
                notOldFirst()
                notTitleFirst()

                noteViewModel.getAllNotesByIdNew().observe(this) { lisOfNotes ->
                    lisOfNotes?.let {
                        adapter.setNote(it)
                    }
                }
                recyclerView!!.smoothSnapToPosition(0)
            }
        }
        noteViewModel.mOld.observe(this){it->
            if(it == true){

                preferencesPrivate.edit().putBoolean(Constants.old, true).apply()
                notNewFirst()
                notTitleFirst()

                noteViewModel.getAllNotesByIdOld().observe(this) { lisOfNotes ->
                    lisOfNotes?.let {
                        adapter.setNote(it)
                    }
                }
                recyclerView!!.smoothSnapToPosition(0)

            }
        }
        noteViewModel.mTitle.observe(this){it->
            if(it == true){

                preferencesPrivate.edit().putBoolean(Constants.title, true).apply()
                notOldFirst()
                notNewFirst()

                noteViewModel.getAllNotesByTitle().observe(this) { lisOfNotes ->
                    lisOfNotes?.let {
                        adapter.setNote(it)
                    }
                }
                recyclerView!!.smoothSnapToPosition(0)
            }
        }
        adapter.registerAdapterDataObserver( object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                //lifecycleScope.launch(Dispatchers.IO){
                 recyclerView!!.smoothSnapToPosition(0)
                //}
            }

//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                super.onItemRangeInserted(positionStart, itemCount)
//                recyclerView!!.smoothSnapToPosition(0)
//            }
        })








        val swipeGesture = object : SwipeGesture(this) {

            @SuppressLint("NotifyDataSetChanged")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        /*    val archiveItem = adapter?.getItem(viewHolder.adapterPosition)
                            //createNewNote(archiveItem!!)*/

                    }
                    ItemTouchHelper.LEFT -> {
                        val position = viewHolder.adapterPosition
                        val deletedItem = adapter.getItem(position)
                        //adapter.deleteNote(position)
                        deleteFromDatabase(deletedItem)
                        //createDeletedNotesDatabase(adapter.getItem(position))
                        //recyclerView!!.recycledViewPool.clear()
                        //adapter.notifyDataSetChanged()

                        Snackbar.make(
                            recyclerView!!,
                            "${deletedItem.title.toString()} is deleted",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Undo") {
                                // adding on click listener to our action of snack bar.
                                // below line is to add our item to array list with a position.

                                //deletedItem.let { it1 -> adapter.addNote(position, it1) }
                                insertToDatabase(deletedItem)

                            }.addCallback(object : Snackbar.Callback() {
                                override fun onDismissed(
                                    transientBottomBar: Snackbar?,
                                    event: Int
                                ) {
                                    super.onDismissed(transientBottomBar, event)
                                    if (event != DISMISS_EVENT_ACTION) {
                                        // Snackbar closed on its own
                                        //deleteFromDatabase(deletedItem)
                                        createDeletedNotesDatabase(deletedItem)

                                    }
                                }

                                override fun onShown(sb: Snackbar?) {
                                    super.onShown(sb)

                                }
                            }).show()


                    }
                }


            }

        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(recyclerView)

        recyclerView?.itemAnimator = DefaultItemAnimator()
        // Add a neat dividing line between items in the list


        displayScreen(-1)
        val user = Firebase.auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
            txtUserName.text = user.displayName
            txtEmail.text = user.email
            Glide.with(this).load(user.photoUrl).into(imageUser)
        }


        // titleFirst?.let { newestFirst?.let { it1 -> oldestFirst?.let { it2 -> adapter?.updateList(it, it1, it2) } } }

    }

    private fun insertToDatabase(deletedNote: Note) {
//        myRef.child(auth.currentUser?.uid.toString()).child(deletedNote.noteId).removeValue()
        noteViewModel.insert(deletedNote)
    }

    private fun deleteFromDatabase(deletedNote: Note) {
//        myRef.child(auth.currentUser?.uid.toString()).child(deletedNote.noteId).removeValue()
        noteViewModel.deleteById(deletedNote.noteId)
    }

    private fun createDeletedNotesDatabase(deletedNote: Note) {

//        myDeletedNotesRef.child(auth.currentUser?.uid.toString()).child(deletedNote.noteId)
//            .setValue(deletedNote)
        deletedNoteViewModel.insert(deletedNote,applicationContext)
    }


    private fun showNote(noteToShow: Int) {
        Log.i("Tag","$noteToShow")
        val intent = Intent(this, ShowNoteActivity::class.java)
        val b = Bundle()
        b.putString("key", adapter.getItem(noteToShow).noteId) //Your id
        adapter.getItem(noteToShow).title?.let { Log.i("Tag", it) }
        intent.putExtras(b) //Put your id to your next Intent

        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


//    fun editNote(note: Int) {
//        val intent =  Intent(this, EditNoteActivity::class.java)
//        val b = Bundle()
//        b.putString("key", adapter.getItem(note).noteId) //Your id
//
//        intent.putExtras(b) //Put your id to your next Intent
//        startActivity(intent)
//    }

    /* fun addNote(note:Note){
         adapter?.addNote(note)
     }*/
//    private fun sortOldestDate() {
//        adapter.sort { l, r -> l.noteId.compareTo(r.noteId) }
//       // recyclerView?.scrollToPosition(0)
//    }
//
//    private fun sortAlphabetical() {
//        adapter.sort { l, r -> l.title?.compareTo(r.title ?: "") ?: 0 }
//       // recyclerView?.scrollToPosition(0)
//    }
//
//    private fun sortNewestDate() {
//        adapter.sort { l, r -> r.noteId.compareTo(l.noteId) }
//      //  recyclerView?.scrollToPosition(0)
//    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val menuItem = menu.findItem(R.id.search)
        val searchView = menuItem.actionView as SearchView
        searchView.queryHint = "Type here to search for notes!"
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                val tempArr = ArrayList<Note>()
                noteViewModel.getAllNotes().observe(this@MainActivity){ listOfNotes ->
                    for (arr in listOfNotes) {
                        if (arr.title!!.toLowerCase(Locale.getDefault()).contains(newText.toString())||arr.description!!.toLowerCase(Locale.getDefault()).contains(newText.toString())) {
                            tempArr.add(arr)
                            adapter.setNote(tempArr)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
//                adapter.setNote(tempArr)
//                adapter.notifyDataSetChanged()
                return true
            }

        })

        MenuItemCompat.setOnActionExpandListener(
            menuItem,
            object : MenuItemCompat.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    fab.isVisible = false
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    fab.isVisible = true
                    return true
                }
            })

        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
       super.onPrepareOptionsMenu(menu)
        when {
            isNewFirst() -> {
                if (menu != null) {
                    menu.findItem(R.id.mNewFirst).isChecked = true
                }

            }
            isOldFirst()->{
                if (menu != null) {
                    menu.findItem(R.id.mOldFirst).isChecked = true
                }

            }
            isTitleFirst()->{
                if (menu != null) {
                    menu.findItem(R.id.mTitle).isChecked = true
                }
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {

            R.id.mNewFirst -> {
                item.isChecked = !item.isChecked

                noteViewModel.new()
                preferencesPrivate.edit().putBoolean(Constants.new, true).apply()
                notOldFirst()
                notTitleFirst()
                //lifecycleScope.launch(Dispatchers.IO){
                   // recyclerView!!.smoothSnapToPosition(0)
                //}
                //sortNewestDate()
                return true
            }
            R.id.mOldFirst -> {
                item.isChecked = !item.isChecked

                noteViewModel.old()
                preferencesPrivate.edit().putBoolean(Constants.old, true).apply()
                notNewFirst()
                notTitleFirst()
                //lifecycleScope.launch(Dispatchers.IO){
                    //recyclerView!!.smoothSnapToPosition(0)
                //}
                //sortOldestDate()

                return true
            }
            R.id.mTitle -> {
                item.isChecked = !item.isChecked

                noteViewModel.title()
                preferencesPrivate.edit().putBoolean(Constants.title, true).apply()
                notOldFirst()
                notNewFirst()
                //lifecycleScope.launch(Dispatchers.IO){
                   // recyclerView!!.smoothSnapToPosition(0)
                //}
                //sortAlphabetical()

                return true
            }

            R.id.mLogOut -> {
                Toast.makeText(this, "Log Out", Toast.LENGTH_SHORT).show()
                auth.signOut()
                val logoutIntent = Intent(this, LoginActivity::class.java)
                logoutIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                preferencesPrivate.edit().putBoolean(Constants.FIRST_RUN, true).apply()
                startActivity(logoutIntent)
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

/*    private fun titleFirstSort() {
        Log.i("","title is true")
        noteList.sortBy{it.title?.toLowerCase(Locale.ROOT) }


        adapter?.setNote(noteList)
    }

    private fun oldFirstSort() {
        Log.i("","oldest first is true")
        noteList.sortBy {  it.noteId.toLong() }
        adapter?.setNote(noteList)
    }

    private fun newFirstSort() {
        Log.i("","Newest first is true")
        noteList.sortBy {  it.noteId.toLong() }
        noteList.reverse()
        adapter?.setNote(noteList)
    }*/

    private fun displayScreen(id: Int) {

        // val fragment =  when (id){

        when (id) {
            R.id.deleted_notes -> {
                //supportFragmentManager.beginTransaction().replace(R.id.fragmentHolder, DeletedNotes(deletedNotesList)).commit()
                startActivity(Intent(this, DeletedNotesActivity::class.java))
            }
            R.id.settings->{
                startActivity(Intent(this, Settings::class.java))
            }

            /* R.id.nav_photos -> {
                 supportFragmentManager.beginTransaction().replace(R.id.relativelayout, PhotosFragment()).commit()
             }

             R.id.nav_movies -> {
                 supportFragmentManager.beginTransaction().replace(R.id.relativelayout, MoviesFragment()).commit()
             }

             R.id.nav_notifications -> {
                 Toast.makeText(this, "Clicked Notifications", Toast.LENGTH_SHORT).show()
             }

             R.id.nav_settings -> {
                 Toast.makeText(this, "Clicked Settings", Toast.LENGTH_SHORT).show()
             }

             R.id.nav_aboutUs -> {
                 Toast.makeText(this, "Clicked About Us", Toast.LENGTH_SHORT).show()
             }

             R.id.nav_privacyPolicy -> {
                 Toast.makeText(this, "Clicked Privacy Policy", Toast.LENGTH_SHORT).show()
             }*/
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        displayScreen(item.itemId)

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int = snapMode
            override fun getHorizontalSnapPreference(): Int = snapMode
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }

    override fun onClick(view: View, itemPosition: Int) {
        showNote(itemPosition)
    }
    private fun isFirstRun() = preferencesPrivate.getBoolean(Constants.FIRST_RUN, true)

    private fun consumeFirstRun() =
        preferencesPrivate.edit().putBoolean(Constants.FIRST_RUN, false).apply()

    private fun isTitleFirst() = preferencesPrivate.getBoolean(Constants.title,false)
    private fun isOldFirst() = preferencesPrivate.getBoolean(Constants.old,true)
    private fun isNewFirst() = preferencesPrivate.getBoolean(Constants.new,false)

    private fun notTitleFirst() = preferencesPrivate.edit().putBoolean(Constants.title,false).apply()
    private fun notOldFirst() = preferencesPrivate.edit().putBoolean(Constants.old,false).apply()
    private fun notNewFirst() = preferencesPrivate.edit().putBoolean(Constants.new,false).apply()

}