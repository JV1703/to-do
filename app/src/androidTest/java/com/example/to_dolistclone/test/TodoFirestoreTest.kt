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
        todoFirestore.insertTodo(TEST_USER_ID_DOCUMENT, newTodo)
        val networkData = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        todoNetworkList.toMutableList().add(newTodo)
        assertTrue(networkData.containsAll(todoNetworkList))
        todoFirestore.deleteTodo(TEST_USER_ID_DOCUMENT, newTodo.todoId)
    }

    @Test
    fun deleteTodo() = runTest {
        val todoId = UUID.randomUUID().toString()
        val newTodo = generateSingleTodoNetwork(todoId)
        todoFirestore.insertTodo(TEST_USER_ID_DOCUMENT, newTodo)
        val networkData = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        assertTrue(networkData.contains(newTodo))
        todoFirestore.deleteTodo(TEST_USER_ID_DOCUMENT, newTodo.todoId)
        val updatedNetworkData = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        assertTrue(!updatedNetworkData.contains(newTodo))
    }

    @Test
    fun updateTodoTitle() = runTest {
        val todoToUpdate = todoNetworkList[nextInt(todoNetworkList.size - 1)]
        val newTitle = "Banana"
        todoFirestore.updateTodoTitle(TEST_USER_ID_DOCUMENT, todoToUpdate.todoId, newTitle)

        val networkData = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        assertNotNull(networkData.find { it.title == newTitle })
        assertTrue(networkData.contains(todoToUpdate.copy(title = newTitle)))
    }

    @Test
    fun updateTodoCategory() = runTest {
        val todoToUpdate = todoNetworkList[nextInt(todoNetworkList.size - 1)]
        val newTodoCategory = "New todo category"
        todoFirestore.updateTodoCategory(
            TEST_USER_ID_DOCUMENT, todoToUpdate.todoId, newTodoCategory
        )

        val networkData = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        assertNotNull(networkData.find { it.todoCategoryRefName == newTodoCategory })
        assertTrue(networkData.contains(todoToUpdate.copy(todoCategoryRefName = newTodoCategory)))
    }

    @Test
    fun updateTodoDeadline() = runTest {
        val todoToUpdate = todoNetworkList[nextInt(todoNetworkList.size - 1)]
        val newTodoDeadline = dateUtil.toLong(dateUtil.getCurrentDateTime().plusDays(1))
        todoFirestore.updateTodoDeadline(
            TEST_USER_ID_DOCUMENT, todoToUpdate.todoId, newTodoDeadline
        )

        val networkData = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        assertNotNull(networkData.find { it.deadline == newTodoDeadline })
        assertTrue(networkData.contains(todoToUpdate.copy(deadline = newTodoDeadline)))
    }

    @Test
    fun updateTodoReminder() = runTest {
        val todoToUpdate = todoNetworkList[nextInt(todoNetworkList.size - 1)]
        val newTodoReminder = dateUtil.toLong(dateUtil.getCurrentDateTime().plusDays(1))
        todoFirestore.updateTodoReminder(
            TEST_USER_ID_DOCUMENT, todoToUpdate.todoId, newTodoReminder
        )

        val networkData = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        assertNotNull(networkData.find { it.reminder == newTodoReminder })
        assertTrue(networkData.contains(todoToUpdate.copy(reminder = newTodoReminder)))
    }

    @Test
    fun updateTodoCompletion() = runTest {
        val todoToUpdate = todoNetworkList[nextInt(todoNetworkList.size - 1)]
        val newTodoCompletion = !todoToUpdate.isComplete
        val newCompletedOn = if (newTodoCompletion) dateUtil.toLong(
            dateUtil.getCurrentDateTime().plusDays(10)
        ) else null
        todoFirestore.updateTodoCompletion(
            TEST_USER_ID_DOCUMENT, todoToUpdate.todoId, newTodoCompletion, newCompletedOn
        )

        val networkData = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        assertNotNull(networkData.find { it.todoId == todoToUpdate.todoId && it.isComplete == newTodoCompletion && it.completedOn == newCompletedOn })
        assertTrue(
            networkData.contains(
                todoToUpdate.copy(
                    isComplete = newTodoCompletion, completedOn = newCompletedOn
                )
            )
        )
    }

    @Test
    fun updateTodoTasksAvailability() = runTest {
        val todoToUpdate = todoNetworkList[nextInt(todoNetworkList.size - 1)]
        val newTodoTasksAvailability = !todoToUpdate.tasks
        todoFirestore.updateTodoTasksAvailability(
            TEST_USER_ID_DOCUMENT, todoToUpdate.todoId, newTodoTasksAvailability
        )

        val networkData = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        assertNotNull(networkData.find { it.tasks == newTodoTasksAvailability })
        assertTrue(networkData.contains(todoToUpdate.copy(tasks = newTodoTasksAvailability)))
    }

    @Test
    fun updateTodoNotesAvailability() = runTest {
        val todoToUpdate = todoNetworkList[nextInt(todoNetworkList.size - 1)]
        val newTodoNotesAvailability = !todoToUpdate.notes
        todoFirestore.updateTodoNotesAvailability(
            TEST_USER_ID_DOCUMENT, todoToUpdate.todoId, newTodoNotesAvailability
        )

        val networkData = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        assertNotNull(networkData.find { it.notes == newTodoNotesAvailability })
        assertTrue(networkData.contains(todoToUpdate.copy(notes = newTodoNotesAvailability)))
    }

    @Test
    fun updateTodoAttachmentsAvailability() = runTest {
        val todoToUpdate = todoNetworkList[nextInt(todoNetworkList.size - 1)]
        val newTodoAttachmentAvailability = !todoToUpdate.notes
        todoFirestore.updateTodoAttachmentsAvailability(
            TEST_USER_ID_DOCUMENT, todoToUpdate.todoId, newTodoAttachmentAvailability
        )

        val networkData = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        assertNotNull(networkData.find { it.attachments == newTodoAttachmentAvailability })
        assertTrue(networkData.contains(todoToUpdate.copy(attachments = newTodoAttachmentAvailability)))
    }

    @Test
    fun updateTodoAlarmRef() = runTest {
        val todoToUpdate = todoNetworkList[nextInt(todoNetworkList.size - 1)]
        val newTodoAlarmRef = nextInt()
        todoFirestore.updateTodoAlarmRef(
            TEST_USER_ID_DOCUMENT, todoToUpdate.todoId, newTodoAlarmRef
        )

        val networkData = todoFirestore.getTodos(TEST_USER_ID_DOCUMENT)
        assertNotNull(networkData.find { it.alarmRef == newTodoAlarmRef })
        assertTrue(networkData.contains(todoToUpdate.copy(alarmRef = newTodoAlarmRef)))
    }
}