package com.example.to_dolistclone.feature.tasks.ui

import android.os.Bundle
import android.view.View
import com.example.to_dolistclone.R
import com.example.to_dolistclone.core.utils.ui.pulseAnimation
import com.example.to_dolistclone.databinding.FragmentTasksBinding
import com.example.to_dolistclone.feature.BaseFragment
import com.example.to_dolistclone.feature.common.dialog.DialogsManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TasksFragment : BaseFragment<FragmentTasksBinding>(FragmentTasksBinding::inflate) {

    @Inject
    lateinit var dialogsManager: DialogsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabFade.setImageResource(R.drawable.fab_bg)
        binding.fabFade.pulseAnimation()
        binding.add.setOnClickListener {
            dialogsManager.createTaskModalBottomSheet()
        }
    }

}