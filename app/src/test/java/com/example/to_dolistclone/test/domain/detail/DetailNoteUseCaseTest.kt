package com.example.to_dolistclone.test.domain.detail

import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.data.local.model.NoteEntity
import com.example.to_dolistclone.core.data.local.model.toNote
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.fake.FakeTodoRepository
import com.example.to_dolistclone.feature.detail.domain.implementation.DetailNoteUseCaseImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.random.Random.Default.nextInt

@OptIn(ExperimentalCoroutinesApi::class)
class DetailNoteUseCaseTest {

    private lateinit var fakeTodoRepository: TodoRepository
    private lateinit var useCase: DetailNoteUseCaseImpl
    private lateinit var dateUtil: DateUtil
    private lateinit var fakeNoteEntityList: List<NoteEntity>

    @Before
    fun setup() {
        fakeTodoRepository = FakeTodoRepository()
        useCase = DetailNoteUseCaseImpl(fakeTodoRepository)
        dateUtil = DateUtil()
        fakeNoteEntityList = generateNoteEntityList()
    }

    private fun generateNoteEntityList(n: Int = 10): List<NoteEntity> {
        val noteEntityList = arrayListOf<NoteEntity>()
        for (i in 0 until n) {
            val noteEntity = NoteEntity(
                noteId = UUID.randomUUID().toString(),
                title = "title $i",
                body = "body $i",
                created_at = dateUtil.getCurrentDateTimeLong(),
                updated_at = dateUtil.getCurrentDateTimeLong()
            )
            noteEntityList.add(noteEntity)
        }
        return noteEntityList
    }

    @Test
    fun insertNote() = runTest {
        fakeNoteEntityList.forEach { useCase.insertNote(it.toNote()) }

        val actual = fakeTodoRepository.getNotes().first()

        assertEquals(fakeNoteEntityList.map { it.toNote() }, actual)
    }

    @Test
    fun deleteNote() = runTest {
        fakeNoteEntityList.forEach { useCase.insertNote(it.toNote()) }

        val noteToDelete = fakeTodoRepository.getNotes().first()[nextInt(
            fakeTodoRepository.getNotes().first().size - 1
        )]

        assertTrue(fakeTodoRepository.getNotes().first().contains(noteToDelete))

        useCase.deleteNote(noteToDelete.noteId)

        val updatedList = fakeTodoRepository.getNotes().first()

        assertTrue(!updatedList.contains(noteToDelete))
    }

}