package com.example.to_dolistclone.feature.todo.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.utils.ui.collectLatestLifecycleFlow
import com.example.to_dolistclone.databinding.ActivityCompletedTodosBinding
import com.example.to_dolistclone.feature.detail.ui.DetailsActivity
import com.example.to_dolistclone.feature.todo.adapter.CompletedTodosAdapter
import com.example.to_dolistclone.feature.todo.adapter.CompletedTodosAdapterClickListener
import com.example.to_dolistclone.feature.todo.viewmodel.CompletedTodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CompletedTodosActivity : AppCompatActivity(), CompletedTodosAdapterClickListener {

    @Inject
    lateinit var dateUtil: DateUtil

    private lateinit var binding: ActivityCompletedTodosBinding

    private val viewModel: CompletedTodoViewModel by viewModels()
    private lateinit var rvAdapter: CompletedTodosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompletedTodosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRv()

        collectLatestLifecycleFlow(viewModel.completedTodosList) {
            rvAdapter.submitList(it)
        }
    }

    private fun setupRv() {
        rvAdapter = CompletedTodosAdapter(dateUtil, this)
        binding.completedTodoRv.adapter = rvAdapter
    }

    override fun navigate(todo: Todo) {
        val intent = Intent(this, DetailsActivity::class.java)
        viewModel.saveSelectedTodoId(todoId = todo.todoId)
        startActivity(intent)
    }

    override fun complete(todoId: String, isComplete: Boolean) {
        viewModel.updateTodoCompletion(todoId, isComplete)
    }
}