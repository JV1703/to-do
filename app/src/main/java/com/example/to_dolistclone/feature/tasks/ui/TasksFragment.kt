package com.example.to_dolistclone.feature.tasks.ui

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.example.to_dolistclone.core.utils.ui.makeToast
import com.example.to_dolistclone.databinding.FragmentTasksBinding
import com.example.to_dolistclone.feature.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : BaseFragment<FragmentTasksBinding>(FragmentTasksBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabFade.pulseAnimation()

        binding.add.setOnClickListener {
            makeToast("navigate to details fragment")
        }
    }

    private fun ImageView.pulseAnimation(
        scaleX: Float = 1.5F,
        ScaleY: Float = 1.5F,
        alpha: Float = 0F,
        duration: Long = 1000,
        repeatCount: Int = ObjectAnimator.INFINITE,
        repeatMode: Int = ObjectAnimator.RESTART
    ) {
        val pulse = ObjectAnimator.ofPropertyValuesHolder(
            binding.fabFade,
            PropertyValuesHolder.ofFloat("scaleX", scaleX),
            PropertyValuesHolder.ofFloat("scaleY", ScaleY),
            PropertyValuesHolder.ofFloat("alpha", alpha)
        )
        pulse.duration = duration
        pulse.repeatCount = repeatCount
        pulse.repeatMode = repeatMode
        pulse.start()
    }

}