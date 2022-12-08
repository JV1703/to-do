package com.example.to_dolistclone.feature.todo.ui

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.domain.model.Todo
import com.example.to_dolistclone.core.utils.ui.collectLatestLifecycleFlow
import com.example.to_dolistclone.databinding.FragmentTodoBinding
import com.example.to_dolistclone.feature.BaseFragment
import com.example.to_dolistclone.feature.common.dialog.DialogsManager
import com.example.to_dolistclone.feature.detail.ui.DetailsActivity
import com.example.to_dolistclone.feature.todo.adapter.TodosAdapter
import com.example.to_dolistclone.feature.todo.adapter.TodosAdapterClickListener
import com.example.to_dolistclone.feature.todo.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TodoFragment : BaseFragment<FragmentTodoBinding>(FragmentTodoBinding::inflate),
    TodosAdapterClickListener {

    @Inject
    lateinit var dialogsManager: DialogsManager

    @Inject
    lateinit var dateUtil: DateUtil

    private val viewModel: TodoViewModel by viewModels()

    private lateinit var previousAdapter: TodosAdapter
    private lateinit var todayAdapter: TodosAdapter
    private lateinit var futureAdapter: TodosAdapter
    private lateinit var completedToday: TodosAdapter

    private lateinit var pulseObjetAnimator: ObjectAnimator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()

        pulseAnimation(binding.fabFade)
        binding.add.setOnClickListener {
            dialogsManager.showTodoShortcut()
        }

        binding.viewCompletedTaskTv.setOnClickListener {
            val intent = Intent(requireContext(), CompletedTodosActivity::class.java)
            startActivity(intent)
        }

        collectLatestLifecycleFlow(viewModel.todoFragmentUiState) { uiState ->
            Log.i("status", "showToday: ${uiState.showToday}")

            binding.apply {
                previousContainer.isGone = uiState.previousTodos.isEmpty()
                todayContainer.isGone = uiState.todayTodos.isEmpty()
                futureContainer.isGone = uiState.futureTodos.isEmpty()
                completedTodayContainer.isGone = uiState.completedTodayTodos.isEmpty()
            }

            binding.previousToggleContainer.setOnClickListener {
                toggle(binding.previousToggle, binding.previousRv, uiState.showPrevious) {
                    viewModel.saveShowPrevious(!uiState.showPrevious)
                }
            }

            binding.todayToggleContainer.setOnClickListener {
                toggle(binding.todayToggle, binding.todayRv, uiState.showToday) {
                    viewModel.saveShowToday(!uiState.showToday)
                }
            }

            binding.futureToggleContainer.setOnClickListener {
                toggle(binding.futureToggle, binding.futureRv, uiState.showFuture) {
                    viewModel.saveShowFuture(!uiState.showFuture)
                }
            }

            binding.completedTodayToggleContainer.setOnClickListener {
                toggle(
                    binding.completedTodayToggle,
                    binding.completedTodayRv,
                    uiState.showCompletedToday
                ) {
                    viewModel.saveShowCompletedToday(!uiState.showCompletedToday)
                }
            }

            previousAdapter.submitList(uiState.previousTodos)
            todayAdapter.submitList(uiState.todayTodos)
            futureAdapter.submitList(uiState.futureTodos)
            completedToday.submitList(uiState.completedTodayTodos)
        }
    }

    override fun onResume() {
        super.onResume()
        pulseObjetAnimator.start()
    }

    override fun onStop() {
        super.onStop()
        pulseObjetAnimator.cancel()
    }

    private fun toggle(
        imageView: AppCompatImageView,
        recyclerView: RecyclerView,
        show: Boolean,
        onClick: () -> Unit
    ) {
        recyclerView.isVisible = show
        imageView.rotation = if (!show) 180F else 0F
        onClick()
    }

    private fun setupAdapter() {
//        previousAdapter = TodosAdapter(dateUtil = dateUtil, onClickCheckBox = {
//            viewModel.updateTodoCompletion(it.todoId, it.isComplete)
//        }, onClickNavigation = {
//            navigateToDetailsActivity(it.todoId)
//        })
//        todayAdapter = TodosAdapter(dateUtil = dateUtil, onClickCheckBox = {
//            viewModel.updateTodoCompletion(it.todoId, it.isComplete)
//        }, onClickNavigation = {
//            navigateToDetailsActivity(it.todoId)
//        })
//        futureAdapter = TodosAdapter(dateUtil = dateUtil, onClickCheckBox = {
//            viewModel.updateTodoCompletion(it.todoId, it.isComplete)
//        }, onClickNavigation = {
//            navigateToDetailsActivity(it.todoId)
//        })
//        completedToday = TodosAdapter(dateUtil = dateUtil, onClickCheckBox = {
//            viewModel.updateTodoCompletion(it.todoId, it.isComplete)
//        }, onClickNavigation = {
//            navigateToDetailsActivity(it.todoId)
//        })

        previousAdapter = TodosAdapter(dateUtil = dateUtil, this)
        todayAdapter = TodosAdapter(dateUtil = dateUtil, this)
        futureAdapter = TodosAdapter(dateUtil = dateUtil, this)
        completedToday = TodosAdapter(dateUtil = dateUtil, this)
        binding.previousRv.adapter = previousAdapter
        binding.todayRv.adapter = todayAdapter
        binding.futureRv.adapter = futureAdapter
        binding.completedTodayRv.adapter = completedToday
    }

    private fun navigateToDetailsActivity(todoId: String) {
        viewModel.saveSelectedTodoId(todoId)
        val intent = Intent(requireContext(), DetailsActivity::class.java)
        startActivity(intent)
    }

    private fun pulseAnimation(
        imageView: ImageView,
        scaleX: Float = 1.5F,
        ScaleY: Float = 1.5F,
        alpha: Float = 0F,
        duration: Long = 1000,
        repeatCount: Int = ObjectAnimator.INFINITE,
        repeatMode: Int = ObjectAnimator.RESTART
    ) {
        pulseObjetAnimator = ObjectAnimator.ofPropertyValuesHolder(
            imageView,
            PropertyValuesHolder.ofFloat("scaleX", scaleX),
            PropertyValuesHolder.ofFloat("scaleY", ScaleY),
            PropertyValuesHolder.ofFloat("alpha", alpha)
        )
        pulseObjetAnimator.duration = duration
        pulseObjetAnimator.repeatCount = repeatCount
        pulseObjetAnimator.repeatMode = repeatMode
    }

    override fun navigate(todo: Todo) {
        viewModel.saveSelectedTodoId(todo.todoId)
        val intent = Intent(requireContext(), DetailsActivity::class.java)
        startActivity(intent)
    }

    override fun complete(todoId: String, isComplete: Boolean) {
        viewModel.updateTodoCompletion(todoId, isComplete)
    }
}