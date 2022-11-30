package com.example.to_dolistclone.feature.detail.adapter.attachment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistclone.core.domain.model.Attachment
import com.example.to_dolistclone.databinding.DetailAttachmentItemBinding

class DetailAttachmentAdapter(private val clickListener: DetailAttachmentAdapterListener) :
    ListAdapter<Attachment, DetailAttachmentAdapter.DetailAttachmentViewHolder>(DiffUtilCallback) {

    inner class DetailAttachmentViewHolder(private val binding: DetailAttachmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setupListener() {

            val attachment: Attachment = getItem(absoluteAdapterPosition)

            binding.deleteIv.setOnClickListener {
                clickListener.deleteAttachment(attachment = attachment)
            }

            itemView.setOnClickListener {
                clickListener.openFile(attachment = attachment)
            }

        }

        fun bind(attachment: Attachment) {
            binding.contentTypeTv.text = attachment.type
            binding.fileNameTv.text = attachment.name
            binding.fileSizeTv.text = getFileSize(attachment.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailAttachmentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DetailAttachmentViewHolder(
            DetailAttachmentItemBinding.inflate(
                layoutInflater, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: DetailAttachmentViewHolder, position: Int) {
        val attachment = getItem(position)
        holder.setupListener()
        holder.bind(attachment)
        holder.itemView.setOnClickListener {
            clickListener.openFile(attachment)
        }
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<Attachment>() {
        override fun areItemsTheSame(oldItem: Attachment, newItem: Attachment): Boolean {
            return oldItem.attachmentId == newItem.attachmentId
        }

        override fun areContentsTheSame(oldItem: Attachment, newItem: Attachment): Boolean {
            return oldItem == newItem
        }
    }

    private fun fileSizeInKb(size: Long): Long {
        return size / 1024
    }

    private fun fileSizeInMb(size: Long): Long {
        return fileSizeInKb(size) / 1024
    }

    private fun getFileSize(size: Long): String {
        val kb = fileSizeInKb(size)
        return if (kb >= 1024) {
            "${fileSizeInMb(size)} MB"
        } else {
            "$kb KB"
        }
    }
}