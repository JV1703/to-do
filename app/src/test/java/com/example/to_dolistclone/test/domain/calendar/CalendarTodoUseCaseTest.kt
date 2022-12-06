package com.example.to_dolistclone.test.domain.calendar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.data.local.model.TodoEntity
import com.example.to_dolistclone.core.data.local.model.toTodo
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.fake.FakeTodoRepository
import com.example.to_dolistclone.feature.calendar.domain.abstraction.CalendarTodoUseCase
import com.example.to_dolistclone.feature.calendar.domain.implementation.CalendarTodoUseCaseImpl
import com.example.to_dolistclone.utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarTodoUseCaseTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var fakeTodoRepository: TodoRepository
    private lateinit var calendarTodoUseCase: CalendarTodoUseCase
    private lateinit var dateUtil: DateUtil

    @Before
    fun setup() {
        dateUtil = DateUtil()
        fakeTodoRepository = FakeTodoRepository()
        calendarTodoUseCase = CalendarTodoUseCaseImpl(fakeTodoRepository, dateUtil)
    }

    @Test
    fun getTodosGroupByDeadline() = runTest {
        // generate fake data
        val todoEntities = arrayListOf<TodoEntity>()
        for (i in 0 until 4) {
            val deadline = if (i % 2 == 0) dateUtil.getCurrentDateTime()
                .plusDays(i.toLong()) else dateUtil.getCurrentDateTime().plusHours(i.toLong())
            val todoEntity = TodoEntity(
                todoId = UUID.randomUUID().toString(),
                title = "todo $i",
                deadline = dateUtil.toLong(deadline),
                reminder = null,
                repeat = null,
                isComplete = i % 2 == 0,
                createdOn = dateUtil.getCurrentDateTimeLong(),
                completedOn = if (i % 2 == 0) dateUtil.toLong(deadline.plusHours(12)) else null,
                tasks = i % 2 == 0,
                notes = i % 2 == 0,
                attachments = i % 2 == 0,
                alarmRef = if (i % 2 == 0) Random.nextInt() else null,
                todoCategoryRefName = if (i % 2 == 0) "Personal" else "Work"
            )
            todoEntities.add(todoEntity)
            fakeTodoRepository.insertTodo(todoEntity.toTodo())
        }

        // test to confirm 4 todos is inserted to the database
        assertEquals(todoEntities.size, calendarTodoUseCase.todos.first().size)

        val actual = calendarTodoUseCase.getTodosGroupByDeadline().first()
        val expectedGrouping = todoEntities.groupBy { todo ->
            todo.deadline?.let { deadline ->
                dateUtil.toLocalDate(deadline)
            }
        }

        // test to confirm the size of group is the same
        assertEquals(expectedGrouping.size, actual.keys.size)

        // test if all the actual key have the same value size as its expected value
        actual.forEach { map ->
            assertEquals(expectedGrouping[map.key]?.size, map.value.size)
        }

    }
}
