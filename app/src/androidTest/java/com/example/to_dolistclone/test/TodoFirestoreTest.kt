package com.example.to_dolistclone.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.example.note.utils.MainCoroutineRule
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.data.local.model.TodoEntity
import com.example.to_dolistclone.core.data.remote.ApiResult
import com.example.to_dolistclone.core.data.remote.firebase.TodoFirestoreImpl
import com.example.to_dolistclone.core.data.remote.model.TodoNetwork
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

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

    private lateinit var todoFirestore: TodoFirestoreImpl
    private lateinit var todoNetworkList: List<TodoNetwork>

    @Before
    fun setup() {
        hiltRule.inject()
        todoFirestore = TodoFirestoreImpl(firebaseFirestore)
        todoNetworkList = generateTodoListAtDate(dateUtil.getCurrentDateTime(), true)
    }

    private fun generateTodoListAtDate(
        date: LocalDateTime, isCompleted: Boolean, n: Int = 4
    ): List<TodoNetwork> {
        val todoNetworks = arrayListOf<TodoNetwork>()
        for (i in 0 until n) {
            val todoNetwork = TodoNetwork(
                todoId = UUID.randomUUID().toString(),
                title = "todo $i",
                deadline = if (i % 2 == 0) dateUtil.toLong(date.plusHours(3)) else null,
                reminder = if (i % 2 == 0) dateUtil.toLong(date.plusHours(1)) else null,
                repeat = null,
                isComplete = isCompleted,
                createdOn = dateUtil.toLong(date),
                completedOn = if (isCompleted) dateUtil.toLong(date.plusHours(2)) else null,
                tasks = i % 2 == 0,
                notes = i % 2 == 0,
                attachments = i % 2 == 0,
                alarmRef = if (i % 2 == 0) Random.nextInt() else null,
                todoCategoryRefName = if (i % 2 == 0) "Personal" else "Work"
            )
            todoNetworks.add(todoNetwork)
        }
        return todoNetworks
    }

    @Test
    fun upsertTodo_insertSuccess() = runTest {

        todoNetworkList.forEach {
            todoFirestore.upsertTodo(it)
        }

        val networkData = todoFirestore.getTodos()

        assertFalse(networkData is ApiResult.Success)

    }

}