package com.miquel.tasquerviews

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.miquel.tasquerviews.databinding.FragmentHomeBinding
import com.miquel.tasquerviews.repository.TaskItem
import com.miquel.tasquerviews.repository.TasksDatabase
import com.miquel.tasquerviews.repository.TasquerApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.getValue


class HomeFragment : Fragment() {
    private lateinit var taskItemsList: List<TaskItem>
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    val args: HomeFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("HomeFragment", "onCreate")
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        Log.d("HomeFragment", "onCreateView")
        // Inflate the layout for this fragment
        //binding= FragmentHomeBinding.inflate(inflater, container, false)
        val username = args.username
        if (activity is MainActivity.FragmentCallback) {
            (activity as MainActivity.FragmentCallback).updateTopAppBar(username)
        }
        loadTasks(username, binding)

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HomeFragment", "onViewCreated")


    }
    private fun loadTasks(username: String, binding: FragmentHomeBinding) {
        CoroutineScope(Dispatchers.IO).launch{
            taskItemsList = TasquerApplication.database.taskDao().getTaskByUserEmail(username)
            binding.myRecyclerView.layoutManager= LinearLayoutManager(requireContext())
            binding.myRecyclerView.adapter= MyAdapter(taskItemsList){
                task ->
                CoroutineScope(Dispatchers.IO).launch{
                    TasquerApplication.database.taskDao().updateTask(task)
                }
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}