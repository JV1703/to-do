package com.example.to_dolistclone.feature.detail.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistclone.core.domain.model.Task
import com.example.to_dolistclone.databinding.DetailSubTaskItemBinding
import com.example.to_dolistclone.feature.detail.adapter.DetailTaskAdapter.DetailTaskViewHolder


class DetailTaskAdapter(private val itemTouchHelperContract: DragAndDropController.ItemTouchHelperContract, private val taskListener: DetailTaskAdapterListener) :
    ListAdapter<Task, DetailTaskViewHolder>(DiffUtilCallback) {

    inner class DetailTaskViewHolder(private val binding: DetailSubTaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
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
                taskListener.updateTaskCompletion(getItem(absoluteAdapterPosition).taskId, isChecked)
            }

            binding.task.setOnFocusChangeListener { view, hasFocus ->
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