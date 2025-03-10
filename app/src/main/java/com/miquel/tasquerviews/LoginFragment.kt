package com.miquel.tasquerviews

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.miquel.tasquerviews.databinding.FragmentLoginBinding
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.miquel.tasquerviews.repository.TasksDatabase
import com.miquel.tasquerviews.repository.TasquerApplication
import com.miquel.tasquerviews.repository.User
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private var user :  User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val placeholder = R.drawable.tasky_unfocussed
        val imageUrl = "https://drive.google.com/uc?export=view&id=1JTZh2pXBot1ksYpFNkaWuLEivfRFK8qn"

        Glide.with(this)
            .load(imageUrl)
            .placeholder(placeholder)
            .error(placeholder)
            .into(binding.imageView)

        binding.login.setOnClickListener {
            val (email, password) = getBothFromTextEdit(binding)

            if (isValidEmail(email) && isValidPassword(password)){
                lifecycleScope.launch {
                    user= TasquerApplication.database.userDao().getUserByEmail(email)
                    if (user != null && user!!.password == password) { //login ok
                        if (activity is MainActivity.FragmentCallback) {
                            if(binding.rememberCheck.isChecked){
                                (activity as MainActivity.FragmentCallback).updateSharedPreferences(email)
                            }
                            (activity as MainActivity.FragmentCallback).manageMediaPlayer(user!!.songPosition)
                        }
                        findNavController().navigate(R.id.homeFragment2,HomeFragmentArgs(email).toBundle())
                    }
                    if (user == null) {//create user
                        user = User(email = email, password = password, songPosition = 0)
                        TasquerApplication.database.userDao().addUser(user!!)
                        if (activity is MainActivity.FragmentCallback) {
                            if(binding.rememberCheck.isChecked){
                                (activity as MainActivity.FragmentCallback).updateSharedPreferences(email)
                            }
                            (activity as MainActivity.FragmentCallback).updateTopAppBar(email)
                            (activity as MainActivity.FragmentCallback).manageMediaPlayer(0)
                        }
                        findNavController().navigate( //guide to new user to add the first task
                            LoginFragmentDirections.actionLoginFragment2ToAddFragment2(email)
                        )

                    }
                    if (user != null && user!!.password != password) {// wrong password
                        binding.password.error = getString(R.string.pass_error)
                    }
                }
            }
        }
        return binding.root
    }
    private fun getBothFromTextEdit(binding: FragmentLoginBinding): Pair<String, String> {
        val email = binding.username.text.toString()
        val password = binding.password.text.toString()
        if (!isValidEmail(email)) {
            binding.username.error = getString(R.string.mail_error)
        }
        if (!isValidPassword(password)) {
            binding.password.error = getString(R.string.pass_error)
        }
        return Pair(email, password)
    }
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }
    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = "^[^\\s]{3,}$"
        return password.matches(passwordRegex.toRegex())
    }



}

