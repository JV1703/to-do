package com.example.to_dolistclone.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.example.note.utils.MainCoroutineRule
import com.example.to_dolistclone.core.data.remote.firebase.*
import com.example.to_dolistclone.core.data.remote.model.TodoCategoryNetwork
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
class TodoCategoryNetworkTest {

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

    private lateinit var todoCategoryFirestore: TodoCategoryFirestoreImpl
    private lateinit var todoCategoryNetworkList: MutableList<TodoCategoryNetwork>
    private lateinit var TEST_ATTACHMENT_TODO_REF: String

    @Before
    fun setup() {
        hiltRule.inject()
        todoCategoryFirestore = TodoCategoryFirestoreImpl(firebaseFirestore)
        insertAttachments()
    }

    private fun insertAttachments() {
        todoCategoryNetworkList =
            testDataGenerator.produceListOfTodoCategoryNetwork().toMutableList()
        todoCategoryNetworkList.forEach { todoCategory ->
            firebaseFirestore.collection(ACTIVE_COLLECTION).document(TEST_USER_ID_DOCUMENT)
                .collection(TODO_CATEGORY_COLLECTION).document(todoCategory.todoCategoryName)
                .set(todoCategory)
        }
    }

    @Test
    fun insertTodoCategory() = runTest {
        val newTodoCategory = TodoCategoryNetwork("Banana")
        todoCategoryFirestore.upsertTodoCategory(TEST_USER_ID_DOCUMENT, newTodoCategory)
        val networkData = todoCategoryFirestore.getAttachments(TEST_USER_ID_DOCUMENT)
        todoCategoryNetworkList.add(newTodoCategory)
        assertTrue(networkData.containsAll(todoCategoryNetworkList))
        todoCategoryFirestore.deleteAttachment(
            TEST_USER_ID_DOCUMENT,
            newTodoCategory.todoCategoryName
        )
    }

    @Test
    fun updateTodoCategory() = runTest {
        val noteToUpdate =
            todoCategoryNetworkList[Random.nextInt(todoCategoryNetworkList.size - 1)].copy(
                todoCategoryName = "Coconut"
            )
        todoCategoryFirestore.upsertTodoCategory(TEST_USER_ID_DOCUMENT, noteToUpdate)

        val networkData = todoCategoryFirestore.getAttachments(TEST_USER_ID_DOCUMENT)
        assertTrue(networkData.contains(noteToUpdate))
    }

    @Test
    fun getTodoCategory() = runTest {
        val attachmentToGet =
            todoCategoryNetworkList[Random.nextInt(todoCategoryNetworkList.size - 1)]
        val networkData =
            todoCategoryFirestore.getTodoCategory(
                TEST_USER_ID_DOCUMENT,
                attachmentToGet.todoCategoryName
            )
        assertEquals(attachmentToGet, networkData)
    }

    @Test
    fun getTodoCategories() = runTest {
        val networkData = todoCategoryFirestore.getAttachments(TEST_USER_ID_DOCUMENT)
        assertTrue(networkData.containsAll(todoCategoryNetworkList))
    }

    @Test
    fun deleteTodoCategory() = runTest {
        val todoCategoryToDelete =
            todoCategoryNetworkList[Random.nextInt(todoCategoryNetworkList.size - 1)]
        todoCategoryFirestore.deleteAttachment(
            TEST_USER_ID_DOCUMENT,
            todoCategoryToDelete.todoCategoryName
        )
        val networkData = todoCategoryFirestore.getAttachments(TEST_USER_ID_DOCUMENT)
        assertTrue(!networkData.contains(todoCategoryToDelete))
    }
}