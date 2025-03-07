package com.miquel.tasquerviews

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.miquel.tasquerviews.MainActivity.FragmentCallback
import com.miquel.tasquerviews.databinding.ActivityMainBinding
import kotlin.math.log
import androidx.core.content.edit

class MainActivity : AppCompatActivity(), FragmentCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var bottomAppBar: BottomNavigationView

    interface FragmentCallback {
        fun updateTopAppBar(title: String, subtitle: String)
        fun updateSharedPreferences(email: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        //enableEdgeToEdge()
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val loggedUserMail = preferences.getString("remembered_user_mail", "")

        val navHostFragment =supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        topAppBar = binding.topAppBar
        bottomAppBar = binding.bottomNavigationView

        if (loggedUserMail != "") {
            //Navigate to home
            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment(loggedUserMail!!)
            navController.navigate(action)
        }
            // Hide the TopAppBar and BottomAppBar when in the LoginFragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment -> {
                    Log.d("LoginFragment", "LoginFragment")
                    //supportActionBar?.hide()// Hide the toolbar
                    topAppBar.visibility = View.GONE
                    bottomAppBar.visibility = View.GONE
                }
                else -> {
                    Log.d("Else", "Else")
                    //supportActionBar?.show()//Show the toolbar
                    topAppBar.visibility = View.VISIBLE
                    bottomAppBar.visibility = View.VISIBLE
                }
            }
        }
        //binding.bottomAppBar.setupWithNavController(navController)



        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
        //    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        //    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        //    insets
        //}

    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    override fun updateTopAppBar(title: String, subtitle: String) {
        topAppBar.title = title
        topAppBar.subtitle = subtitle
    }

    override fun updateSharedPreferences(email: String) {
        val preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        preferences.edit() { putString("remembered_user_mail", email) }

    }
}