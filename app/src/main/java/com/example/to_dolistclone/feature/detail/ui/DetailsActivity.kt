package com.example.to_dolistclone.feature.detail.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.common.TODO_ID
import com.example.to_dolistclone.databinding.ActivityDetailsBinding
import com.example.to_dolistclone.feature.common.CategoryPopupMenu
import com.example.to_dolistclone.feature.common.dialog.DialogsManager
import com.example.to_dolistclone.feature.detail.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var dialogsManager: DialogsManager

    @Inject
    lateinit var dateUtil: DateUtil

    @Inject
    lateinit var categoryPopupMenu: CategoryPopupMenu

    private lateinit var binding: ActivityDetailsBinding

    private val viewModel: DetailsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.saveTodoId(intent.extras?.getString(TODO_ID)!!)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}