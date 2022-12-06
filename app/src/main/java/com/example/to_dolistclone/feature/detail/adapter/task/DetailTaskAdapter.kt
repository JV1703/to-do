package com.example.to_dolistclone.feature.detail.adapter.task

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistclone.core.domain.model.Task
import com.example.to_dolistclone.databinding.DetailSubTaskItemBinding
import com.example.to_dolistclone.feature.detail.adapter.task.DetailTaskAdapter.DetailTaskViewHolder


class DetailTaskAdapter(private val taskListener: DetailTaskAdapterListener) :
    ListAdapter<Task, DetailTaskViewHolder>(DiffUtilCallback) {

    inner class DetailTaskViewHolder(private val binding: DetailSubTaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            setStrikeThrough(binding.task, task.isComplete)
            binding.checkbox.isChecked = task.isComplete
            binding.task.setText(task.task.trim())
        }

        fun setupListener() {
            binding.checkbox.setOnClickListener {

                val task = getItem(absoluteAdapterPosition)

                setStrikeThrough(binding.task, task.isComplete)

                taskListener.updateTaskCompletion(task.taskId, !task.isComplete)

            }

            binding.task.setOnFocusChangeListener { _, hasFocus ->
                if(!hasFocus){
                    if(binding.task.text.toString().trim().isEmpty()){
                        taskListener.deleteTask(getItem(absoluteAdapterPosition).taskId)
                    }else{
                        taskListener.updateTaskTitle(getItem(absoluteAdapterPosition).taskId, binding.task.text.toString().trim())
                    }
                }
            }
        }
    }

    private fun setStrikeThrough(textView: TextView, isComplete: Boolean){
        textView.paintFlags = if(isComplete) Paint.STRIKE_THRU_TEXT_FLAG else 0
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): DetailTaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DetailTaskViewHolder(
            DetailSubTaskItemBinding.inflate(
                layoutInflater, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: DetailTaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
        holder.setupListener()
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.taskId == newItem.taskId
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

}