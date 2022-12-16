package com.example.to_dolistclone.core.data.remote.firebase.implementation

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.example.to_dolistclone.core.common.FileManager
import com.example.to_dolistclone.core.data.remote.firebase.abstraction.AttachmentFirestore
import com.example.to_dolistclone.core.data.remote.model.AttachmentNetwork
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

val ATTACHMENT_COLLECTION = "Attachments"

class AttachmentFirestoreImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val fileManager: FileManager
) : AttachmentFirestore {

    override suspend fun upsertAttachment(userId: String, attachment: AttachmentNetwork) {
        firestore.collection(ACTIVE_COLLECTION).document(userId).collection(ATTACHMENT_COLLECTION)
            .document(attachment.attachmentId).set(attachment).await()
    }

    override suspend fun getAttachment(userId: String, attachmentId: String): AttachmentNetwork? {
        return firestore.collection(ACTIVE_COLLECTION).document(userId)
            .collection(ATTACHMENT_COLLECTION).document(attachmentId).get().await()
            .toObject(AttachmentNetwork::class.java)
    }

    override suspend fun getAttachments(userId: String): List<AttachmentNetwork> {
        return firestore.collection(ACTIVE_COLLECTION).document(userId)
            .collection(ATTACHMENT_COLLECTION).get().await()
            .toObjects(AttachmentNetwork::class.java)
    }

    override suspend fun deleteAttachment(userId: String, attachmentId: String) {
        firestore.collection(ACTIVE_COLLECTION).document(userId).collection(ATTACHMENT_COLLECTION)
            .document(attachmentId).delete().await()
    }

    override suspend fun updateAttachment(
        userId: String, attachmentId: String, field: Map<String, Any>
    ) {
        firestore.collection(ACTIVE_COLLECTION).document(userId).collection(ATTACHMENT_COLLECTION)
            .document(attachmentId).set(field).await()
    }

    override suspend fun uploadAttachment(userId: String, attachmentUri: Uri){
        val storageRef = storage.reference
        val destination = fileManager.generateNetworkStorageDestination(userId, attachmentUri)
        val attachmentRef = storageRef.child(destination)
        attachmentRef.putFile(attachmentUri).await()

    }

    override suspend fun downloadAttachment(path: String) {
        val storageRef = storage.reference
        val fileLocation = storageRef.child(path)
        val uri = path.toUri()
        val file = File(uri.path!!)
        val localFile = File(fileManager.generateInternalStorageDestination(
            uri = uri
        ))
//        fileLocation.getFile(localFile).addOnSuccessListener {
//            Log.i("download file", "${localFile.name} have been downloaded")
//        }.addOnFailureListener{
//            Log.e("download file", "${localFile.name} download fail")
//        }
        fileLocation.getFile(localFile).await()
    }

    override suspend fun deleteAttachmentFromFirebaseStorage(path: String) {
        val storageRef = storage.reference
        val fileRef = storageRef.child(path)
        fileRef.delete().await()
    }
}