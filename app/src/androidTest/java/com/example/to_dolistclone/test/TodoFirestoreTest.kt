package com.example.to_dolistclone.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.example.note.utils.MainCoroutineRule
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.data.remote.firebase.abstraction.TodoFirestore
import com.example.to_dolistclone.core.data.remote.firebase.implementation.ACTIVE_COLLECTION
import com.example.to_dolistclone.core.data.remote.firebase.implementation.TEST_USER_ID_DOCUMENT
import com.example.to_dolistclone.core.data.remote.firebase.implementation.TodoFirestoreImpl
import com.example.to_dolistclone.core.data.remote.model.TodoNetwork
import com.example.to_dolistclone.utils.TestDataGenerator
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
class TodoFirestoreTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mainDispatcherRule = MainCoroutineRule()

    @get:Rule(order = 2)
    val instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    @Inject
    lateinit var dateUtil: DateUtil

    @Inject
    lateinit var testDataGenerator: TestDataGenerator

    private lateinit var todoFirestore: TodoFirestore
    private lateinit var todoNetworkList: MutableList<TodoNetwork>

    @Before
    fun setup() {
        hiltRule.inject()
        todoFirestore = TodoFirestoreImpl(firebaseFirestore)
        insertTodos()
    }

    private fun insertTodos() {
        todoNetworkList = testDataGenerator.produceListOfTodoNetwork().toMutableList()
        todoNetworkList.forEach { todo ->
            firebaseFirestore.collection(ACTIVE_COLLECTION).document(TEST_USER_ID_DOCUMENT)
                .collection("Todos").document(todo.todoId).set(todo)
        }
    }

    private fun generateSingleTodoNetwork(todoId: String) = TodoNetwork(
        todoId = todoId,
        title = "Todo title",
        deadline = if (Random.nextBoolean()) dateUtil.getCurrentDateTimeLong() else null,
        reminder = if (Random.nextBoolean()) dateUtil.getCurrentDateTimeLong() else null,
        repeat = null,
        isComplete = Random.nextBoolean(),
        createdOn = dateUtil.getCurrentDateTimeLong(),
        completedOn = if (Random.nextBoolean()) dateUtil.getCurrentDateTimeLong() else null,
        tasks = Random.nextBoolean(),
        notes = Random.nextBoolean(),
        attachments = Random.nextBoolean(),
        alarmRef = if (Random.nextBoolean()) nextInt() else null,
        todoCategoryRefName = if (Random.nextBoolean()) "Personal" else "Work"
    )

    @Test
    fun upsertTodo_andGetTodos() = runTest {
        val todoId = UUID.randomUUID().toString()
        val newTodo = generateSingleTodoNetwork(todoId)
        todoFirestore.upsertTodo(TEST_USER_ID_DOCUMENT, newTodo)
        val networkData = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        todoNetworkList.toMutableList().add(newTodo)
        assertTrue(networkData.containsAll(todoNetworkList))
        todoFirestore.deleteTodo(TEST_USER_ID_DOCUMENT, newTodo.todoId)
    }

    @Test
    fun deleteTodo() = runTest {
        val todoId = UUID.randomUUID().toString()
        val newTodo = generateSingleTodoNetwork(todoId)
        todoFirestore.upsertTodo(TEST_USER_ID_DOCUMENT, newTodo)
        val networkData = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        assertTrue(networkData.contains(newTodo))
        todoFirestore.deleteTodo(TEST_USER_ID_DOCUMENT, newTodo.todoId)
        val updatedNetworkData = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        assertTrue(!updatedNetworkData.contains(newTodo))
    }

    @Test
    fun updateTodo() = runTest {
        val todoToUpdate = todoNetworkList[nextInt(todoNetworkList.size)]
        val newTitle = "Updated title"
        todoFirestore.updateTodo(
            TEST_USER_ID_DOCUMENT, todoToUpdate.todoId, mapOf("title" to newTitle)
        )
        val networkTodos = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        assertTrue(networkTodos.contains(todoToUpdate.copy(title  = newTitle)))
    }

}