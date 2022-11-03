package com.example.to_dolistclone.feature.common.dialog

import androidx.fragment.app.FragmentManager
import javax.inject.Inject

class DialogsManager @Inject constructor(private val fragmentManager: FragmentManager) {

    fun createTaskModalBottomSheet() {
        val modalBottomSheet = ModalBottomSheet()
        modalBottomSheet.show(fragmentManager, ModalBottomSheet.TAG)
    }

    fun createAddCategoryDialogFragment(){
        val addCategoryDialogFragment = AddCategoryDialogFragment()
        addCategoryDialogFragment.show(fragmentManager, AddCategoryDialogFragment.TAG)
    }

}