package com.example.to_dolistclone.feature.todo.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.databinding.TodoItemBinding

class TodosAdapter(
    private val dateUtil: DateUtil,
    private val onClickCheckBox: (Todo) -> Unit,
    private val onClickNavigation: (Todo) -> Unit
) :
    ListAdapter<Todo, TodosAdapter.TodosAdapterViewHolder>(DiffUtilCallback) {

    inner class TodosAdapterViewHolder(
        private val binding: TodoItemBinding,
        private val dateUtil: DateUtil
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun setupListener() {
            binding.taskCb.setOnCheckedChangeListener { _, isChecked ->
                onClickCheckBox(
                    getItem(absoluteAdapterPosition).copy(
                        isComplete = isChecked,
                        completedOn = if (isChecked) dateUtil.getCurrentDateTimeLong() else null
                    )
                )

                if (isChecked) {
                    binding.taskTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    binding.taskTv.paintFlags = 0
                }
            }
        }

        fun bind(todo: Todo) {
            binding.taskCb.isChecked = todo.isComplete
            binding.taskTv.text = todo.title

            if (todo.isComplete) {
                binding.taskCb.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.taskCb.paintFlags = 0
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodosAdapterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TodosAdapterViewHolder(
            TodoItemBinding.inflate(layoutInflater, parent, false),
            dateUtil
        )
    }

    override fun onBindViewHolder(holder: TodosAdapterViewHolder, position: Int) {
        val todo = getItem(position)
        holder.setupListener()
        holder.bind(todo)
        holder.itemView.setOnClickListener {
            onClickNavigation(todo)
        }
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<Todo>() {

        override fun areItemsTheSame(
            oldItem: Todo, newItem: Todo
        ): Boolean {
            return oldItem.todoId == newItem.todoId
        }

        override fun areContentsTheSame(
            oldItem: Todo, newItem: Todo
        ): Boolean {
            return oldItem == newItem
        }
    }

}