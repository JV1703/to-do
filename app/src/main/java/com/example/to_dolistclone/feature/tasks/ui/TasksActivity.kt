package com.example.to_dolistclone.feature.tasks.ui

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.to_dolistclone.R
import com.example.to_dolistclone.databinding.ActivityTasksBinding
import com.example.to_dolistclone.feature.calendar.CalendarFragment
import com.example.to_dolistclone.feature.profile.ui.ProfileFragment
import com.example.to_dolistclone.feature.tasks.adapter.HomeViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TasksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTasksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupDrawer()

        val fragmentLists = listOf(TasksFragment(), CalendarFragment(), ProfileFragment())
        binding.viewPager.adapter = HomeViewPagerAdapter(fragmentLists, this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, index ->
            tab.icon = when (index) {
                0 -> {
                    AppCompatResources.getDrawable(this, R.drawable.ic_tasks)
                }
                1 -> {
                    AppCompatResources.getDrawable(this, R.drawable.ic_calendar)
                }
                2 -> {
                    AppCompatResources.getDrawable(this, R.drawable.ic_profile)
                }
                else -> {
                    throw Resources.NotFoundException("Position not found")
                }
            }

            tab.text = when (index) {
                0 -> {
                    "Tasks"
                }
                1 -> {
                    "Calendar"
                }
                2 -> {
                    "Profile"
                }
                else -> {
                    throw Resources.NotFoundException("Position not found")
                }
            }
        }.attach()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupDrawer() {
        val drawerToggle = setupDrawerToggle()
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()
        binding.drawerLayout.addDrawerListener(drawerToggle)
    }

    private fun setupDrawerToggle() =
        ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open,
            R.string.close
        )
}