package com.example.to_dolistclone.feature.profile.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.databinding.ProfileFragmentWeeklyTaskItemBinding
import java.time.LocalDateTime
import java.util.*

class UpcomingWeeklyTodoAdapter(private val dateUtil: DateUtil) :
    ListAdapter<Todo, UpcomingWeeklyTodoAdapter.UpcomingWeeklyTodoViewHolder>(DiffUtilCallback) {

    inner class UpcomingWeeklyTodoViewHolder(
        private val binding: ProfileFragmentWeeklyTaskItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: Todo) {
            binding.todoItemTv.text = todo.title
            todo.deadline?.let {
                binding.dueDateTv.text = getDateString(dateUtil.toLocalDateTime(it))
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): UpcomingWeeklyTodoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UpcomingWeeklyTodoViewHolder(
            ProfileFragmentWeeklyTaskItemBinding.inflate(
                layoutInflater, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: UpcomingWeeklyTodoViewHolder, position: Int) {
        val todo = getItem(position)
        holder.bind(todo)
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.todoId == newItem.todoId
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }
    }

    private fun getDateString(localDateTime: LocalDateTime): String {
        return if (localDateTime.year == LocalDateTime.now().atZone(dateUtil.zoneId).year) {
            dateUtil.toString(localDateTime, "EEE, MMM dd", Locale.getDefault())
        } else {
            dateUtil.toString(localDateTime, "EEE, MMM dd, yyyy", Locale.getDefault())
        }
    }
}