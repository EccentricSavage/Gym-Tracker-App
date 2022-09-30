package com.savage.gymtracker

class Set(id: Int, exercise: String, weight: Int, reps: Int ) {
    var id: Int
    var exercise: String
    var weight: Int
    var reps: Int


    init {
        this.id = id
        this.exercise = exercise
        this.weight = weight
        this.reps = reps

    }

}