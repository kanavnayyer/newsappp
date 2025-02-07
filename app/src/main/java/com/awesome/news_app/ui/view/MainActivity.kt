package com.awesome.news_app.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.awesome.news_app.R
import com.awesome.news_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.newsFragment -> {
                    navController.navigate(
                        R.id.newsFragment, null, getNavOptions(R.id.newsFragment)
                    )
                    true
                }
                R.id.savedFragment -> {
                    navController.navigate(
                        R.id.savedFragment, null, getNavOptions(R.id.savedFragment)
                    )
                    true
                }
                else -> false
            }
        }
    }

    private fun getNavOptions(destinationId: Int): NavOptions {
        return NavOptions.Builder()
            .setPopUpTo(destinationId, true)
            .setLaunchSingleTop(true)
            .build()
    }
}
