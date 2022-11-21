package com.example.to_dolistclone.feature.home.ui

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import com.example.to_dolistclone.R
import com.example.to_dolistclone.databinding.FragmentHomeBinding
import com.example.to_dolistclone.feature.BaseFragment
import com.example.to_dolistclone.feature.calendar.CalendarFragment
import com.example.to_dolistclone.feature.common.dialog.DialogsManager
import com.example.to_dolistclone.feature.home.adapter.HomeViewPagerAdapter
import com.example.to_dolistclone.feature.profile.ui.ProfileFragment
import com.example.to_dolistclone.feature.todo.ui.TodoFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    @Inject
    lateinit var dialogsManager: DialogsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupTabLayout()
        setupDrawer()
    }

    private fun setupToolbar() {
        val homeActivity = requireActivity() as HomeActivity
        homeActivity.setSupportActionBar(binding.toolbar)
        homeActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupDrawer() {
        val drawerToggle = setupDrawerToggle()
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()
        binding.drawerLayout.addDrawerListener(drawerToggle)
    }

    private fun setupDrawerToggle() =
        ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            binding.toolbar,
            R.string.open,
            R.string.close
        )

    private fun setupTabLayout() {
        val fragmentLists = listOf(TodoFragment(), CalendarFragment(), ProfileFragment())
        binding.viewPager.adapter =
            HomeViewPagerAdapter(fragmentLists, childFragmentManager, viewLifecycleOwner.lifecycle)
        binding.viewPager.isUserInputEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, index ->
            tab.icon = when (index) {
                0 -> {
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_tasks)
                }
                1 -> {
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_calendar)
                }
                2 -> {
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_profile)
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.adapter = null
    }
}