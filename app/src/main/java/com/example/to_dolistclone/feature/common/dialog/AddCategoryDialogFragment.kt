package com.example.to_dolistclone.feature.common.dialog

import android.animation.ObjectAnimator
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.to_dolistclone.databinding.TodoCategoryDialogFragmentBinding
import com.example.to_dolistclone.feature.detail.viewmodel.DetailsViewModel
import com.example.to_dolistclone.feature.todo.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCategoryDialogFragment(private val saveClickListener: (String) -> Unit) :
    DialogFragment() {

    private var _binding: TodoCategoryDialogFragmentBinding? = null
    private val binding get() = _binding!!
    private val todoViewModel: TodoViewModel by activityViewModels()
    private val detailsViewModel: DetailsViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(null)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = TodoCategoryDialogFragmentBinding.inflate(layoutInflater)

        binding.save.setOnClickListener {
            saveClickListener(binding.input.text.toString().trim())
            dialog?.dismiss()
        }
        binding.input.addTextChangedListener(textWatcher)

        return AlertDialog.Builder(requireContext()).setView(binding.root).create()
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let {
                binding.textCounter.text = "${it?.length}/50"
                if (s.length >= 50) {
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