package com.miquel.tasquerviews

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.miquel.tasquerviews.databinding.FragmentAddBinding
import com.miquel.tasquerviews.repository.TaskItem
import com.miquel.tasquerviews.repository.TasquerApplication
import com.miquel.tasquerviews.repository.User
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    val args: HomeFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAddBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (activity is MainActivity.FragmentCallback) {
            (activity as MainActivity.FragmentCallback).updateTopAppBar(args.username)
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val date = binding.date
        date.setOnClickListener {
            showDatePicker(date)
        }
        val time: EditText = binding.time
        var duration: Int = 0
        var timeIsNumber: Boolean = true
        time.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                //nothing to do
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    duration = s.toString().toInt()
                    time.error = null
                    timeIsNumber = true
                } catch (e: NumberFormatException) {
                    time.error = R.string.time_error.toString()
                    timeIsNumber = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //nothing to do
            }
        })


        val button = binding.addTaskButton
        button.setOnClickListener {
            val description = binding.description.text.toString()
            val link = binding.link.text.toString()
            val date: Date = DateFormat.getDateInstance().parse(binding.date.text.toString())
            val isDone: Boolean = false
            val email: String = args.username?:""
            lifecycleScope.launch {
                val user: User? = TasquerApplication.database.userDao().getUserByEmail(email)
                if (user != null) {
                    val taskItem: TaskItem = TaskItem(
                        id = null,
                        date = date,
                        description = description,
                        isDone = isDone,
                        duration = duration,
                        userId = user.userId,
                        email = email,
                        link = link
                    )
                    TasquerApplication.database.taskDao().addTask(taskItem)

                } else {
                    Log.d("AddFragment", "User not found")
                }
            }

        }
    }

    private fun showDatePicker(date: EditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = Date(selection)
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = formatter.format(selectedDate)
            Log.d("SELECTED_DATE", formattedDate)
            date.setText(formattedDate)
        }
        datePicker.show(parentFragmentManager, "DatePicker")
    }
}



