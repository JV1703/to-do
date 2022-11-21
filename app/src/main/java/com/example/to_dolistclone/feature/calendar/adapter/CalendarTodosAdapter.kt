package com.example.to_dolistclone.feature.calendar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.databinding.CalendarTodoItemBinding
import java.util.*

class CalendarTodosAdapter(private val dateUtil: DateUtil, private val onClick: (Todo) -> Unit) :
    ListAdapter<Todo, CalendarTodosAdapter.CalendarTodosViewHolder>(DiffUtilCallback) {

    class CalendarTodosViewHolder(
        private val binding: CalendarTodoItemBinding,
        private val dateUtil: DateUtil
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: Todo) {
            binding.titleTv.text = todo.title

            if (todo.reminder != null) {
                binding.reminderTv.text =
                    dateUtil.toString(todo.reminder, "hh:mm a", Locale.getDefault())
            }

            binding.reminderIv.isGone = todo.reminder == null
            binding.reminderTv.isGone = todo.reminder == null
            binding.attachmentIv.isGone = !todo.attachments
            binding.tasksIv.isGone = !todo.tasks
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarTodosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CalendarTodosViewHolder(
            CalendarTodoItemBinding.inflate(
                layoutInflater, parent, false
            ),
            dateUtil
        )
    }

    override fun onBindViewHolder(holder: CalendarTodosViewHolder, position: Int) {
        val todo = getItem(position)
        holder.bind(todo)
        holder.itemView.setOnClickListener {
            onClick(todo)
        }
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.todoId == newItem.todoId
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }

    }

}