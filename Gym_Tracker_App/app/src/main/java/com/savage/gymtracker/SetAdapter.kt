package com.savage.gymtracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SetAdapter( val context: Context,
    private var sets: ArrayList<com.savage.gymtracker.Set>
    ) : RecyclerView.Adapter<SetAdapter.SetViewHolder>(){

    class SetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        return SetViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_set,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val curSet = sets[position]

        holder.itemView.apply {
            findViewById<TextView>(R.id.tvExercise).text = curSet.exercise
            findViewById<TextView>(R.id.tvWeight).text = "${curSet.weight.toString()}kg"
            findViewById<TextView>(R.id.tvReps).text = curSet.reps.toString()
        }

        holder.itemView.findViewById<ImageButton>(R.id.ibDelete).setOnClickListener { view ->
            if (context is LogProgressActivity) {
                context.deleteRecordAlertDialog(curSet)
            }
        }
    }

    override fun getItemCount(): Int {
        return sets.size
    }

    fun updateData() {

    }

}