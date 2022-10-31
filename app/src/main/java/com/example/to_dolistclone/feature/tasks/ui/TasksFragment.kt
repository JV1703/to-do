package com.example.to_dolistclone.feature.tasks.ui

import android.os.Bundle
import android.view.View
import com.example.to_dolistclone.databinding.FragmentTasksBinding
import com.example.to_dolistclone.feature.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : BaseFragment<FragmentTasksBinding>(FragmentTasksBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}