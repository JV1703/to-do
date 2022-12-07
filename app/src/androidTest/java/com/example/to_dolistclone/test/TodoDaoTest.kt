package com.example.to_dolistclone.test

import android.database.sqlite.SQLiteConstraintException
import com.example.note.utils.MainCoroutineRule
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.data.local.TodoDatabase
import com.example.to_dolistclone.core.data.local.dao.TodoDao
import com.example.to_dolistclone.core.data.local.model.*
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoDetailsEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoWithAttachmentsEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_many.TodoWithTasksEntity
import com.example.to_dolistclone.core.data.local.model.relations.one_to_one.TodoAndNoteEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.Random.Default.nextBoolean
import kotlin.random.Random.Default.nextInt

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class TodoDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    @Inject
//    @Named("test_db")
    lateinit var database: TodoDatabase

    @Inject
    lateinit var dateUtil: DateUtil

    private lateinit var todoEntityList: List<TodoEntity>
    private lateinit var noteEntityList: List<NoteEntity>
    private lateinit var taskEntityList: List<TaskEntity>
    private lateinit var dao: TodoDao

    @Before
    fun setup() {
        hiltRule.inject()
        todoEntityList = generateTodoListAtDate(dateUtil.getCurrentDateTime(), true)
        noteEntityList = generateNoteEntityList(todoRefId = UUID.randomUUID().toString())
        taskEntityList = generateTaskList()
        dao = database.todoDao()
    }

    @After
    fun tearDown() {
        database.close()
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

    private fun generateSingleTodoEntity(todoId: String) = TodoEntity(
        todoId = todoId,
        title = "Todo title",
        deadline = if (nextBoolean()) dateUtil.getCurrentDateTimeLong() else null,
        reminder = if (nextBoolean()) dateUtil.getCurrentDateTimeLong() else null,
        repeat = null,
        isComplete = nextBoolean(),
        createdOn = dateUtil.getCurrentDateTimeLong(),
        completedOn = if (nextBoolean()) dateUtil.getCurrentDateTimeLong() else null,
        tasks = nextBoolean(),
        notes = nextBoolean(),
        attachments = nextBoolean(),
        alarmRef = if (nextBoolean()) nextInt() else null,
        todoCategoryRefName = if (nextBoolean()) "Personal" else "Work"
    )

    private fun generateNoteEntityList(n: Int = 4, todoRefId: String): List<NoteEntity> {
        val noteEntityList = arrayListOf<NoteEntity>()
        for (i in 0 until n) {
            val noteEntity = NoteEntity(
                noteId = if (i == 1) todoRefId else UUID.randomUUID().toString(),
                title = "title $i",
                body = "body $i",
                created_at = dateUtil.getCurrentDateTimeLong(),
                updated_at = dateUtil.getCurrentDateTimeLong()
            )
            noteEntityList.add(noteEntity)
        }
        return noteEntityList
    }

    private fun generateSingleNoteEntity(noteId: String): NoteEntity = NoteEntity(
        noteId = noteId,
        title = "Note title",
        body = "Note body",
        created_at = dateUtil.getCurrentDateTimeLong(),
        updated_at = dateUtil.getCurrentDateTimeLong()
    )

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

    private fun generateSingleTaskEntity(todoRefId: String, position: Int) = TaskEntity(
        taskId = UUID.randomUUID().toString(),
        task = if (nextBoolean()) "task name $position" else "Banana",
        isComplete = nextBoolean(),
        position = position,
        todoRefId = todoRefId
    )

    private fun generateAttachmentEntity(n: Int = 10): List<AttachmentEntity> {
        val attachmentEntityList = arrayListOf<AttachmentEntity>()
        for (i in 0 until n) {
            val attachmentEntity = AttachmentEntity(
                attachmentId = UUID.randomUUID().toString(),
                name = "attachment $i",
                uri = "uri $1",
                type = "jpg",
                size = 1024,
                todoRefId = UUID.randomUUID().toString()
            )
            attachmentEntityList.add(attachmentEntity)
        }
        return attachmentEntityList
    }

    private fun generateSingleAttachment(todoRefId: String) = AttachmentEntity(
        attachmentId = UUID.randomUUID().toString(),
        name = "attachment name",
        uri = "uri $1",
        type = "jpg",
        size = 1024,
        todoRefId = todoRefId
    )

    @Test
    fun insertTodo() = runTest {
        todoEntityList.forEach {
            dao.insertTodo(it)
        }

        val dbData = dao.getTodos().first()
        assertEquals(todoEntityList, dbData)
    }

    @Test
    fun updateTodoTitle() = runTest {
        todoEntityList.forEach {
            dao.insertTodo(it)
        }

        val dbData = dao.getTodos().first()
        assertEquals(todoEntityList, dbData)

        val dataToBeUpdate = dbData[nextInt(dbData.size - 1)]
        val newTitle = "Updated test title"
        dao.updateTodoTitle(todoId = dataToBeUpdate.todoId, title = newTitle)

        val updatedDbData = dao.getTodos().first()
        assertNotNull(updatedDbData.find { it.title == newTitle })
        assertTrue(updatedDbData.contains(dataToBeUpdate.copy(title = newTitle)))
    }

    @Test
    fun updateTodoCategory() = runTest {
        todoEntityList.forEach {
            dao.insertTodo(it)
        }

        val dbData = dao.getTodos().first()
        assertEquals(todoEntityList, dbData)

        val dataToBeUpdate = dbData[nextInt(dbData.size - 1)]
        val newCategory = "New Category"
        dao.updateTodoCategory(todoId = dataToBeUpdate.todoId, category = newCategory)

        val updatedDbData = dao.getTodos().first()
        assertNotNull(updatedDbData.find { it.todoCategoryRefName == newCategory })
        assertTrue(updatedDbData.contains(dataToBeUpdate.copy(todoCategoryRefName = newCategory)))
    }

    @Test
    fun getTodo() = runTest {
        todoEntityList.forEach {
            dao.insertTodo(it)
        }

        val dbData = dao.getTodos().first()
        assertEquals(todoEntityList, dbData)

        val todoToGet = dbData[nextInt(dbData.size - 1)]
        val obtainedTodo = dao.getTodo(todoToGet.todoId).first()
        assertEquals(todoToGet, obtainedTodo)
    }

    @Test
    fun getTodos() = runTest {
        todoEntityList.forEach {
            dao.insertTodo(it)
        }

        assertEquals(todoEntityList, dao.getTodos().first())
    }

    @Test
    fun deleteTodo() = runTest {
        todoEntityList.forEach {
            dao.insertTodo(it)
        }

        val todoToDelete = todoEntityList[nextInt(todoEntityList.size - 1)]
        val dbData = dao.getTodos().first()
        assertTrue(dbData.contains(todoToDelete))

        dao.deleteTodo(todoToDelete.todoId)
        val dbDataAfterDeleteTodo = dao.getTodos().first()

        assertTrue(!dbDataAfterDeleteTodo.contains(todoToDelete))
    }

    @Test
    fun insertNote_success() = runTest {
        val id = UUID.randomUUID().toString()
        val todoEntity = generateSingleTodoEntity(id)
        val noteEntity = generateSingleNoteEntity(id)
        dao.insertTodo(todoEntity)
        dao.insertNote(noteEntity)

        assertEquals(noteEntity, dao.getNotes().first().first())
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertNote_fail() = runTest {
        val noteId = UUID.randomUUID().toString()
        val noteEntity = generateSingleNoteEntity(noteId)

        dao.insertNote(noteEntity)
    }

    @Test
    fun getNotes() = runTest {
        val id = UUID.randomUUID().toString()
        val todoEntity = generateSingleTodoEntity(id)
        val noteEntity = generateSingleNoteEntity(id)
        dao.insertTodo(todoEntity)
        dao.insertNote(noteEntity)

        val dbData = dao.getNotes().first()
        assertEquals(noteEntity, dbData.first())
    }

    @Test
    fun updateTodoDeadLine_success() = runTest {
        todoEntityList.forEach {
            dao.insertTodo(it)
        }

        val dbData = dao.getTodos().first()
        assertEquals(todoEntityList, dbData)

        val dataToBeUpdate = dbData[nextInt(dbData.size - 1)]
        val newDeadline = dateUtil.toLong(dateUtil.getCurrentDateTime().plusDays(1))
        dao.updateTodoDeadline(todoId = dataToBeUpdate.todoId, deadline = newDeadline)

        val updatedDbData = dao.getTodos().first()
        assertNotNull(updatedDbData.find { it.deadline == newDeadline })
        assertTrue(updatedDbData.contains(dataToBeUpdate.copy(deadline = newDeadline)))
    }

    @Test
    fun updateTodoReminder() = runTest {
        todoEntityList.forEach {
            dao.insertTodo(it)
        }

        val dbData = dao.getTodos().first()
        assertEquals(todoEntityList, dbData)

        val dataToBeUpdate = dbData[nextInt(dbData.size - 1)]
        val newReminder = dateUtil.toLong(dateUtil.getCurrentDateTime().plusDays(1))
        dao.updateTodoReminder(todoId = dataToBeUpdate.todoId, reminder = newReminder)

        val updatedDbData = dao.getTodos().first()
        assertNotNull(updatedDbData.find { it.reminder == newReminder })
        assertTrue(updatedDbData.contains(dataToBeUpdate.copy(reminder = newReminder)))
    }

    @Test
    fun updateTodoCompletion() = runTest {
        todoEntityList.forEach {
            dao.insertTodo(it)
        }

        val dbData = dao.getTodos().first()
        assertEquals(todoEntityList, dbData)

        val dataToBeUpdate = dbData[nextInt(dbData.size - 1)]
        val newCompletion = !dataToBeUpdate.tasks
        val newCompletedOn = dateUtil.toLong(dateUtil.getCurrentDate().plusDays(1))
        dao.updateTodoCompletion(
            todoId = dataToBeUpdate.todoId, isComplete = newCompletion, completedOn = newCompletedOn
        )

        val updatedDbData = dao.getTodos().first()
        assertNotNull(updatedDbData.find { it.isComplete == newCompletion && it.completedOn == newCompletedOn })
        assertTrue(
            updatedDbData.contains(
                dataToBeUpdate.copy(
                    isComplete = newCompletion, completedOn = newCompletedOn
                )
            )
        )
    }

    @Test
    fun updateTodoTasksAvailability() = runTest {
        todoEntityList.forEach {
            dao.insertTodo(it)
        }

        val dbData = dao.getTodos().first()
        assertEquals(todoEntityList, dbData)

        val dataToBeUpdate = dbData[nextInt(dbData.size - 1)]
        val newTaskAvailability = !dataToBeUpdate.tasks
        dao.updateTodoTasksAvailability(
            todoId = dataToBeUpdate.todoId, tasksAvailability = newTaskAvailability
        )

        val updatedDbData = dao.getTodos().first()
        assertNotNull(updatedDbData.find { it.tasks == newTaskAvailability })
        assertTrue(updatedDbData.contains(dataToBeUpdate.copy(tasks = newTaskAvailability)))
    }

    @Test
    fun updateTodoNotesAvailability() = runTest {
        todoEntityList.forEach {
            dao.insertTodo(it)
        }

        val dbData = dao.getTodos().first()
        assertEquals(todoEntityList, dbData)

        val dataToBeUpdate = dbData[nextInt(dbData.size - 1)]
        val newNotesAvailability = !dataToBeUpdate.notes
        dao.updateTodoNotesAvailability(
            todoId = dataToBeUpdate.todoId, notesAvailability = newNotesAvailability
        )

        val updatedDbData = dao.getTodos().first()
        assertNotNull(updatedDbData.find { it.notes == newNotesAvailability })
        assertTrue(updatedDbData.contains(dataToBeUpdate.copy(notes = newNotesAvailability)))
    }

    @Test
    fun updateTodoAttachmentsAvailability() = runTest {
        todoEntityList.forEach {
            dao.insertTodo(it)
        }

        val dbData = dao.getTodos().first()
        assertEquals(todoEntityList, dbData)

        val dataToBeUpdate = dbData[nextInt(dbData.size - 1)]
        val newAttachmentAvailability = !dataToBeUpdate.attachments
        dao.updateTodoAttachmentsAvailability(
            todoId = dataToBeUpdate.todoId, attachmentsAvailability = newAttachmentAvailability
        )

        val updatedDbData = dao.getTodos().first()
        assertNotNull(updatedDbData.find { it.attachments == newAttachmentAvailability })
        assertTrue(updatedDbData.contains(dataToBeUpdate.copy(attachments = newAttachmentAvailability)))
    }

    @Test
    fun updateTodoAlarmRef() = runTest {
        todoEntityList.forEach {
            dao.insertTodo(it)
        }

        val dbData = dao.getTodos().first()
        assertEquals(todoEntityList, dbData)

        val dataToBeUpdate = dbData[nextInt(dbData.size - 1)]
        val newAlarmRef = nextInt()
        dao.updateTodoAlarmRef(
            todoId = dataToBeUpdate.todoId, alarmRef = newAlarmRef
        )

        val updatedDbData = dao.getTodos().first()
        assertNotNull(updatedDbData.find { it.alarmRef == newAlarmRef })
        assertTrue(updatedDbData.contains(dataToBeUpdate.copy(alarmRef = newAlarmRef)))
    }

    @Test
    fun deleteNote() = runTest {
        val id1 = UUID.randomUUID().toString()
        val id2 = UUID.randomUUID().toString()
        val todoEntity1 = generateSingleTodoEntity(id1)
        val todoEntity2 = generateSingleTodoEntity(id2)
        val noteEntity1 = generateSingleNoteEntity(id1)
        val noteEntity2 = generateSingleNoteEntity(id2)
        dao.insertTodo(todoEntity1)
        dao.insertTodo(todoEntity2)
        dao.insertNote(noteEntity1)
        dao.insertNote(noteEntity2)

        dao.deleteNote(noteEntity1.noteId)
        val dbDataAfterDelete = dao.getNotes().first()
        assertEquals(dbDataAfterDelete.size, 1)
    }

    @Test
    fun insertTask_success() = runTest {
        val todoId = UUID.randomUUID().toString()
        val todoEntity = generateSingleTodoEntity(todoId)
        dao.insertTodo(todoEntity)
        val taskSize = 10
        val taskList = arrayListOf<TaskEntity>()
        for (i in 0 until taskSize) {
            val taskEntity = generateSingleTaskEntity(todoId, i)
            taskList.add(taskEntity)
        }
        taskList.forEach {
            dao.insertTask(it)
        }

        val dbData = dao.getTasks().first()
        assertEquals(taskList, dbData)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertTask_error() = runTest {
        taskEntityList.forEach {
            dao.insertTask(it)
        }
    }

    @Test
    fun updateTaskPosition() = runTest {
        val todoId = UUID.randomUUID().toString()
        val todoEntity = generateSingleTodoEntity(todoId)
        dao.insertTodo(todoEntity)
        val taskSize = 10
        val taskList = arrayListOf<TaskEntity>()
        for (i in 0 until taskSize) {
            val taskEntity = generateSingleTaskEntity(todoId, i)
            taskList.add(taskEntity)
        }
        taskList.forEach {
            dao.insertTask(it)
        }

        val dbData = dao.getTasks().first()
        assertTrue(dbData.mapIndexed { index, taskEntity -> taskEntity.position == index }
            .all { true })

        val test = taskList.reversed()
            .mapIndexed { index, taskEntity -> taskEntity.copy(position = index) }

        test.forEach { dao.updateTaskPosition(it.taskId, it.position) }

        val updatedDbData = dao.getTasks().first()

        for (i in taskList.indices) {
            assertEquals(taskList[i].position, updatedDbData[taskList.size - 1 - i].position)
        }
    }

    @Test
    fun updateTaskTitle() = runTest {
        val todoId = UUID.randomUUID().toString()
        val todoEntity = generateSingleTodoEntity(todoId)
        dao.insertTodo(todoEntity)
        val taskSize = 10
        val taskList = arrayListOf<TaskEntity>()
        for (i in 0 until taskSize) {
            val taskEntity = generateSingleTaskEntity(todoId, i)
            taskList.add(taskEntity)
        }
        taskList.forEach {
            dao.insertTask(it)
        }

        val dbData = dao.getTasks().first()
        val taskToBeUpdate = dbData[nextInt(dbData.size - 1)]
        val newTitle = "New Task Title"

        dao.updateTaskTitle(taskToBeUpdate.taskId, newTitle)

        val updatedDbData = dao.getTasks().first()
        assertTrue(updatedDbData.contains(taskToBeUpdate.copy(task = newTitle)))
        assertFalse(dbData.contains(taskToBeUpdate.copy(task = newTitle)))
    }

    @Test
    fun updateTaskCompletion() = runTest {
        val todoId = UUID.randomUUID().toString()
        val todoEntity = generateSingleTodoEntity(todoId)
        dao.insertTodo(todoEntity)
        val taskSize = 10
        val taskList = arrayListOf<TaskEntity>()
        for (i in 0 until taskSize) {
            val taskEntity = generateSingleTaskEntity(todoId, i)
            taskList.add(taskEntity)
        }
        taskList.forEach {
            dao.insertTask(it)
        }

        val dbData = dao.getTasks().first()
        val taskToBeUpdate = dbData[nextInt(dbData.size - 1)]
        val newTaskCompletion = !taskToBeUpdate.isComplete

        dao.updateTaskCompletion(taskToBeUpdate.taskId, newTaskCompletion)

        val updatedDbData = dao.getTasks().first()
        assertTrue(updatedDbData.contains(taskToBeUpdate.copy(isComplete = newTaskCompletion)))
        assertFalse(dbData.contains(taskToBeUpdate.copy(isComplete = newTaskCompletion)))
    }

    @Test
    fun insertTasks_success() = runTest {
        val id1 = UUID.randomUUID().toString()
        val id2 = UUID.randomUUID().toString()
        val todoEntity1 = generateSingleTodoEntity(id1)
        val todoEntity2 = generateSingleTodoEntity(id2)
        val taskEntity1 = generateSingleTaskEntity(id1, 0)
        val taskEntity2 = generateSingleTaskEntity(id2, 1)

        dao.insertTodo(todoEntity1)
        dao.insertTodo(todoEntity2)

        val taskList = listOf(taskEntity1, taskEntity2)

        dao.insertTasks(taskList)
        val dbData = dao.getTasks().first()
        assertEquals(taskList, dbData)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertTasks_fail() = runTest {
        val id1 = UUID.randomUUID().toString()
        val id2 = UUID.randomUUID().toString()
        val taskEntity1 = generateSingleTaskEntity(id1, 0)
        val taskEntity2 = generateSingleTaskEntity(id2, 1)

        val taskList = listOf(taskEntity1, taskEntity2)
        dao.insertTasks(taskList)
    }

    @Test
    fun getTasks() = runTest {
        val id1 = UUID.randomUUID().toString()
        val id2 = UUID.randomUUID().toString()
        val todoEntity1 = generateSingleTodoEntity(id1)
        val todoEntity2 = generateSingleTodoEntity(id2)
        val taskEntity1 = generateSingleTaskEntity(id1, 0)
        val taskEntity2 = generateSingleTaskEntity(id2, 1)

        dao.insertTodo(todoEntity1)
        dao.insertTodo(todoEntity2)

        val taskList = listOf(taskEntity1, taskEntity2)

        dao.insertTasks(taskList)
        val dbData = dao.getTasks().first()
        assertEquals(taskList, dbData)
    }

    @Test
    fun deleteTask() = runTest {
        val id1 = UUID.randomUUID().toString()
        val id2 = UUID.randomUUID().toString()
        val todoEntity1 = generateSingleTodoEntity(id1)
        val todoEntity2 = generateSingleTodoEntity(id2)
        val taskEntity1 = generateSingleTaskEntity(id1, 0)
        val taskEntity2 = generateSingleTaskEntity(id2, 1)

        dao.insertTodo(todoEntity1)
        dao.insertTodo(todoEntity2)

        val taskList = listOf(taskEntity1, taskEntity2)

        dao.insertTasks(taskList)
        val dbData = dao.getTasks().first()
        assertEquals(taskList, dbData)

        val taskToDelete = dbData[nextInt(dbData.size - 1)]
        dao.deleteTask(taskToDelete.taskId)
        val updatedDbData = dao.getTasks().first()

        assertTrue(updatedDbData.contains(taskEntity2))
        assertFalse(updatedDbData.contains(taskEntity1))
    }

    @Test
    fun insertAttachment_success() = runTest {
        val id1 = UUID.randomUUID().toString()
        val id2 = UUID.randomUUID().toString()
        val todoEntity1 = generateSingleTodoEntity(id1)
        val todoEntity2 = generateSingleTodoEntity(id2)
        val attachmentEntity1 = generateSingleAttachment(id1)
        val attachmentEntity2 = generateSingleAttachment(id2)
        val attachmentList = listOf(attachmentEntity1, attachmentEntity2)

        dao.insertTodo(todoEntity1)
        dao.insertTodo(todoEntity2)

        attachmentList.forEach {
            dao.insertAttachment(it)
        }

        val dbData = dao.getAttachments().first()

        assertEquals(attachmentList, dbData)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertAttachment_fail() = runTest {
        val id1 = UUID.randomUUID().toString()
        val attachmentEntity1 = generateSingleAttachment(id1)
        dao.insertAttachment(attachmentEntity1)
    }

    @Test
    fun insertAttachments_success() = runTest {
        val id1 = UUID.randomUUID().toString()
        val id2 = UUID.randomUUID().toString()
        val todoEntity1 = generateSingleTodoEntity(id1)
        val todoEntity2 = generateSingleTodoEntity(id2)
        val attachmentEntity1 = generateSingleAttachment(id1)
        val attachmentEntity2 = generateSingleAttachment(id2)
        val attachmentList = listOf(attachmentEntity1, attachmentEntity2)

        dao.insertTodo(todoEntity1)
        dao.insertTodo(todoEntity2)

        dao.insertAttachments(attachmentList)

        val dbData = dao.getAttachments().first()

        assertEquals(attachmentList, dbData)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertAttachments_fail() = runTest {
        val id1 = UUID.randomUUID().toString()
        val id2 = UUID.randomUUID().toString()
        val attachmentEntity1 = generateSingleAttachment(id1)
        val attachmentEntity2 = generateSingleAttachment(id2)
        val attachmentList = listOf(attachmentEntity1, attachmentEntity2)

        dao.insertAttachments(attachmentList)

        val dbData = dao.getAttachments().first()

        assertEquals(attachmentList, dbData)
    }

    @Test
    fun getAttachments() = runTest {
        val id1 = UUID.randomUUID().toString()
        val id2 = UUID.randomUUID().toString()
        val todoEntity1 = generateSingleTodoEntity(id1)
        val todoEntity2 = generateSingleTodoEntity(id2)
        val attachmentEntity1 = generateSingleAttachment(id1)
        val attachmentEntity2 = generateSingleAttachment(id2)
        val attachmentList = listOf(attachmentEntity1, attachmentEntity2)

        dao.insertTodo(todoEntity1)
        dao.insertTodo(todoEntity2)

        dao.insertAttachments(attachmentList)

        val dbData = dao.getAttachments().first()

        assertEquals(attachmentList, dbData)
    }

    @Test
    fun deleteAttachment() = runTest {
        val id1 = UUID.randomUUID().toString()
        val id2 = UUID.randomUUID().toString()
        val todoEntity1 = generateSingleTodoEntity(id1)
        val todoEntity2 = generateSingleTodoEntity(id2)
        val attachmentEntity1 = generateSingleAttachment(id1)
        val attachmentEntity2 = generateSingleAttachment(id2)
        val attachmentList = listOf(attachmentEntity1, attachmentEntity2)

        dao.insertTodo(todoEntity1)
        dao.insertTodo(todoEntity2)

        dao.insertAttachments(attachmentList)

        val dbData = dao.getAttachments().first()
        val attachmentToDelete = dbData[nextInt(dbData.size - 1)]
        dao.deleteAttachment(attachmentToDelete.attachmentId)
        val updatedDbData = dao.getAttachments().first()

        assertTrue(!updatedDbData.contains(attachmentToDelete))
    }

    @Test
    fun insertTodoCategory() = runTest {
        val todoCategory = TodoCategoryEntity("Random category")
        dao.insertTodoCategory(todoCategory)

        val dbData = dao.getTodoCategories().first().first()
        assertEquals(todoCategory, dbData)
    }

    @Test
    fun getTodoCategories() = runTest {
        val todoCategory1 = TodoCategoryEntity("Random category")
        val todoCategory2 = TodoCategoryEntity("Category random")
        val listOfCategory = listOf(todoCategory1, todoCategory2)
        listOfCategory.forEach {
            dao.insertTodoCategory(it)
        }

        val dbData = dao.getTodoCategories().first()
        assertEquals(listOfCategory, dbData)
    }

    @Test
    fun deleteTodoCategory() = runTest {
        val todoCategory1 = TodoCategoryEntity("Random category")
        val todoCategory2 = TodoCategoryEntity("Category random")
        val listOfCategory = listOf(todoCategory1, todoCategory2)
        listOfCategory.forEach {
            dao.insertTodoCategory(it)
        }

        val dbData = dao.getTodoCategories().first()
        assertEquals(listOfCategory, dbData)
        val categoryToDelete = listOfCategory[nextInt(listOfCategory.size - 1)]
        dao.deleteTodoCategory(categoryToDelete.todoCategoryName)
        val updatedDbData = dao.getTodoCategories().first()

        assertTrue(!updatedDbData.contains(categoryToDelete))
    }

    @Test
    fun getTodoAndNote() = runTest {
        val todoId = UUID.randomUUID().toString()
        val todoEntity = generateSingleTodoEntity(todoId)
        val noteEntity = generateSingleNoteEntity(todoId)
        dao.insertTodo(todoEntity)
        val dbData = dao.getTodoAndNote(todoId).first()
        assertEquals(todoEntity, dbData?.todo)
        assertNull(dbData?.note)
        dao.insertNote(noteEntity)
        val updatedDbData = dao.getTodoAndNote(todoId).first()
        val expectedResult = TodoAndNoteEntity(todo = todoEntity, note = noteEntity)
        assertEquals(expectedResult, updatedDbData)
    }

    @Test
    fun getTodoWithTasks() = runTest {
        val todoId = UUID.randomUUID().toString()
        val todoEntity = generateSingleTodoEntity(todoId)
        val taskEntity1 = generateSingleTaskEntity(todoId, 0)
        val taskEntity2 = generateSingleTaskEntity(todoId, 1)
        val taskEntityList = listOf(taskEntity1, taskEntity2)
        dao.insertTodo(todoEntity)
        val dbData = dao.getTodoWithTasks(todoId).first()
        assertEquals(todoEntity, dbData?.todo)
        assertTrue(dbData?.tasks?.isEmpty()!!)

        dao.insertTasks(taskEntityList)
        val todoWithTasks = TodoWithTasksEntity(
            todo = todoEntity, tasks = taskEntityList
        )
        val updatedDbData = dao.getTodoWithTasks(todoId).first()
        assertEquals(todoWithTasks, updatedDbData)
    }

    @Test
    fun getTodoWithAttachments() = runTest {
        val todoId = UUID.randomUUID().toString()
        val todoEntity = generateSingleTodoEntity(todoId)
        val attachmentEntity1 = generateSingleAttachment(todoId)
        val attachmentEntity2 = generateSingleAttachment(todoId)
        val attachmentEntityList = listOf(attachmentEntity1, attachmentEntity2)
        dao.insertTodo(todoEntity)
        val dbData = dao.getTodoWithAttachments(todoId).first()
        assertEquals(todoEntity, dbData?.todo)
        assertTrue(dbData?.attachments?.isEmpty()!!)

        dao.insertAttachments(attachmentEntityList)
        val updatedDbData = dao.getTodoWithAttachments(todoId).first()
        val expectedResult = TodoWithAttachmentsEntity(todo = todoEntity, attachments = attachmentEntityList)
        assertEquals(expectedResult, updatedDbData)
    }

    @Test
    fun getTodoDetails() = runTest{
        val todoId = UUID.randomUUID().toString()
        val todoEntity = generateSingleTodoEntity(todoId)
        val taskEntity1 = generateSingleTaskEntity(todoId, 0)
        val taskEntity2 = generateSingleTaskEntity(todoId, 1)
        val taskEntityList = listOf(taskEntity1, taskEntity2)
        val noteEntity = generateSingleNoteEntity(todoId)
        val attachmentEntity1 = generateSingleAttachment(todoId)
        val attachmentEntity2 = generateSingleAttachment(todoId)
        val attachmentEntityList = listOf(attachmentEntity1, attachmentEntity2)

        dao.insertTodo(todoEntity)
        dao.insertTasks(taskEntityList)
        dao.insertNote(noteEntity)
        dao.insertAttachments(attachmentEntityList)

        val expectedResult = TodoDetailsEntity(todo = todoEntity, tasks = taskEntityList, note = noteEntity, attachments = attachmentEntityList )
        val dbData = dao.getTodoDetails(todoId).first()

        assertEquals(expectedResult, dbData)
    }
}