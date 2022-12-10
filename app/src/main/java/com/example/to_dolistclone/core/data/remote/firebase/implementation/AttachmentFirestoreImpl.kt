package com.example.to_dolistclone.core.data.remote.firebase.implementation

import com.example.to_dolistclone.core.data.remote.firebase.abstraction.AttachmentFirestore
import com.example.to_dolistclone.core.data.remote.model.AttachmentNetwork
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

val ATTACHMENT_COLLECTION = "Attachments"

class AttachmentFirestoreImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    AttachmentFirestore {

    override suspend fun upsertAttachment(userId: String, attachment: AttachmentNetwork) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(ATTACHMENT_COLLECTION)
            .document(attachment.attachmentId)
            .set(attachment)
            .await()
    }

    override suspend fun getAttachment(userId: String, attachmentId: String): AttachmentNetwork? {
        return firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(ATTACHMENT_COLLECTION)
            .document(attachmentId)
            .get()
            .await()
            .toObject(AttachmentNetwork::class.java)
    }

    override suspend fun getAttachments(userId: String): List<AttachmentNetwork> {
        return firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(ATTACHMENT_COLLECTION)
            .get()
            .await()
            .toObjects(AttachmentNetwork::class.java)
    }

    override suspend fun deleteAttachment(userId: String, attachmentId: String) {
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(ATTACHMENT_COLLECTION)
            .document(attachmentId)
            .delete()
            .await()
    }

    override suspend fun updateAttachment(userId: String, attachmentId: String, field: Map<String, Any>){
        firestore
            .collection(ACTIVE_COLLECTION)
            .document(userId)
            .collection(ATTACHMENT_COLLECTION)
            .document(attachmentId)
            .set(field)
            .await()
    }

}