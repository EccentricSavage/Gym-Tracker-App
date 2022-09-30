package com.savage.gymtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // call action bar
        val actionBar = supportActionBar
        // show the back button
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val logProgressButtonClick = findViewById<Button>(R.id.btnLogProgress)
        logProgressButtonClick.setOnClickListener {
            val logProgressIntent = Intent(this, LogProgressActivity::class.java)
            startActivity(logProgressIntent)
        }

    }

    // enable back function
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}

// Possible updates

// TODO: Add update row functionality

// TODO: Add Toast


