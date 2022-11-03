package com.example.to_dolistclone.feature.common.dialog

import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.PopupMenu
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.to_dolistclone.R
import com.example.to_dolistclone.core.utils.ui.collectLatestLifecycleFlow
import com.example.to_dolistclone.core.utils.ui.makeToast
import com.example.to_dolistclone.databinding.ModalBottomSheetContentBinding
import com.example.to_dolistclone.feature.tasks.viewmodel.TasksViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ModalBottomSheet : BottomSheetDialogFragment() {

    @Inject
    lateinit var dialogsManager: DialogsManager
    private val viewModel: TasksViewModel by viewModels()

    private var _binding: ModalBottomSheetContentBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(null)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ModalBottomSheetContentBinding.inflate(inflater, container, false)

        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->
            binding.category.setOnClickListener {
                showCategoryPopupMenu(it, uiState.categories, uiState.selectedCategory)
            }
            binding.category.text = uiState.selectedCategory ?: "No Category"
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun showCategoryPopupMenu(
        v: View,
        setOfTodoCategories: Set<String>,
        selectedCategory: String?
    ) {
        val popup = PopupMenu(requireContext(), v)
        setupMenuOptions(popup.menu, setOfTodoCategories, selectedCategory)

        val inflater = popup.menuInflater
        inflater.inflate(R.menu.category_pop_up_menu, popup.menu)
        popup.setForceShowIcon(true)
        popup.setOnMenuItemClickListener(popUpMenuClickListener)

        popup.show()
    }

    private fun generateOptions(set: Set<String>): MutableSet<String> {
        val categories = mutableSetOf<String>()
        categories.add("No Category")
        set.forEach { category -> categories.add(category) }
        categories.add("Create New")
        return categories
    }

    private fun setupMenuOptions(menu: Menu, categoryList: Set<String>, selectedCategory: String?) {
        generateOptions(categoryList).forEachIndexed { index, title ->
            menu.add(Menu.NONE, index, index, title)
        }
        menu.getItem(categoryList.size + 1).icon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)
        if(categoryList.isNotEmpty()){
            if (selectedCategory == null) {
                setStringColor(menu.getItem(0), R.color.teal_200)
            } else {
                setStringColor(
                    menu.getItem(categoryList.indexOf(selectedCategory) + 1),
                    R.color.teal_200
                )
            }
        }
    }

    private fun setStringColor(menuItem: MenuItem, @ColorRes color: Int) {
        val title = SpannableString(menuItem.title)
        title.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), color)),
            0,
            title.length,
            0
        )
        menuItem.title = title
    }

    private val popUpMenuClickListener = PopupMenu.OnMenuItemClickListener { menuItem ->
        when (menuItem.title) {
            "Create New" -> {
                dialogsManager.createAddCategoryDialogFragment()
                true
            }
            else -> {
                makeToast(menuItem.title.toString())
                viewModel.updateSelectedCategory(menuItem.title.toString())
                binding.category.text = menuItem.title
                true
            }
        }
    }
}