package com.example.to_dolistclone.test.domain.detail

import com.example.to_dolistclone.core.data.local.model.AttachmentEntity
import com.example.to_dolistclone.core.data.local.model.toAttachment
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.fake.FakeTodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.DetailAttachmentUseCase
import com.example.to_dolistclone.feature.detail.domain.implementation.DetailAttachmentUseCaseImpl
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
class DetailAttachmentUseCaseTest {

    private lateinit var fakeTodoRepository: TodoRepository
    private lateinit var useCase: DetailAttachmentUseCase
    private lateinit var fakeAttachmentEntityList: List<AttachmentEntity>

    @Before
    fun setup() {
        fakeTodoRepository = FakeTodoRepository()
        useCase = DetailAttachmentUseCaseImpl(fakeTodoRepository)
        fakeAttachmentEntityList = generateAttachmentEntity()
    }

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

    @Test
    fun insertAttachment() = runTest {
        fakeAttachmentEntityList.forEach {
            useCase.insertAttachment(it.toAttachment())
        }

        val actual = fakeTodoRepository.getAttachments().first()

        assertEquals(fakeAttachmentEntityList.map { it.toAttachment() }, actual)
    }

    @Test
    fun deleteAttachment() = runTest {
        fakeAttachmentEntityList.forEach {
            useCase.insertAttachment(it.toAttachment())
        }

        assertTrue(fakeTodoRepository.getAttachments().first().isNotEmpty())

        val actualList = fakeTodoRepository.getAttachments().first()
        val attachmentToDelete = actualList[nextInt(actualList.size - 1)]
        useCase.deleteAttachment(attachmentToDelete.attachmentId)
        val updatedList = fakeTodoRepository.getAttachments().first()
        assertTrue(!updatedList.contains(attachmentToDelete))
    }

}