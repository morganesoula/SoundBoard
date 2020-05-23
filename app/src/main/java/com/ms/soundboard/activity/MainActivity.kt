package com.ms.soundboard.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ms.soundboard.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    /************************************************
        LIFECYCLE
     ************************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        val host: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = host.navController

        // Displays the name of the fragment in the toolbar
        appBarConfiguration = AppBarConfiguration(mainActivityBottomNavView.menu)
        setupActionBar(navController, appBarConfiguration)

        // Display bottom navigation
        setUpBottomNav(navController)
    }

    /************************************************
        METHODS
     ************************************************/

    private fun setupActionBar(
        navController: NavController,
        appBarConfiguration: AppBarConfiguration
    ) {
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setUpBottomNav(navController: NavController) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.mainActivityBottomNavView)

        bottomNavigationView?.setupWithNavController(navController)
    }
}
