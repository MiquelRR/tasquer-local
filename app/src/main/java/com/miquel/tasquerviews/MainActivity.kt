package com.miquel.tasquerviews

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PersistableBundle
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
    private var mediaPlayer: MediaPlayer? = null
    private var lastMail:String =""

    interface FragmentCallback {
        fun updateTopAppBar(title: String?)
        fun updateSharedPreferences(email: String)
        fun manageMediaPlayer(position: Int)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.es_una_lata)
            mediaPlayer?.isLooping = true
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val loggedUserMail = preferences.getString("remembered_user_mail", "")

        val navHostFragment =supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        topAppBar = binding.topAppBar
        bottomAppBar = binding.bottomNavigationView
        bottomAppBar.setOnItemSelectedListener(this)

        if (loggedUserMail != "") { //User exists and remembered->Navigate to home
            lifecycleScope.launch {
                val user = TasquerApplication.database.userDao().getUserByEmail(loggedUserMail!!)
                if (user != null) {
                    updateTopAppBar(loggedUserMail)
                    navController.navigate(R.id.homeFragment2, HomeFragmentArgs(loggedUserMail).toBundle())
                    manageMediaPlayer(user.songPosition)
                }
            }
            navController.navigate(R.id.homeFragment2,HomeFragmentArgs(loggedUserMail!!).toBundle())
        }
            // Hide the TopAppBar and BottomAppBar when in the LoginFragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.label) {
                "fragment_login" -> {
                    topAppBar.visibility = View.GONE
                    bottomAppBar.visibility = View.GONE
                }
                else -> {
                    topAppBar.visibility = View.VISIBLE
                    bottomAppBar.visibility = View.VISIBLE
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    override fun updateTopAppBar(title: String?) {
        val showTitle = if (title=="" || title==null) "none passed" else title
        lastMail = if (title=="" || title==null) lastMail else title

        topAppBar.title = showTitle
        lifecycleScope.launch{
            val tasksForUser:Int= TasquerApplication.database.taskDao().getAmountTaskOfUserEmail(showTitle)
            val prompt = if (tasksForUser==0) getString(R.string.zero_task_message) else getString(R.string.tasks_for_user,tasksForUser)
            topAppBar.subtitle = prompt
        }
    }

    override fun updateSharedPreferences(email: String) {
        val preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        preferences.edit() { putString("remembered_user_mail", email) }
    }

    override fun manageMediaPlayer(position: Int) {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
            }
            if(position >= 0 ) {
                mediaPlayer!!.seekTo(position)
                mediaPlayer!!.start()
            } else {
                lifecycleScope.launch {
                    Log.d("MUSIC", "manageMediaPlayer: $lastMail -> $position")
                    TasquerApplication.database.userDao().updateSongPositionToUserEmail(lastMail, mediaPlayer!!.currentPosition)
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
           R.id.homeFragment2 -> {

                if(navController.currentDestination?.id != R.id.homeFragment2) {

                    navController.navigate(R.id.homeFragment2, HomeFragmentArgs(topAppBar.title.toString()).toBundle())
                    return true
                }
                return true
           }
           R.id.addFragment2 -> {
               if(navController.currentDestination?.id != R.id.addFragment2) {
                    navController.navigate(R.id.addFragment2, AddFragmentArgs(topAppBar.title.toString()).toBundle())
                    return true
               }
               return true
           }
           R.id.loginFragment2 -> { //We dont care if we are in login, we go to login
               if(navController.currentDestination?.id != R.id.loginFragment2) {
                   updateTopAppBar("")
                   val preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                   preferences.edit() { putString("remembered_user_mail", "") }
                   navController.navigate(R.id.loginFragment2)
                   manageMediaPlayer(-1)
                   Log.d("MainActivity", "navigate to home music stop?")
               }
               return true
           }
       }
       return false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            manageMediaPlayer(-1)
            mediaPlayer!!.release()
            mediaPlayer = null
        }

    }

}

