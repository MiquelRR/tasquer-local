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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    val args: HomeFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        if (activity is MainActivity.FragmentCallback) {
            Log.d("AddFragment", "update top app bar 1")
            (activity as MainActivity.FragmentCallback).updateTopAppBar(args.username)
        } else  {
            Log.d("AddFragment", "activity is null 1")
        }
        // Inflate the layout for this fragment
        return binding.root
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
            Log.d("AddFragment", "pressed")
            val desField= binding.description
            val description = desField.text.toString()
            val linkField = binding.link
            val link = linkField.text.toString()
            val dateField = binding.date
            var date: Date? = null
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            try {
                date = dateFormat.parse(dateField.text.toString())
            } catch (e: Exception) {
                Log.d("AddFragment", "Error parsing date: ${e.message}")
                date = null
            }
            val isDone: Boolean = false
            val email: String = args.username?:""
            Log.d("AddFragment", "email: $email -> $description ${date.toString()}")

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
                    Log.d("AddFragment", "Task added")
                    if (activity is MainActivity.FragmentCallback) {
                        Log.d("AddFragment", "update top app bar 2")
                        (activity as MainActivity.FragmentCallback).updateTopAppBar(email)
                    } else  {
                        Log.d("AddFragment", "activity is null 2 ")
                    }
                } else {
                    Log.d("AddFragment", "User not found")
                }
                time.text.clear()
                dateField.text.clear()
                linkField.text.clear()
                time.error = null
                desField.text.clear()


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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Release the binding in onDestroyView
    }


}



