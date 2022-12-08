package com.example.to_dolistclone.test.domain.todo

import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.data.local.model.TodoEntity
import com.example.to_dolistclone.core.data.local.model.toTodo
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.fake.FakeTodoRepository
import com.example.to_dolistclone.feature.todo.domain.implementation.TodoUseCaseImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class TodoUseCaseImplTest {

//    @get:Rule
//    val instantExecutorRule = InstantTaskExecutorRule()
//
//    @get:Rule
//    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var fakeTodoRepository: TodoRepository
    private lateinit var todoUseCase: TodoUseCaseImpl
    private lateinit var dateUtil: DateUtil

    private lateinit var currentDateTime: LocalDateTime
    private lateinit var fakeTodoEntityList: List<TodoEntity>

    @Before
    fun setup() {
        fakeTodoRepository = FakeTodoRepository()
        dateUtil = DateUtil()
        todoUseCase = TodoUseCaseImpl(fakeTodoRepository, dateUtil)

        currentDateTime = dateUtil.getCurrentDateTime()
        val todoEntityListNotCompletedBeforeCurrentDateTime =
            generateTodoListAtDate(currentDateTime.minusDays(1), false)
        val todoEntityListNotCompletedAfterCurrentDateTime =
            generateTodoListAtDate(currentDateTime.plusDays(1), false)
        val todoEntityListNotCompletedAtCurrentDateTime =
            generateTodoListAtDate(currentDateTime, false)
        val todoEntityListCompletedBeforeCurrentDateTime =
            generateTodoListAtDate(currentDateTime.minusDays(1), true)
        val todoEntityListCompletedAfterCurrentDateTime =
            generateTodoListAtDate(currentDateTime.plusDays(1), true)
        val todoEntityListCompletedAtCurrentDateTime = generateTodoListAtDate(currentDateTime, true)
        fakeTodoEntityList = arrayListOf(
            todoEntityListNotCompletedBeforeCurrentDateTime,
            todoEntityListNotCompletedAfterCurrentDateTime,
            todoEntityListNotCompletedAtCurrentDateTime,
            todoEntityListCompletedBeforeCurrentDateTime,
            todoEntityListCompletedAfterCurrentDateTime,
            todoEntityListCompletedAtCurrentDateTime
        ).flatten()
    }

    private fun generateTodoListAtDate(
        date: LocalDateTime, isCompleted: Boolean, n: Int = 4
    ): List<TodoEntity> {
        val todoEntities = arrayListOf<TodoEntity>()
        for (i in 0 until n) {
            val todoEntity = TodoEntity(
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
            todoEntities.add(todoEntity)
        }
        return todoEntities
    }

    @Test
    fun insertTodo() = runTest {
        fakeTodoEntityList.forEach { todo ->
            todoUseCase.insertTodo(todo.toTodo())
        }

        // test to confirm the size is the same
        val actualSize = todoUseCase.todos.first().size
        assertEquals(fakeTodoEntityList.size, actualSize)

        // test to confirm all item is inserted
        val actualList = todoUseCase.todos.first()
        assertEquals(fakeTodoEntityList.map { it.toTodo() }, actualList)
    }

    @Test
    fun updateTodoCompletion() = runTest {
        fakeTodoEntityList.forEach { todo ->
            todoUseCase.insertTodo(todo.toTodo())
        }
        val listSize = todoUseCase.todos.first().size
        val completedOn = dateUtil.toLocalDate("2022/12/31", "yyyy/MM/dd")
        val todoUpdateId = todoUseCase.todos.first()[Random.nextInt(listSize)].todoId

        todoUseCase.updateTodoCompletion(
            todoUpdateId, isComplete = false, completedOn = dateUtil.toLong(completedOn)
        )

        val actualUpdatedList = todoUseCase.todos.first()

        assertEquals(actualUpdatedList.find { it.todoId == todoUpdateId }?.isComplete, false)
        assertEquals(
            actualUpdatedList.find { it.todoId == todoUpdateId }?.completedOn,
            dateUtil.toLong(completedOn)
        )
    }

    @Test
    fun getMappedTodos() = runTest {
        fakeTodoEntityList.forEach { todo ->
            todoUseCase.insertTodo(todo.toTodo())
        }

        val actualResult = todoUseCase.getMappedTodos(dateUtil.toLocalDate(currentDateTime)).first()

        val expectedPrevious = fakeTodoEntityList.filter {
            !it.isComplete && it.deadline != null && dateUtil.toLocalDate(it.deadline!!)
                .isBefore(currentDateTime.toLocalDate())
        }.map { it.toTodo() }
        val expectedToday = fakeTodoEntityList.filter {
            !it.isComplete && it.deadline != null && dateUtil.toLocalDate(it.deadline!!)
                .isEqual(currentDateTime.toLocalDate())
        }.map { it.toTodo() }
        val expectedFuture = fakeTodoEntityList.filter {
            !it.isComplete && dateUtil.toLocalDate(
                it.deadline ?: dateUtil.toLong(currentDateTime.plusDays(1))
            ).isAfter(currentDateTime.toLocalDate())
        }.map { it.toTodo() }
        val expectedCompletedToday = fakeTodoEntityList.filter {
            it.isComplete && it.completedOn != null && dateUtil.toLocalDate(it.completedOn!!)
                .isEqual(currentDateTime.toLocalDate())
        }.map { it.toTodo() }

        // test if map key previous have the same value
        assertEquals(expectedPrevious, actualResult["previous"])
        // test if map key today have the same value
        assertEquals(expectedToday, actualResult["today"])
        // test if map key future have the same value
        assertEquals(expectedFuture, actualResult["future"])
        // test if completedToday have the same value
        assertEquals(expectedCompletedToday, actualResult["completedToday"])
    }

    @Test
    fun getTodosGroupByDeadline() = runTest {
        fakeTodoEntityList.forEach {
            todoUseCase.insertTodo(it.toTodo())
        }

        val expected = fakeTodoEntityList
            .map { todo -> todo.toTodo() }
            .groupBy { todo ->
                todo.deadline?.let { deadline ->
                    dateUtil.toLocalDate(deadline)
                }
            }

        val actual = todoUseCase.getTodosGroupByDeadline().first()

        assertEquals(expected, actual)
    }

    @Test
    fun saveShowPrevious() = runTest {
        todoUseCase.saveShowPrevious(true)

        val actual = todoUseCase.getShowPrevious().first()
        assertEquals(true, actual)
    }

    @Test
    fun saveShowToday() = runTest {
        todoUseCase.saveShowToday(true)

        val actual = todoUseCase.getShowToday().first()
        assertEquals(true, actual)
    }

    @Test
    fun saveShowFuture() = runTest {
        todoUseCase.saveShowFuture(true)

        val actual = todoUseCase.getShowFuture().first()
        assertEquals(true, actual)
    }

    @Test
    fun saveShowCompletedToday() = runTest {
        todoUseCase.saveShowCompletedToday(true)

        val actual = todoUseCase.getShowCompletedToday().first()
        assertEquals(true, actual)
    }

}