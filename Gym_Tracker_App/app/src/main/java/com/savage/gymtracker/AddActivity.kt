package com.savage.gymtracker

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AddActivity :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_item)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)


        val spinner = findViewById<Spinner>(R.id.spinExercise)
        
        spinner!!.onItemSelectedListener

        fun populateSpinner(newElement: Boolean) {
            // call databaseHandler and populate array
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            // create array of Exercise class
            val exArray = databaseHandler.retrieveExercises()
            // create array of Exercise Strings
            val exExArray = Array<String>(exArray.size) { i -> exArray[i].exercise }
            exExArray.sortBy { it }
            // Create an ArrayAdapter using a simple spinner layout and exercises
            val aa = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, exExArray)
            // Set layout to use when the list of choices appear
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Set Adapter to Spinner
            spinner.adapter = aa
            // Set starting selection
            if (newElement) {
                val index = exExArray.indexOf(exArray[exArray.size - 1].exercise)
                spinner.setSelection(index)
            }
        }
        populateSpinner(false)


        // Give functionality to add button
        val addButtonClicked = findViewById<Button>(R.id.btnAdd2)
        addButtonClicked.setOnClickListener {

            addRecord()

            finish()
        }
        // + button dialog box function
        fun addExercise(view: View){
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            builder.setTitle("Add Exercise")
            val dialogLayout = inflater.inflate(R.layout.add_exercise, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.etAddExercise)
            builder.setView(dialogLayout)
            builder.setPositiveButton("Add") {
                    dialogInterface, i ->
                    if (editText.text.toString() != "") {
                        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
                        databaseHandler.addExercise(Exercise(0, editText.text.toString()))
                        populateSpinner(true)
                    }
//                Toast.makeText(applicationContext, "Field was empty", Toast.LENGTH_SHORT).show()

            }
            builder.setNegativeButton("Cancel", null)

            builder.show()
        }
        // + button onclick
        val plusButtonClicked = findViewById<Button>(R.id.btnAddToExercise)
        plusButtonClicked.setOnClickListener {
            addExercise(it)
//            finish()

        }

        // - button dialog box function
        fun removeExercise(view: View){
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            builder.setTitle("Remove Exercise")
            // define currently selected exercise
            val curEx = spinner.selectedItem.toString()
            // confirmation message
            builder.setMessage("Are you sure you wants to remove '$curEx' from the list?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            //performing positive action
            builder.setPositiveButton("Yes") { dialogInterface, which ->

                //creating the instance of DatabaseHandler class
                val databaseHandler: DatabaseHandler = DatabaseHandler(this)
                //calling the deleteSet method of DatabaseHandler class to delete record
                val status = databaseHandler.deleteExercise(curEx)

                if (status > -1) {
                    Toast.makeText(
                        applicationContext,
                        "Record deleted successfully.",
                        Toast.LENGTH_LONG
                    ).show()
                populateSpinner(false) // refresh spinner without deleted item
                }

                dialogInterface.dismiss() // Dialog will be dismissed
            }

            //performing negative action
            builder.setNegativeButton("No") { dialogInterface, which ->
                dialogInterface.dismiss() // Dialog will be dismissed
            }
            // Create the AlertDialog
            val alertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
            alertDialog.show()  // show the dialog to UI

            }

        // - button onclick
        val minusButtonClicked = findViewById<Button>(R.id.btnRemoveFromExercise)
        minusButtonClicked.setOnClickListener {
            removeExercise(it)
        }
    }



    // enable back function
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // saving data to database
    private fun addRecord() {
        val exerciseData = findViewById<Spinner>(R.id.spinExercise).selectedItem.toString()
        val weightData = findViewById<EditText>(R.id.etWeight).text
        val repData = findViewById<EditText>(R.id.etReps).text

        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        if (exerciseData.isNotEmpty() && weightData.isNotEmpty() && repData.isNotEmpty()) {
            val status =
                databaseHandler.addSet(Set(0, exerciseData, weightData.toString().toInt(), repData.toString().toInt()))
            if (status > -1) {
                Toast.makeText(applicationContext, "Set saved", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Cannot have blank field(s)",
                Toast.LENGTH_LONG
            ).show()
        }
    }

}