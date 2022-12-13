package com.example.to_dolistclone.core.data.remote.firebase.abstraction

import android.net.Uri
import com.example.to_dolistclone.core.data.remote.model.AttachmentNetwork

interface AttachmentFirestore {
    suspend fun upsertAttachment(userId: String, attachment: AttachmentNetwork)
    suspend fun getAttachment(userId: String, attachmentId: String): AttachmentNetwork?
    suspend fun getAttachments(userId: String): List<AttachmentNetwork>
    suspend fun deleteAttachment(userId: String, attachmentId: String)
    suspend fun updateAttachment(userId: String, attachmentId: String, field: Map<String, Any>)
    suspend fun uploadAttachment(userId: String, attachmentUri: Uri)
    suspend fun downloadAttachment(path: String)
    suspend fun deleteAttachmentFromFirebaseStorage(path: String)
}