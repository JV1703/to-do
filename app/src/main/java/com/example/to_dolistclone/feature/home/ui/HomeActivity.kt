package com.example.to_dolistclone.feature.home.ui

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.example.to_dolistclone.R
import com.example.to_dolistclone.core.utils.ui.collectLatestLifecycleFlow
import com.example.to_dolistclone.core.utils.ui.makeToast
import com.example.to_dolistclone.databinding.ActivityHomeBinding
import com.example.to_dolistclone.feature.calendar.CalendarFragment
import com.example.to_dolistclone.feature.common.dialog.DialogsManager
import com.example.to_dolistclone.feature.home.adapter.DrawerCategoryAdapter
import com.example.to_dolistclone.feature.home.adapter.HomeViewPagerAdapter
import com.example.to_dolistclone.feature.home.viewmodel.HomeViewModel
import com.example.to_dolistclone.feature.profile.ui.ProfileFragment
import com.example.to_dolistclone.feature.todo.ui.TodoFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var dialogsManager: DialogsManager

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityHomeBinding

    private lateinit var categoryAdapter: DrawerCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupTabLayout()
        setupDrawer()

        binding.drawer.addCategory.setOnClickListener {
            dialogsManager.createAddCategoryDialogFragment{ categoryName ->
                if(categoryName.isNotEmpty()){
                    viewModel.insertTodoCategory(categoryName)
                }else{
                    makeToast("Please provide name for category")
                }
            }
        }

        collectLatestLifecycleFlow(viewModel.drawerUiState) { uiState ->
            categoryDrawerToggle(uiState.hide)
            setupDrawerCategoryRv(uiState.categories)
        }

        binding.drawer.categoryHideToggle.setOnClickListener {
            viewModel.drawerRvToggle()
        }
    }

    private fun setupToolbar() {
        this.setSupportActionBar(binding.toolbar)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupDrawer() {
        val drawerToggle = setupDrawerToggle()
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()
        binding.drawerLayout.addDrawerListener(drawerToggle)
    }

    private fun setupDrawerToggle() = ActionBarDrawerToggle(
        this, binding.drawerLayout, binding.toolbar, R.string.open, R.string.close
    )

    private fun setupTabLayout() {
        val fragmentLists = listOf(TodoFragment(), CalendarFragment(), ProfileFragment())
        binding.viewPager.adapter =
            HomeViewPagerAdapter(fragmentLists, supportFragmentManager, lifecycle)
        binding.viewPager.isUserInputEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, index ->

            when (index) {
                0 -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_tasks)
                    tab.text = "Tasks"
                }
                1 -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_calendar)
                    tab.text = "Calendar"
                }
                2 -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_profile)
                    tab.text = "Profile"
                }
                else -> {
                    throw Resources.NotFoundException("Position not found")
                }
            }
        }.attach()
    }

    private fun setupDrawerCategoryRv(categories: List<String>) {
        categoryAdapter = DrawerCategoryAdapter()
        val drawer = binding.drawer
        drawer.apply {
            drawerRv.adapter = categoryAdapter
            categoryAdapter.submitList(categories)
        }
    }

    private fun categoryDrawerToggle(hide: Boolean) {
        binding.drawer.drawerRv.isGone = hide
        binding.drawer.addCategory.isGone = hide
        if (!hide) {
            binding.drawer.categoryHideToggle.rotation = 180F
        } else {
            binding.drawer.categoryHideToggle.rotation = 0F
        }
    }
}