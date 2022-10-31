package com.example.to_dolistclone.feature.tasks.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.to_dolistclone.R
import com.example.to_dolistclone.databinding.ActivityTasksBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTasksBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHost.navController
        binding.toolbar.navigationIcon = null
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
        setupBottomNavBar()
        setupDrawer()
//        navController.addOnDestinationChangedListener { controller, destination, arguments ->
//            if (destination.id == R.id.calendarFragment) {
//                supportActionBar?.setDisplayHomeAsUpEnabled(false)
//            }
//        }


    }

//    private fun setupBottomNavBar() {
//        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
//        navController = navHost.navController
//        binding.bottomNavBar.setupWithNavController(navController)
//    }

    private fun setupBottomNavBar() {
        binding.bottomNavBar.setupWithNavController(navController)
    }

    private fun setupDrawer(){
        val drawer = binding.drawerLayout
        val builder = AppBarConfiguration.Builder(navController.graph)
        builder.setOpenableLayout(drawer)
        val appBarConfiguration = builder.build()
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}