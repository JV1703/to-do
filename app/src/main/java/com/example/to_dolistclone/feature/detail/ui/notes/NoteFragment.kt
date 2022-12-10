package com.example.to_dolistclone.feature.detail.ui.notes

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.utils.ui.collectLatestLifecycleFlow
import com.example.to_dolistclone.databinding.FragmentNoteBinding
import com.example.to_dolistclone.feature.BaseFragment
import com.example.to_dolistclone.feature.detail.viewmodel.DetailsViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NoteFragment : BaseFragment<FragmentNoteBinding>(FragmentNoteBinding::inflate) {

    @Inject
    lateinit var dateUtil: DateUtil

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val viewModel: DetailsViewModel by viewModels()
    private lateinit var todoId: String

    private var initialNoteTitle: String? = null
    private var initialNoteBody: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectLatestLifecycleFlow(viewModel.todoId) {
            todoId = it
        }

        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->

            val note = uiState.todoDetails?.note

            initialNoteTitle = uiState.todoDetails?.note?.title
            initialNoteBody = uiState.todoDetails?.note?.body

            binding.lastUpdateTv.isGone = note == null

            note?.let {
                binding.apply {
                    titleEt.setText(it.title)
                    lastUpdateTv.text = "Last update time: ${
                        dateUtil.toString(
                            it.updated_at, "EEE, MMM dd hh:mm a", Locale.getDefault()
                        )
                    }"
                    bodyEt.setText(it.body)
                }
            }

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                val newNote = viewModel.createNote(
                    noteId = todoId,
                    title = binding.titleEt.text.toString().trim(),
                    body = binding.bodyEt.text.toString().trim(),
                    createdAt = note?.created_at
                )

                if (binding.titleEt.text?.isNotEmpty() == true || binding.bodyEt.text?.isNotEmpty() == true) {
                    viewModel.upsertNote(userId = firebaseAuth.currentUser!!.uid, note = newNote)
                    viewModel.updateTodoNotesAvailability(
                        userId = firebaseAuth.currentUser!!.uid,
                        todoId = todoId,
                        notesAvailability = true
                    )
                } else {
                    viewModel.deleteNote(userId = firebaseAuth.currentUser!!.uid, noteId = todoId)
                    viewModel.updateTodoNotesAvailability(
                        userId = firebaseAuth.currentUser!!.uid,
                        todoId = todoId,
                        notesAvailability = false
                    )
                }

                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

}