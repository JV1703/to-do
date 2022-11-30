package com.example.to_dolistclone.feature.detail.ui.note

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.utils.ui.collectLatestLifecycleFlow
import com.example.to_dolistclone.core.utils.ui.makeToast
import com.example.to_dolistclone.databinding.FragmentNoteBinding
import com.example.to_dolistclone.feature.BaseFragment
import com.example.to_dolistclone.feature.detail.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NoteFragment : BaseFragment<FragmentNoteBinding>(FragmentNoteBinding::inflate) {

    @Inject
    lateinit var dateUtil: DateUtil

    private val viewModel: DetailsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->

            val note = uiState.todoDetails?.note

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
                    title = binding.titleEt.text.toString().trim(),
                    body = binding.bodyEt.text.toString().trim(),
                    createdAt = note?.created_at
                )

                if (binding.titleEt.text?.isNotEmpty() == true || binding.bodyEt.text?.isNotEmpty() == true) {
                    viewModel.insertNote(newNote)
                }else{
                    viewModel.deleteNote()
                }

                makeToast("haha")
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

}