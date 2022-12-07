package com.example.to_dolistclone.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.example.note.utils.MainCoroutineRule
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.data.local.model.NoteEntity
import com.example.to_dolistclone.core.data.remote.firebase.*
import com.example.to_dolistclone.core.data.remote.model.NoteNetwork
import com.example.to_dolistclone.utils.TestDataGenerator
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
class NoteFirestoreTest {

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

    private lateinit var noteFirestore: NoteFirestoreImpl
    private lateinit var noteNetworkList: MutableList<NoteNetwork>
    private lateinit var TEST_NOTE_TODO_REF: String

    @Before
    fun setup() {
        hiltRule.inject()
        noteFirestore = NoteFirestoreImpl(firebaseFirestore)
        insertNotes()
        TEST_NOTE_TODO_REF = noteNetworkList[Random.nextInt(noteNetworkList.size - 1)].noteId
    }

    private fun insertNotes() {
        noteNetworkList = testDataGenerator.produceListOfNoteNetwork().toMutableList()
        noteNetworkList.forEach { note ->
            firebaseFirestore.collection(ACTIVE_COLLECTION).document(TEST_USER_ID_DOCUMENT)
                .collection(NOTE_COLLECTION).document(note.noteId).set(note)
        }
    }

    private fun generateSingleNoteNetwork(noteId: String): NoteNetwork = NoteNetwork(
        noteId = noteId,
        title = "Note title",
        body = "Note body",
        created_at = dateUtil.getCurrentDateTimeLong(),
        updated_at = dateUtil.getCurrentDateTimeLong()
    )

    @Test
    fun insertNote() = runTest {
        val noteId = UUID.randomUUID().toString()
        val newNote = generateSingleNoteNetwork(noteId)
        noteFirestore.upsertNote(TEST_USER_ID_DOCUMENT, newNote)
        val networkData = noteFirestore.getNotes(TEST_USER_ID_DOCUMENT)
        noteNetworkList.add(newNote)
        assertTrue(networkData.containsAll(noteNetworkList))
        noteFirestore.deleteNote(TEST_USER_ID_DOCUMENT, newNote.noteId)
    }

    @Test
    fun updateNote() = runTest {
        val noteToUpdate = noteNetworkList[nextInt(noteNetworkList.size-1)].copy(title = "Banana")
        noteFirestore.upsertNote(TEST_USER_ID_DOCUMENT, noteToUpdate)

        val networkData = noteFirestore.getNotes(TEST_USER_ID_DOCUMENT)
        assertTrue(networkData.contains(noteToUpdate))
    }

    @Test
    fun getNote() = runTest {
        val noteToGet = noteNetworkList[nextInt(noteNetworkList.size-1)]
        val networkData = noteFirestore.getNote(TEST_USER_ID_DOCUMENT, noteToGet.noteId)
        assertEquals(noteToGet, networkData)
    }

    @Test
    fun getNotes() = runTest {
        val networkData = noteFirestore.getNotes(TEST_USER_ID_DOCUMENT)
        assertTrue(networkData.containsAll(noteNetworkList))
    }

    @Test
    fun deleteNote() = runTest {
        val noteToDelete = noteNetworkList[nextInt(noteNetworkList.size-1)]
        noteFirestore.deleteNote(TEST_USER_ID_DOCUMENT, noteToDelete.noteId)
        val networkData = noteFirestore.getNotes(TEST_USER_ID_DOCUMENT)
        assertTrue(!networkData.contains(noteToDelete))
    }

}