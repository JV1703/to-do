package com.example.to_dolistclone.feature.common.dialog

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.to_dolistclone.core.utils.ui.makeToast
import com.example.to_dolistclone.databinding.TodoCategoryDialogFragmentBinding
import com.example.to_dolistclone.feature.tasks.viewmodel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCategoryDialogFragment : DialogFragment() {

    private var _binding: TodoCategoryDialogFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TasksViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TodoCategoryDialogFragmentBinding.inflate(inflater, container, false)
        binding.save.setOnClickListener {
            if (binding.input.text.isNotEmpty()) {
                viewModel.insertTodoCategory(binding.input.text.toString().trim())
                viewModel.updateSelectedCategory(binding.input.text.toString().trim())
                dialog?.dismiss()
            } else {
                makeToast("Please provide name for category")
            }
        }
        binding.input.addTextChangedListener(textWatcher)
        return binding.root
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            p0?.let {
                binding.textCounter.text = "${it.length}/50"
                if (it.length >= 50) {
                    vibrate(binding.textCounter)
                }
            }
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    }

    private fun vibrate(textView: TextView) {
        val rotate = ObjectAnimator.ofFloat(textView, "rotation", 0f, 20f, 0f, -20f, 0f)
        rotate.repeatCount = 5
        rotate.duration = 100
        rotate.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.input.removeTextChangedListener(textWatcher)
        _binding = null
    }

    companion object {
        const val TAG = "Add Category Dialog Fragment"
    }

}