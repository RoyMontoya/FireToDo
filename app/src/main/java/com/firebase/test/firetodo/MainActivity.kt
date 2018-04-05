package com.firebase.test.firetodo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.firebase.test.firetodo.model.Constants
import com.firebase.test.firetodo.model.ToDoItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance().reference
    }

     fun addNewItem(view: View){

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
}
