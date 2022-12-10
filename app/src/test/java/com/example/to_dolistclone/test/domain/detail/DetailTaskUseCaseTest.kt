package com.example.to_dolistclone.test.domain.detail

import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.data.local.model.TaskEntity
import com.example.to_dolistclone.core.data.local.model.toTask
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.fake.FakeTodoRepository
import com.example.to_dolistclone.feature.detail.domain.implementation.DetailTaskUseCaseImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.random.Random.Default.nextInt

@OptIn(ExperimentalCoroutinesApi::class)
class DetailTaskUseCaseTest {

    private lateinit var fakeTodoRepository: TodoRepository
    private lateinit var useCase: DetailTaskUseCaseImpl
    private lateinit var dateUtil: DateUtil
    private lateinit var fakeTaskEntityList: List<TaskEntity>

    @Before
    fun setup() {
        fakeTodoRepository = FakeTodoRepository()
        useCase = DetailTaskUseCaseImpl(fakeTodoRepository)
        dateUtil = DateUtil()
        fakeTaskEntityList = generateTaskList()
    }

    private fun generateTaskList(n: Int = 10): List<TaskEntity> {
        val taskEntityList = arrayListOf<TaskEntity>()
        for (i in 0 until n) {
            val taskEntity = TaskEntity(
                taskId = UUID.randomUUID().toString(),
                task = if (i % 2 == 0) "task name $i" else "",
                isComplete = i % 2 == 0,
                position = i,
                todoRefId = UUID.randomUUID().toString()
            )
            taskEntityList.add(taskEntity)
        }
        return taskEntityList.shuffled()
    }

    @Test
    fun insertTask() = runTest {
        fakeTaskEntityList.forEach { useCase.insertTask(it.toTask()) }
        val actual = fakeTodoRepository.getTasks().first()
        assertEquals(fakeTaskEntityList.map { it.toTask() }, actual)
    }

    @Test
    fun updateTaskPosition() = runTest {
        fakeTaskEntityList.forEach { useCase.insertTask(it.toTask()) }

        val savedTask = fakeTodoRepository.getTasks().first()
        val taskToUpdate = savedTask[nextInt(savedTask.size - 1)]
        val previousTaskPosition = taskToUpdate.position

        val newPosition = 10
        useCase.updateTaskPosition(taskToUpdate.taskId, newPosition)
        val updatedTaskList = fakeTodoRepository.getTasks().first()

        assertNotEquals(
            previousTaskPosition,
            updatedTaskList.find { it.taskId == taskToUpdate.taskId }?.position
        )
        assertEquals(
            newPosition,
            updatedTaskList.find { it.taskId == taskToUpdate.taskId }?.position
        )
    }

    @Test
    fun updateTaskTitle() = runTest {
        fakeTaskEntityList.forEach { useCase.insertTask(it.toTask()) }

        val savedTask = fakeTodoRepository.getTasks().first()
        val taskToUpdate = savedTask[nextInt(savedTask.size - 1)]
        val previousTaskTitle = taskToUpdate.task

        val newTitle = "Random"
        useCase.updateTaskTitle(taskToUpdate.taskId, "Random")
        val updatedTaskList = fakeTodoRepository.getTasks().first()

        assertNotEquals(
            previousTaskTitle,
            updatedTaskList.find { it.taskId == taskToUpdate.taskId }?.position
        )
        assertEquals(newTitle, updatedTaskList.find { it.taskId == taskToUpdate.taskId }?.task)
    }

    @Test
    fun updateTaskCompletion() = runTest {
        fakeTaskEntityList.forEach { useCase.insertTask(it.toTask()) }

        val savedTask = fakeTodoRepository.getTasks().first()
        val taskToUpdate = savedTask[nextInt(savedTask.size - 1)]
        val previousTaskCompletion = taskToUpdate.isComplete

        val newCompletion = !previousTaskCompletion
        useCase.updateTaskCompletion(taskToUpdate.taskId, newCompletion)
        val updatedTaskList = fakeTodoRepository.getTasks().first()

        assertNotEquals(
            previousTaskCompletion,
            updatedTaskList.find { it.taskId == taskToUpdate.taskId }?.isComplete
        )
        assertEquals(
            newCompletion,
            updatedTaskList.find { it.taskId == taskToUpdate.taskId }?.isComplete
        )
    }

    @Test
    fun deleteTask() = runTest {
        fakeTaskEntityList.forEach { useCase.insertTask(it.toTask()) }

        val savedTask = fakeTodoRepository.getTasks().first()
        val taskToDelete = savedTask[nextInt(savedTask.size - 1)]

        useCase.deleteTask(taskToDelete.taskId)

        val updatedTasks = fakeTodoRepository.getTasks().first()

        assertTrue(!updatedTasks.contains(taskToDelete))
    }

}