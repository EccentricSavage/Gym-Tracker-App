package com.savage.gymtracker

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class LogProgressActivity : AppCompatActivity() {

    companion object {
        var selectedDateGlobal: LocalDate = LocalDate.now()

        // returns selectedDateGlobal in simple format
        fun returnSimpleDate(date: LocalDate): String {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyyyy")

            return selectedDateGlobal.format(formatter)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_progress)

        // call action bar
        val actionBar = supportActionBar
        // show the back button
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // define date button
        val btnDate = findViewById<Button>(R.id.tvDate)

        // get date from calendar
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // convert calendar date to datetime then set format
        fun dateConverter(year: Int, month: Int, day: Int): String {
            val selectedDate = LocalDate.parse("$year-${month+1}-$day", DateTimeFormatter.ofPattern("yyyy-M-d"))
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("eee, d MMMM u")
            return selectedDate.format(formatter)
        }

        // change button text to formatted selected date date
        btnDate.text = dateConverter(year, month, day)

        // onclick listener for calendar dialog
        btnDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, myear, mmonth, mdayOfMonth ->
                selectedDateGlobal = LocalDate.parse("$myear-${mmonth+1}-$mdayOfMonth", DateTimeFormatter.ofPattern("yyyy-M-d") )
                btnDate.text = dateConverter(myear, mmonth, mdayOfMonth)
                // make sure correct date is selected and refresh recyclerview
                DatabaseHandler.refresh()
                setupDataIntoRecyclerView()
            }, year, month, day)
            datePickerDialog.show()
        }

        // add onclick listener to add button
        val addButtonClicked = findViewById<Button>(R.id.btnAdd)
        addButtonClicked.setOnClickListener {
            val addIntent = Intent(this, AddActivity::class.java)
            startActivity(addIntent)
        }

        // add onclick listener to done button
        val doneButtonClicked = findViewById<Button>(R.id.btnDone)
        doneButtonClicked.setOnClickListener {
            finish()
        }

        setupDataIntoRecyclerView()

    }

    // show table of data
    private fun setupDataIntoRecyclerView(){
        if (getSetList().size >=0) {
            var rvSetItems = findViewById<RecyclerView>(R.id.rvSetItems)
            // set the layout manager
            rvSetItems.layoutManager = LinearLayoutManager(this)
            // Initialise adapter class and pass list
            val setAdapter = SetAdapter(this, getSetList())
            // set adapter instance to recyclerview and inflate
            rvSetItems.adapter = setAdapter
        }
    }

    // enable back function
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // retrieve set List
    private fun getSetList(): ArrayList<com.savage.gymtracker.Set> {
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        // make sure correct table is showing
//        DatabaseHandler.refresh()
        // call retrieveSets
        val setsList: ArrayList<com.savage.gymtracker.Set> = databaseHandler.retrieveSets()

        return setsList
    }

    override fun onResume() {
        super.onResume()
        setupDataIntoRecyclerView()
    }

    fun deleteRecordAlertDialog(set: Set) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete Record")
        //set message for alert dialog
        builder.setMessage("Are you sure you wants to delete ${set.reps} ${set.exercise} at ${set.weight}kg?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->

            //creating the instance of DatabaseHandler class
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            //calling the deleteSet method of DatabaseHandler class to delete record
            val status = databaseHandler.deleteSet(Set(set.id, set.exercise, set.weight, set.reps))
            if (status > -1) {
                Toast.makeText(
                    applicationContext,
                    "Record deleted successfully.",
                    Toast.LENGTH_LONG
                ).show()

                setupDataIntoRecyclerView()
            }

            dialogInterface.dismiss() // Dialog will be dismissed
        }
        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }




}