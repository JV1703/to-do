package com.example.to_dolistclone.test

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.example.note.utils.MainCoroutineRule
import com.example.to_dolistclone.core.data.remote.firebase.abstraction.TaskFirestore
import com.example.to_dolistclone.core.data.remote.firebase.implementation.ACTIVE_COLLECTION
import com.example.to_dolistclone.core.data.remote.firebase.implementation.TASK_COLLECTION
import com.example.to_dolistclone.core.data.remote.firebase.implementation.TEST_USER_ID_DOCUMENT
import com.example.to_dolistclone.core.data.remote.firebase.implementation.TaskFirestoreImpl
import com.example.to_dolistclone.core.data.remote.model.TaskNetwork
import com.example.to_dolistclone.utils.TestDataGenerator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt

@OptIn(ExperimentalCoroutinesApi::class)
@SmallTest
@HiltAndroidTest
class TaskFirestoreTest {

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

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var taskFirestore: TaskFirestore
    private lateinit var taskNetworkList: MutableList<TaskNetwork>
    private lateinit var TEST_TASK_TODO_REF: String

    @Before
    fun setup() {
        hiltRule.inject()
        taskFirestore = TaskFirestoreImpl(firebaseFirestore)
        insertTasks()
        TEST_TASK_TODO_REF = taskNetworkList[nextInt(taskNetworkList.size - 1)].todoRefId!!
        firebaseAuth.signOut()
        firebaseAuth.signInWithEmailAndPassword("abc@test.com", "123456")
    }

    private fun insertTasks() {
        taskNetworkList = testDataGenerator.produceListOfTaskNetwork().toMutableList()
        taskNetworkList.forEach { task ->
            firebaseFirestore.collection(ACTIVE_COLLECTION).document(TEST_USER_ID_DOCUMENT)
                .collection(TASK_COLLECTION).document(task.taskId).set(task)
        }
    }

    private fun generateSingleTaskNetwork(todoRefId: String, position: Int) = TaskNetwork(
        taskId = UUID.randomUUID().toString(),
        task = if (Random.nextBoolean()) "task name $position" else "Banana",
        isComplete = Random.nextBoolean(),
        position = position,
        todoRefId = todoRefId
    )

    @Test
    fun upsertTask() = runTest {

        val todoId = UUID.randomUUID().toString()
        val newTask = generateSingleTaskNetwork(todoId, nextInt())
        taskFirestore.upsertTask(TEST_USER_ID_DOCUMENT, newTask)
        val networkData = taskFirestore.getTasks(TEST_USER_ID_DOCUMENT)
        taskNetworkList.add(newTask)
        assertTrue(networkData.containsAll(taskNetworkList))
        taskFirestore.deleteTask(TEST_USER_ID_DOCUMENT, newTask.taskId)
    }

    @Test
    fun getTasks() = runTest {
        val networkData = taskFirestore.getTasks(TEST_USER_ID_DOCUMENT)
        assertTrue(networkData.containsAll(taskNetworkList))
    }

    @Test
    fun getTasks_withReferenceToTodoId() = runTest {
        val networkData = taskFirestore.getTasks(TEST_USER_ID_DOCUMENT, TEST_TASK_TODO_REF)
        assertTrue(networkData.containsAll(taskNetworkList.filter { it.todoRefId == TEST_TASK_TODO_REF }))
    }

    @Test
    fun getTask() = runTest{
        val randomTask = taskNetworkList[nextInt(taskNetworkList.size-1)]
        val networkData = taskFirestore.getTask(TEST_USER_ID_DOCUMENT, randomTask.taskId)
        assertEquals(randomTask, networkData)
    }

    @Test
    fun deleteTask() = runTest {
        val todoId = UUID.randomUUID().toString()
        val newTask = generateSingleTaskNetwork(todoId, nextInt())
        taskFirestore.upsertTask(TEST_USER_ID_DOCUMENT, newTask)
        val networkData = taskFirestore.getTasks(TEST_USER_ID_DOCUMENT)
        assertTrue(networkData.contains(newTask))
        taskFirestore.deleteTask(TEST_USER_ID_DOCUMENT, newTask.taskId)
        val updatedNetworkData = taskFirestore.getTasks(TEST_USER_ID_DOCUMENT)
        assertTrue(!updatedNetworkData.contains(newTask))
    }

    @Test
    fun deleteTasks() = runTest {
        taskFirestore.deleteTasks(TEST_USER_ID_DOCUMENT, TEST_TASK_TODO_REF)
        val dbData = taskFirestore.getTasks(TEST_USER_ID_DOCUMENT)
        assertTrue(dbData.none { it.todoRefId == TEST_TASK_TODO_REF })
    }

    @Test
    fun updateTask() = runTest{
        val taskToUpdate = taskNetworkList[nextInt(taskNetworkList.size - 1)]
        val newTitle = "New task title"

        taskFirestore.updateTask(TEST_USER_ID_DOCUMENT, taskToUpdate.taskId, mapOf("task" to newTitle))
        val networkData = taskFirestore.getTask(TEST_USER_ID_DOCUMENT, taskToUpdate.taskId)

        assertEquals(taskToUpdate.copy(task = newTitle), networkData)
        assertTrue(networkData?.task == newTitle)
    }

}