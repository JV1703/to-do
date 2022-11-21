package com.example.to_dolistclone.feature.home.adapter

import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistclone.databinding.SubTaskItemBinding

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val tasksProxy = arrayListOf<TaskProxy>()

    fun addTask() {
        tasksProxy.add(TaskProxy(position = tasksProxy.size))
        this.notifyItemInserted(tasksProxy.size - 1)
    }

    fun getTasks() = tasksProxy

    fun deleteTask(position: Int) {
        tasksProxy.removeAt(position)
        this.notifyItemRemoved(position)
        Log.i("TasksAdapter", "$tasksProxy")
    }

    inner class TaskViewHolder(private val binding: SubTaskItemBinding) :
        RecyclerView.ViewHolder(binding.root), TextWatcher {

        fun bind(task: TaskProxy) {
            binding.checkbox.isChecked = task.isComplete
            binding.task.setText(task.task.trim())
        }

        fun setupListener() {
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.task.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    binding.task.paintFlags = 0
                }
                tasksProxy[absoluteAdapterPosition].isComplete = isChecked
            }
            binding.task.addTextChangedListener(this)
            binding.delete.setOnClickListener {
                deleteTask(absoluteAdapterPosition)
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
            tasksProxy[absoluteAdapterPosition].task = p0.toString().trim()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TaskViewHolder(SubTaskItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasksProxy[position]
        holder.bind(task)
        holder.setupListener()
    }

    override fun getItemCount(): Int {
        return tasksProxy.size
    }
}