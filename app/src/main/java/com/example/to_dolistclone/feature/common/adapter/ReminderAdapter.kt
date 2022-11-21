package com.example.to_dolistclone.feature.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistclone.databinding.ReminderItemBinding

class ReminderAdapter(private val onClicked: (String) -> Unit) :
    RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    private val reminders = listOf(
        "Same with due date",
        "5 minutes before",
        "10 minutes before",
        "15 minutes before",
        "30 minutes before",
        "1 day before"
    )

    private var checkedPosition: Int = 0

    inner class ReminderViewHolder(private val binding: ReminderItemBinding) :
        RecyclerView.ViewHolder(binding.root), OnCheckedChangeListener {

        fun bind(reminder: String) {
            binding.reminder.text = reminder
        }

        fun setupListeners() {
            binding.checkbox.setOnCheckedChangeListener(this)
        }

        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
            binding.checkbox.isChecked = p1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ReminderViewHolder(ReminderItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.setupListeners()
        holder.bind(reminder)
        holder.itemView.setOnClickListener {
            checkedPosition = reminders.indexOf(reminder)
            onClicked(reminder)
        }
    }

    override fun getItemCount(): Int = reminders.size
}