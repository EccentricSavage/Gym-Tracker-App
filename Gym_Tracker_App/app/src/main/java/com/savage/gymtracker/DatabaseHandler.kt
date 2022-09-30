package com.savage.gymtracker

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

        companion object {
            private var date: String = LogProgressActivity.returnSimpleDate(LogProgressActivity.selectedDateGlobal)
            private val DATABASE_VERSION = 1
            private val DATABASE_NAME = "WorkoutsDatabase"

            private var TABLE_WORKOUTS = "WorkoutsTable${date}"

            private val KEY_ID = "_id"
            private val KEY_EXERCISE = "exercise"
            private val KEY_WEIGHT = "weight"
            private val KEY_REPS = "reps"

            private val TABLE_EXERCISES = "ExercisesTable"

            fun refresh() {
                // inelegant solution to the above vars not automatically updating
                val currentDate: String = LogProgressActivity.returnSimpleDate(LogProgressActivity.selectedDateGlobal)
                TABLE_WORKOUTS = "WorkoutsTable${currentDate}"
            }
        }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_WORKOUT_TABLE =
            ("CREATE TABLE $TABLE_WORKOUTS($KEY_ID INTEGER PRIMARY KEY,$KEY_EXERCISE TEXT,$KEY_WEIGHT INT,$KEY_REPS INT)")
        db?.execSQL(CREATE_WORKOUT_TABLE)

        val CREATE_EXERCISE_TABLE =
            ("CREATE TABLE $TABLE_EXERCISES($KEY_ID INTEGER PRIMARY KEY,$KEY_EXERCISE TEXT)")
        db?.execSQL(CREATE_EXERCISE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUTS")
        onCreate(db)
    }

    private fun createTableIfNotExists(db: SQLiteDatabase?) {
        val CREATE_WORKOUT_TABLE =
            ("CREATE TABLE IF NOT EXISTS $TABLE_WORKOUTS($KEY_ID INTEGER PRIMARY KEY,$KEY_EXERCISE TEXT,$KEY_WEIGHT INT,$KEY_REPS INT)")
        db?.execSQL(CREATE_WORKOUT_TABLE)
    }

    fun addSet(set: Set): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_EXERCISE, set.exercise)
        contentValues.put(KEY_WEIGHT, set.weight)
        contentValues.put(KEY_REPS, set.reps)

        // Insert Query
        val success = db.insert(TABLE_WORKOUTS, null, contentValues)

        db.close() // Closing database connection
        return success
    }

    fun retrieveSets(): ArrayList<Set> {
        val setList: ArrayList<Set> = ArrayList()

        val db = this.readableDatabase

        // check if db exists
        createTableIfNotExists(db)

        // Query records
        val selectQuery = "SELECT  * FROM $TABLE_WORKOUTS"


        // Cursor used to read and add records
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return setList
        }

        var id: Int
        var exercise: String
        var weight: Int
        var reps: Int

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
                exercise = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXERCISE))
                weight = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_WEIGHT))
                reps = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_REPS))
                // check for -1 index

                val set = Set(id, exercise, weight, reps)
                setList.add(set)

            } while (cursor.moveToNext())
        }
        cursor.close()
        return setList
    }

    fun updateSet(set: Set): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_EXERCISE, set.exercise)
        contentValues.put(KEY_REPS, set.reps)
        contentValues.put(KEY_WEIGHT, set.weight)

        // Update row
        val success = db.update(TABLE_WORKOUTS, contentValues, KEY_ID+"="+set.id, null)

        db.close()
        return success
    }

    fun deleteSet(set: Set): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, set.id)
        // Delete row
        val success = db.delete(TABLE_WORKOUTS, KEY_ID+"="+set.id, null)
        db.close()
        return success

    }

    fun addExercise(ex: Exercise): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_EXERCISE, ex.exercise)

        // Insert Query
        val success = db.insert(TABLE_EXERCISES, null, contentValues)

        db.close() // Closing database connection
        return success
    }

    fun retrieveExercises(): ArrayList<Exercise> {
        val exList: ArrayList<Exercise> = ArrayList()

        val db = this.readableDatabase

        val CREATE_EXERCISES_TABLE =
            ("CREATE TABLE IF NOT EXISTS $TABLE_EXERCISES($KEY_ID INTEGER PRIMARY KEY,$KEY_EXERCISE TEXT)")
        db?.execSQL(CREATE_EXERCISES_TABLE)

        // Query records
        val selectQuery = "SELECT  * FROM $TABLE_EXERCISES"

        // Cursor used to read and add records
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            when(e) {
                is SQLiteException, is NullPointerException -> {
                    db.execSQL(selectQuery)
                    return exList
                }
            }

        }

        var id: Int
        var exercise: String

        try {
            if (cursor.moveToFirst()) {
                do {
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
                    exercise = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXERCISE))
                    // check for -1 index

                    val ex = Exercise(id, exercise)
                    exList.add(ex)

                } while (cursor.moveToNext())
            }
        } catch (e: java.lang.NullPointerException) {
            cursor.close()
            return exList
        }

        cursor.close()
        return exList
    }

    fun deleteExercise(exercise: String): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_EXERCISE, exercise)
        // Delete row
        val success = db.delete(TABLE_EXERCISES, "$KEY_EXERCISE='$exercise'", null)
        db.close()
        return success
    }


}