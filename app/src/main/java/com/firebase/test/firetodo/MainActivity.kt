package com.firebase.test.firetodo

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.firebase.test.firetodo.adapter.ToDoItemAdapter
import com.firebase.test.firetodo.listeners.ItemRowListener
import com.firebase.test.firetodo.model.Constants
import com.firebase.test.firetodo.model.ToDoItem
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ItemRowListener {

    lateinit var database: DatabaseReference
    var toDoItemList: MutableList<ToDoItem>? = null
    lateinit var listAdapter: ToDoItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().reference
        toDoItemList = mutableListOf<ToDoItem>()
        listAdapter = ToDoItemAdapter(this, toDoItemList!!)
        items_list.adapter = listAdapter
        database.orderByKey().addListenerForSingleValueEvent(itemListener)
    }

    fun addNewItem(view: View) {

        val alert = AlertDialog.Builder(this)
        val itemEditText = EditText(this)
        alert.setMessage("Add New Item")
        alert.setTitle("Enter To Do Item Text")
        alert.setView(itemEditText)
        alert.setPositiveButton("Submit") { dialog, positiveButton ->
            val todoItem = ToDoItem()
            todoItem.itemText = itemEditText.text.toString()
            todoItem.done = false
            //We first make a push so that a new item is made with a unique ID
            val newItem = database.child(Constants.FIREBASE_ITEM).push()
            todoItem.objectId = newItem.key
            //then, we used the reference to set the value on that ID
            newItem.setValue(todoItem)
            dialog.dismiss()
            Toast.makeText(this, "Item saved with ID " + todoItem.objectId, Toast.LENGTH_SHORT).show()
        }
        alert.show()

    }

    var itemListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            addDataToList(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }

    private fun addDataToList(dataSnapshot: DataSnapshot) {
        val items = dataSnapshot.children.iterator()
        //Check if current database contains any collection
        if (items.hasNext()) {
            val toDoListindex = items.next()
            val itemsIterator = toDoListindex.children.iterator()

            //check if the collection has any to do items or not
            while (itemsIterator.hasNext()) {
                //get current item
                val currentItem = itemsIterator.next()
                val todoItem = ToDoItem()
                //get current data in a map
                val map = currentItem.getValue() as HashMap<String, Any>
                //key will return Firebase ID
                todoItem.objectId = currentItem.key
                todoItem.done = map.get("done") as Boolean
                todoItem.itemText = map.get("itemText") as String
                toDoItemList?.add(todoItem);
            }
        }
        //alert adapter that has changed
        listAdapter.notifyDataSetChanged()
    }

    override fun modifyItemState(itemObjectId: String, isDone: Boolean) {
        val itemReference = database.child(Constants.FIREBASE_ITEM).child(itemObjectId)
        itemReference.child("done").setValue(isDone);
    }

    override fun onItemDelete(itemObjectId: String) {
        val itemReference = database.child(Constants.FIREBASE_ITEM).child(itemObjectId)
        itemReference.removeValue()
    }
}
