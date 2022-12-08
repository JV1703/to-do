package com.example.to_dolistclone.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.example.note.utils.MainCoroutineRule
import com.example.to_dolistclone.core.data.remote.firebase.*
import com.example.to_dolistclone.core.data.remote.firebase.abstraction.AttachmentFirestore
import com.example.to_dolistclone.core.data.remote.firebase.implementation.ACTIVE_COLLECTION
import com.example.to_dolistclone.core.data.remote.firebase.implementation.ATTACHMENT_COLLECTION
import com.example.to_dolistclone.core.data.remote.firebase.implementation.AttachmentFirestoreImpl
import com.example.to_dolistclone.core.data.remote.firebase.implementation.TEST_USER_ID_DOCUMENT
import com.example.to_dolistclone.core.data.remote.model.AttachmentNetwork
import com.example.to_dolistclone.utils.TestDataGenerator
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
@SmallTest
@HiltAndroidTest
class AttachmentFirestoreTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mainDispatcherRule = MainCoroutineRule()

    @get:Rule(order = 2)
    val instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    @Inject
    lateinit var testDataGenerator: TestDataGenerator

    private lateinit var attachmentFirestore: AttachmentFirestore
    private lateinit var attachmentNetworkList: MutableList<AttachmentNetwork>
    private lateinit var TEST_ATTACHMENT_TODO_REF: String

    @Before
    fun setup() {
        hiltRule.inject()
        attachmentFirestore = AttachmentFirestoreImpl(firebaseFirestore)
        insertAttachments()
        TEST_ATTACHMENT_TODO_REF =
            attachmentNetworkList[Random.nextInt(attachmentNetworkList.size - 1)].todoRefId
    }

    private fun insertAttachments() {
        attachmentNetworkList = testDataGenerator.produceListOfAttachmentNetwork().toMutableList()
        attachmentNetworkList.forEach { attachment ->
            firebaseFirestore.collection(ACTIVE_COLLECTION).document(TEST_USER_ID_DOCUMENT)
                .collection(ATTACHMENT_COLLECTION).document(attachment.attachmentId).set(attachment)
        }
    }

    private fun generateSingleAttachmentNetwork(todoRefId: String) = AttachmentNetwork(
        attachmentId = UUID.randomUUID().toString(),
        name = "attachment name",
        uri = "uri $1",
        type = "jpg",
        size = 1024,
        todoRefId = todoRefId
    )

    @Test
    fun insertAttachment() = runTest {
        val todoRefId = UUID.randomUUID().toString()
        val newAttachment = generateSingleAttachmentNetwork(todoRefId)
        attachmentFirestore.upsertAttachment(TEST_USER_ID_DOCUMENT, newAttachment)
        val networkData = attachmentFirestore.getAttachments(TEST_USER_ID_DOCUMENT)
        attachmentNetworkList.add(newAttachment)
        assertTrue(networkData.containsAll(attachmentNetworkList))
        attachmentFirestore.deleteAttachment(TEST_USER_ID_DOCUMENT, newAttachment.attachmentId)
    }

    @Test
    fun updateAttachment() = runTest {
        val noteToUpdate =
            attachmentNetworkList[Random.nextInt(attachmentNetworkList.size - 1)].copy(name = "Banana")
        attachmentFirestore.upsertAttachment(TEST_USER_ID_DOCUMENT, noteToUpdate)

        val networkData = attachmentFirestore.getAttachments(TEST_USER_ID_DOCUMENT)
        assertTrue(networkData.contains(noteToUpdate))
    }

    @Test
    fun getAttachment() = runTest {
        val attachmentToGet = attachmentNetworkList[Random.nextInt(attachmentNetworkList.size - 1)]
        val networkData =
            attachmentFirestore.getAttachment(TEST_USER_ID_DOCUMENT, attachmentToGet.attachmentId)
        assertEquals(attachmentToGet, networkData)
    }

    @Test
    fun getAttachments() = runTest {
        val networkData = attachmentFirestore.getAttachments(TEST_USER_ID_DOCUMENT)
        assertTrue(networkData.containsAll(attachmentNetworkList))
    }

    @Test
    fun deleteAttachment() = runTest {
        val attachmentToDelete =
            attachmentNetworkList[Random.nextInt(attachmentNetworkList.size - 1)]
        attachmentFirestore.deleteAttachment(TEST_USER_ID_DOCUMENT, attachmentToDelete.attachmentId)
        val networkData = attachmentFirestore.getAttachments(TEST_USER_ID_DOCUMENT)
        assertTrue(!networkData.contains(attachmentToDelete))
    }
}