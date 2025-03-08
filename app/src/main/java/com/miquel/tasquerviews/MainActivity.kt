package com.miquel.tasquerviews

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationBarView
import com.miquel.tasquerviews.repository.TasquerApplication
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), FragmentCallback, NavigationBarView. OnItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var bottomAppBar: BottomNavigationView
    private var loggededUserMail: String = ""

    interface FragmentCallback {
        fun updateTopAppBar(title: String?)
        fun updateSharedPreferences(email: String)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val loggedUserMail = preferences.getString("remembered_user_mail", "")
        Log.d("Preferences", "onCreate read $loggedUserMail")

        val navHostFragment =supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        topAppBar = binding.topAppBar
        bottomAppBar = binding.bottomNavigationView
        updateTopAppBar("JonDoe")
        bottomAppBar.setOnItemSelectedListener(this)
        //setSupportActionBar()
        //setupActionBarWithNavController(navController)

        if (loggedUserMail != "") { //User exists and remembered->Navigate to home
            val action = LoginFragmentDirections.actionLoginFragment2ToHomeFragment2(loggedUserMail!!)
            //vall action = LoginFragmentDirections.actionLoginFragmentToAddFragment(loggedUserMail!!)
            Log.d("NAV", ">${action.toString()}")
            navController.currentDestination?.id =R.id.homeFragment2
            navController.navigate(action)
            //navController.navigate(R.id.homeFragment,HomeFragmentArgs(loggedUserMail).toBundle())
            Log.d("NAV", "DONEDONE")
        }
            // Hide the TopAppBar and BottomAppBar when in the LoginFragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("NAV", "******${destination.toString()}")
            when (destination.label) {
                "fragment_login" -> {
                    Log.d("NAV", "LoginFragment")
                    //supportActionBar?.hide()// Hide the toolbar
                    topAppBar.visibility = View.GONE
                    bottomAppBar.visibility = View.GONE
                }
                else -> {

                    Log.d("NAV", "-------------${destination.toString()}")
                    //supportActionBar?.show()//Show the toolbar
                    topAppBar.visibility = View.VISIBLE
                    Log.d("NAV", "1111111111")
                    bottomAppBar.visibility = View.VISIBLE
                    Log.d("NAV", "2222222222")
                }
            }
        }
        //bottomAppBar.setupWithNavController(navController)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        Log.d("NAV", "onSupportNavigateUp")
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    override fun updateTopAppBar(title: String?) {
        val showTitle = if (title=="" || title==null) "none passed" else title

        topAppBar.title = showTitle
        lifecycleScope.launch{
            val tasksForUser:Int= TasquerApplication.database.taskDao().getAmountTaskOfUserEmail(showTitle)
            Log.d("LoginFragment", "updateTopAppBar $tasksForUser")
            val prompt = if (tasksForUser==0) getString(R.string.zero_task_message) else getString(R.string.tasks_for_user,tasksForUser)
            topAppBar.subtitle = prompt
        }
    }

    override fun updateSharedPreferences(email: String) {
        val preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        preferences.edit() { putString("remembered_user_mail", email) }
        Log.d("Preferences", "updateSharedPreferences $email")

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("BottomBar", "onNavigationItemSelected ${item.itemId}")
       when(item.itemId){
           R.id.homeFragment2 -> {
               Log.d("BottomBar", "HomeFragment")
                if(navController.currentDestination?.id != R.id.homeFragment2) {
                    Log.d("BottomBar", ">HomeFragment")
                    val action = AddFragmentDirections.actionAddFragment2ToHomeFragment2(topAppBar.title.toString())

                    //navController.navigate(action)
                    navController.navigate(R.id.homeFragment2, HomeFragmentArgs(topAppBar.title.toString()).toBundle())
                    return true
                }
                return true
           }
           R.id.addFragment2 -> {
               Log.d("BottomBar", "AddFragment")
               if(navController.currentDestination?.id != R.id.addFragment2) {
                    Log.d("BottomBar", ">AddFragment")
                    val action = LoginFragmentDirections.actionLoginFragment2ToAddFragment2(topAppBar.title.toString())
                    //navController.navigate(action)
                    navController.navigate(R.id.addFragment2, AddFragmentArgs(topAppBar.title.toString()).toBundle())
                    return true
               }
               return true
           }
           R.id.loginFragment2 -> { //We dont care if we are in login, we go to login
               if(navController.currentDestination?.id != R.id.loginFragment2) {
                   Log.d("BottomBar", "LoginFragment")
                   updateTopAppBar("")
                   loggededUserMail = ""
                   val preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                   preferences.edit() { putString("remembered_user_mail", "") }
                   Log.d("Preferences", "updateSharedPreferences EMPTY")
                   val action = HomeFragmentDirections.actionHomeFragment2ToLoginFragment2()
                   //navController.navigate(action)
                   navController.navigate(R.id.loginFragment2)
               }
               Log.d("BottomBar", "LoginFragment->LoginFragment")
               return true
           }
       }
       return false
    }

}

