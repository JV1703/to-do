package com.example.to_dolistclone.core.data.remote.firebase.abstraction

import com.example.to_dolistclone.core.data.remote.model.AttachmentNetwork

interface AttachmentFirestore {
    suspend fun upsertAttachment(userId: String, attachment: AttachmentNetwork)
    suspend fun getAttachment(userId: String, attachmentId: String): AttachmentNetwork?
    suspend fun getAttachments(userId: String): List<AttachmentNetwork>
    suspend fun deleteAttachment(userId: String, attachmentId: String)
}