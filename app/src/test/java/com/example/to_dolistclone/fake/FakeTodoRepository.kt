package com.example.to_dolistclone.fake

import com.example.to_dolistclone.core.data.local.model.*
import com.example.to_dolistclone.core.domain.model.*
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoCategoryWithTodos
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoWithAttachments
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoWithTasks
import com.example.to_dolistclone.core.domain.model.relation.one_to_one.TodoAndNote
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTodoRepository : TodoRepository {

    private val todoList: ArrayList<TodoEntity> = arrayListOf()
    private val noteList: ArrayList<NoteEntity> = arrayListOf()
    private val taskList: ArrayList<TaskEntity> = arrayListOf()
    private val attachmentList: ArrayList<AttachmentEntity> = arrayListOf()
    private val todoCategoryList: ArrayList<TodoCategoryEntity> = arrayListOf()

    private val stringDataStore: HashMap<String, String> = hashMapOf()
    private val intDataStore: HashMap<String, Int> = hashMapOf()
    private val booleanDataStore: HashMap<String, Boolean> = hashMapOf()

    private val SHOW_PREVIOUS = "show_previous"
    private val SHOW_TODAY = "show_today"
    private val SHOW_FUTURE = "show_future"
    private val SHOW_COMPLETED_TODAY = "show_completed_today"
    private val SELECTED_TODO_ID = "selected_todo_id"
    private val SELECTED_PIE_GRAPH_OPTION = "selected_pie_graph_option"

    override suspend fun saveShowPrevious(isShow: Boolean) {
        booleanDataStore[SHOW_PREVIOUS] = isShow
    }

    override suspend fun saveShowToday(isShow: Boolean) {
        booleanDataStore[SHOW_TODAY] = isShow
    }

    override suspend fun saveShowFuture(isShow: Boolean) {
        booleanDataStore[SHOW_FUTURE] = isShow
    }

    override suspend fun saveShowCompletedToday(isShow: Boolean) {
        booleanDataStore[SHOW_COMPLETED_TODAY] = isShow
    }

    override fun getShowPrevious(): Flow<Boolean> {
        return flow { emit(booleanDataStore[SHOW_PREVIOUS] ?: true) }
    }

    override fun getShowToday(): Flow<Boolean> {
        return flow { emit(booleanDataStore[SHOW_TODAY] ?: true) }
    }

    override fun getShowFuture(): Flow<Boolean> {
        return flow { emit(booleanDataStore[SHOW_FUTURE] ?: true) }
    }

    override fun getShowCompletedToday(): Flow<Boolean> {
        return flow { emit(booleanDataStore[SHOW_COMPLETED_TODAY] ?: true) }
    }

    override suspend fun insertTodo(todo: Todo): Long {
        val todoEntity = todo.toTodoEntity()
        todoList.add(todoEntity)
        return if (todoList.contains(todoEntity)) {
            todoList.indexOf(todoEntity).toLong()
        } else {
            -1
        }
    }

    override fun getTodo(todoId: String): Flow<Todo> {
        return flow { emit(todoList.find { it.todoId == todoId }?.toTodo()!!) }
    }

    override suspend fun updateTodoTitle(todoId: String, title: String): Int {
        val todo = todoList.find { it.todoId == todoId }
        val index = todoList.indexOf(todo)
        return if (todo != null) {
            todoList[index] = todo.copy(title = title)
            index
        } else {
            -1
        }
    }

    override suspend fun updateTodoCategory(todoId: String, category: String): Int {
        val todo = todoList.find { it.todoId == todoId }
        val index = todoList.indexOf(todo)
        return if (todo != null) {
            todoList[index] = todo.copy(todoCategoryRefName = category)
            index
        } else {
            -1
        }
    }

    override suspend fun updateTodoDeadline(todoId: String, deadline: Long?): Int {
        val todo = todoList.find { it.todoId == todoId }
        val index = todoList.indexOf(todo)
        return if (todo != null) {
            todoList[index] = todo.copy(deadline = deadline)
            index
        } else {
            -1
        }
    }

    override suspend fun updateTodoReminder(todoId: String, reminder: Long?): Int {
        val todo = todoList.find { it.todoId == todoId }
        val index = todoList.indexOf(todo)
        return if (todo != null) {
            todoList[index] = todo.copy(reminder = reminder)
            index
        } else {
            -1
        }
    }

    override suspend fun updateTodoCompletion(
        todoId: String, isComplete: Boolean, completedOn: Long?
    ): Int {
        val todo = todoList.find { it.todoId == todoId }
        val index = todoList.indexOf(todo)
        return if (todo != null) {
            todoList[index] = todo.copy(isComplete = isComplete, completedOn = completedOn)
            index
        } else {
            -1
        }
    }

    override suspend fun updateTodoTasksAvailability(
        todoId: String, tasksAvailability: Boolean
    ): Int {
        val todo = todoList.find { it.todoId == todoId }
        val index = todoList.indexOf(todo)
        return if (todo != null) {
            todoList[index] = todo.copy(tasks = tasksAvailability)
            index
        } else {
            -1
        }
    }

    override suspend fun updateTodoNotesAvailability(
        todoId: String, notesAvailability: Boolean
    ): Int {
        val todo = todoList.find { it.todoId == todoId }
        val index = todoList.indexOf(todo)
        return if (todo != null) {
            todoList[index] = todo.copy(notes = notesAvailability)
            index
        } else {
            -1
        }
    }

    override suspend fun updateTodoAttachmentsAvailability(
        todoId: String, attachmentsAvailability: Boolean
    ): Int {
        val todo = todoList.find { it.todoId == todoId }
        val index = todoList.indexOf(todo)
        return if (todo != null) {
            todoList[index] = todo.copy(attachments = attachmentsAvailability)
            index
        } else {
            -1
        }
    }

    override suspend fun updateTodoAlarmRef(todoId: String, alarmRef: Int?): Int {
        val todo = todoList.find { it.todoId == todoId }
        val index = todoList.indexOf(todo)
        return if (todo != null) {
            todoList[index] = todo.copy(alarmRef = alarmRef)
            index
        } else {
            -1
        }
    }

    override fun getTodoDetails(todoId: String): Flow<TodoDetails?> {
        val todo = todoList.find { it.todoId == todoId }?.toTodo()
        val tasks = todo?.let { todo ->
            taskList.filter { it.todoRefId == todoId }.map {
                it.toTask()
            }
        } ?: emptyList()
        val note = todo?.let { todo ->
            noteList.find { it.noteId == todoId }?.toNote()
        }
        val attachments = todo?.let { todo ->
            attachmentList.filter { it.todoRefId == todoId }.map {
                it.toAttachment()
            }
        } ?: emptyList()

        val todoDetails = todo?.let {
            TodoDetails(
                todo = it, tasks = tasks, note = note, attachments = attachments
            )
        }

        return flow { emit(todoDetails) }
    }

    override fun getTodos(): Flow<List<Todo>> {
        return flow { emit(todoList.map { todo -> todo.toTodo() }) }
    }

    override suspend fun deleteTodo(todoId: String): Int {
        val todo = todoList.find { it.todoId == todoId }
        val isSuccess = if (todo == null) {
            -1
        } else {
            1
        }

        if (isSuccess == 1) {
            val taskListFiltered = taskList.filter { it.todoRefId == todoId }
            taskListFiltered.forEach {
                taskList.remove(it)
            }

            val noteListFiltered = noteList.filter { it.noteId == todoId }
            noteListFiltered.forEach {
                noteList.remove(it)
            }

            val attachmentListFiltered = attachmentList.filter { it.todoRefId == todoId }
            attachmentListFiltered.forEach {
                attachmentList.remove(it)
            }
        }

        return isSuccess
    }

    override suspend fun insertNote(note: Note): Long {
        val noteEntity = note.toNoteEntity()
        noteList.add(noteEntity)
        return if (noteList.contains(noteEntity)) {
            noteList.indexOf(noteEntity).toLong()
        } else {
            -1
        }
    }

    override fun getNotes(): Flow<List<Note>> = flow { emit(noteList.map { it.toNote() }) }

    override suspend fun deleteNote(noteId: String): Int {
        val note = noteList.find { it.noteId == noteId }
        noteList.remove(note)
        return if (note == null) {
            -1
        } else {
            1
        }
    }

    override suspend fun insertTask(task: Task): Long {
        val taskEntity = task.toTaskEntity()
        taskList.add(taskEntity)
        return if (taskList.contains(taskEntity)) {
            taskList.indexOf(taskEntity).toLong()
        } else {
            -1
        }
    }

    override suspend fun updateTaskPosition(taskId: String, position: Int): Int {
        val task = taskList.find { it.taskId == taskId }
        val index = taskList.indexOf(task)
        return if (task != null) {
            taskList[index] = task.copy(position = position)
            index
        } else {
            -1
        }
    }

    override suspend fun updateTaskTitle(taskId: String, title: String): Int {
        val task = taskList.find { it.taskId == taskId }
        val index = taskList.indexOf(task)
        return if (task != null) {
            taskList[index] = task.copy(task = title)
            index
        } else {
            -1
        }
    }

    override suspend fun updateTaskCompletion(taskId: String, isComplete: Boolean): Int {
        val task = taskList.find { it.taskId == taskId }
        val index = taskList.indexOf(task)
        return if (task != null) {
            taskList[index] = task.copy(isComplete = isComplete)
            index
        } else {
            -1
        }
    }

    override suspend fun insertTasks(tasks: List<Task>): LongArray {
        val output: ArrayList<Long> = arrayListOf()
        tasks.forEach { task ->
            val taskEntity = task.toTaskEntity()
            taskList.add(taskEntity)
            if (taskList.contains(taskEntity)) {
                val index = taskList.indexOf(taskEntity).toLong()
                output.add(index)
            } else {
                output.add(-1)
            }
        }

        return output.toLongArray()
    }

    override fun getTasks(): Flow<List<Task>> = flow { emit(taskList.map { it.toTask() }) }

    override suspend fun deleteTask(taskId: String): Int {
        val task = taskList.find { it.taskId == taskId }
        taskList.remove(task)
        return if (task == null) {
            -1
        } else {
            1
        }
    }

    override suspend fun insertAttachment(attachment: Attachment): Long {
        val attachmentEntity = attachment.toAttachmentEntity()
        attachmentList.add(attachmentEntity)
        return if (attachmentList.contains(attachmentEntity)) {
            attachmentList.indexOf(attachmentEntity).toLong()
        } else {
            -1
        }
    }

    override suspend fun insertAttachments(attachments: List<Attachment>): LongArray {
        val output: ArrayList<Long> = arrayListOf()
        attachments.forEach { attachment ->
            val attachmentEntity = attachment.toAttachmentEntity()
            attachmentList.add(attachmentEntity)
            if (attachmentList.contains(attachmentEntity)) {
                val index = attachmentList.indexOf(attachmentEntity).toLong()
                output.add(index)
            } else {
                output.add(-1)
            }
        }

        return output.toLongArray()
    }

    override fun getAttachments(): Flow<List<Attachment>> =
        flow { emit(attachmentList.map { it.toAttachment() }) }

    override suspend fun deleteAttachment(attachmentId: String): Int {
        val attachment = attachmentList.find { it.attachmentId == attachmentId }
        attachmentList.remove(attachment)
        return if (attachment == null) {
            -1
        } else {
            1
        }
    }

    override suspend fun insertTodoCategory(todoCategory: TodoCategory): Long {
        val todoCategoryEntity = todoCategory.toTodoEntity()
        todoCategoryList.add(todoCategoryEntity)
        return if (todoCategoryList.contains(todoCategoryEntity)) {
            todoCategoryList.indexOf(todoCategoryEntity).toLong()
        } else {
            -1
        }
    }

    override fun getTodoCategories(): Flow<List<TodoCategory>> {
        return flow { emit(todoCategoryList.map { it.toTodoCategory() }) }
    }

    override suspend fun deleteTodoCategory(todoCategoryName: String): Int {
        val todoCategory = todoCategoryList.find { it.todoCategoryName == todoCategoryName }
        todoCategoryList.remove(todoCategory)
        return if (todoCategory == null) {
            -1
        } else {
            1
        }
    }

    override fun getTodoAndNoteWithTodoId(todoId: String): Flow<TodoAndNote?> {
        val todo = todoList.find { it.todoId == todoId }
        val note = noteList.find { it.noteId == todoId }
        val todoAndNote = todo?.let {
            TodoAndNote(
                todo = it, note = note
            )
        }
        return flow { emit(todoAndNote) }
    }

    override fun getTodoWithTasks(todoId: String): Flow<TodoWithTasks?> {
        val todo = todoList.find { it.todoId == todoId }
        val tasks = taskList.filter { it.todoRefId == todoId }
        val todoWithTasks = todo?.let {
            TodoWithTasks(
                todo = it, tasks = tasks
            )
        }
        return flow { emit(todoWithTasks) }
    }

    override fun getTodoWithAttachments(todoId: String): Flow<TodoWithAttachments?> {
        val todo = todoList.find { it.todoId == todoId }
        val attachments = attachmentList.filter { it.todoRefId == todoId }
        val todoWithAttachments = todo?.let {
            TodoWithAttachments(
                todo = it, attachments = attachments
            )
        }

        return flow { emit(todoWithAttachments) }
    }

    override fun getTodoCategoryWithTodos(todoCategoryName: String): Flow<TodoCategoryWithTodos?> {
        val todoCategory = todoCategoryList.find { it.todoCategoryName == todoCategoryName }
        val todos = todoList.filter { it.todoCategoryRefName == todoCategoryName }
        val todoCategoryWithTodos = todoCategory?.let {
            TodoCategoryWithTodos(
                todoCategory = todoCategory, todos = todos
            )
        }

        return flow { emit(todoCategoryWithTodos) }
    }

    override fun getTodoCategoriesWithTodos(): Flow<List<TodoCategoryWithTodos>> {
        val todoCategoryWithTodosList = todoCategoryList.map { todoCategory ->
            TodoCategoryWithTodos(todoCategory = todoCategory,
                todos = todoList.filter { it.todoCategoryRefName == todoCategory.todoCategoryName })
        }

        return flow { emit(todoCategoryWithTodosList) }
    }

    override suspend fun saveSelectedTodoId(todoId: String) {
        stringDataStore[SELECTED_TODO_ID] = todoId
    }

    override fun getSelectedTodoId(): Flow<String> {
        return flow { emit(stringDataStore[SELECTED_TODO_ID] ?: "") }
    }

    override suspend fun saveSelectedPieGraphOption(selectedOption: Int) {
        intDataStore[SELECTED_PIE_GRAPH_OPTION]
    }

    override fun getSelectedPieGraphOption(): Flow<Int> {
        return flow { emit(intDataStore[SELECTED_PIE_GRAPH_OPTION] ?: 0) }
    }
}