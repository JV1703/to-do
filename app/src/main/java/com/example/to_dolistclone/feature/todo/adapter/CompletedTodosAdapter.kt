package com.example.to_dolistclone.feature.todo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.databinding.CompletedTodosContentItemBinding
import com.example.to_dolistclone.databinding.CompletedTodosHeaderItemBinding
import com.example.to_dolistclone.databinding.CompletedTodosHeaderItemStartBinding
import java.util.*

class CompletedTodosAdapter(
    private val dateUtil: DateUtil,
    private val clickListener: CompletedTodosAdapterClickListener
) :
    ListAdapter<CompletedTodosHelper, RecyclerView.ViewHolder>(
        DiffUtilCallback
    ) {

    inner class HeaderStartViewHolder(private val binding: CompletedTodosHeaderItemStartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CompletedTodosHelper.HeaderStart) {
            val completedOn = dateUtil.toLocalDateTime(item.completedOn)
            binding.completedDateTv.text =
                dateUtil.toString(completedOn, "yyyy/MM/dd", Locale.getDefault())
        }
    }

    inner class HeaderViewHolder(private val binding: CompletedTodosHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CompletedTodosHelper.Header) {
            val completedOn = dateUtil.toLocalDateTime(item.completedOn)
            binding.completedDateTv.text =
                dateUtil.toString(completedOn, "yyyy/MM/dd", Locale.getDefault())
        }
    }

    inner class ContentViewHolder(private val binding: CompletedTodosContentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setupListener() {
            val todo = getItem(absoluteAdapterPosition) as CompletedTodosHelper.CompletedTodos

            binding.todoContainer.setOnClickListener {
                clickListener.navigate(todo.todo)
            }

            binding.todoCompletionCheckbox.setOnClickListener {
                val isComplete = todo.todo.isComplete
                clickListener.complete(todo.todo.todoId, !isComplete)
            }
        }

        fun bind(todo: CompletedTodosHelper.CompletedTodos) {
            binding.todoTitleTv.text = todo.todo.title

            val dueDate = dateUtil.toLocalDateTime(todo.todo.completedOn!!)
            binding.dueDateTv.text = dateUtil.toString(dueDate, "MM-dd", Locale.getDefault())
            binding.todoCompletionCheckbox.isChecked = todo.todo.isComplete
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TimelineViewType.VIEW_TYPE_HEADER_START.viewType -> {
                HeaderStartViewHolder(
                    CompletedTodosHeaderItemStartBinding.inflate(layoutInflater, parent, false)
                )
            }
            TimelineViewType.VIEW_TYPE_HEADER.viewType -> {
                HeaderViewHolder(
                    CompletedTodosHeaderItemBinding.inflate(layoutInflater, parent, false)
                )
            }
            TimelineViewType.VIEW_TYPE_CONTENT.viewType -> {
                ContentViewHolder(
                    CompletedTodosContentItemBinding.inflate(
                        layoutInflater, parent, false
                    )
                )
            }
            else -> throw IllegalArgumentException("Invalid View Type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is HeaderStartViewHolder -> {
                holder.bind(item as CompletedTodosHelper.HeaderStart)
            }
            is HeaderViewHolder -> {
                holder.bind(item as CompletedTodosHelper.Header)
            }
            is ContentViewHolder -> {
                holder.setupListener()
                holder.bind(item as CompletedTodosHelper.CompletedTodos)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CompletedTodosHelper.HeaderStart -> TimelineViewType.VIEW_TYPE_HEADER_START.viewType
            is CompletedTodosHelper.Header -> TimelineViewType.VIEW_TYPE_HEADER.viewType
            is CompletedTodosHelper.CompletedTodos -> TimelineViewType.VIEW_TYPE_CONTENT.viewType
        }
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<CompletedTodosHelper>() {
        override fun areItemsTheSame(
            oldItem: CompletedTodosHelper, newItem: CompletedTodosHelper
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CompletedTodosHelper, newItem: CompletedTodosHelper
        ): Boolean {
            return oldItem == newItem
        }
    }

}