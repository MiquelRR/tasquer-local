package com.miquel.tasquerviews

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.miquel.tasquerviews.databinding.CardBinding
import com.miquel.tasquerviews.repository.TaskItem
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class MyAdapter (
    private var taskList: List<TaskItem>,
    private val onUpdateTask: (TaskItem) -> Unit
):RecyclerView.Adapter<MyAdapter.CardViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = CardBinding.inflate(layoutInflater, parent, false)
        return CardViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: CardViewHolder,
        position: Int
    ) {
        val task = taskList[position]
        holder.bind(task)
        holder.binding.doneRadioButton.setOnClickListener {
            task.isDone = holder.binding.doneRadioButton.isChecked
            onUpdateTask(task)
        }
        holder.binding.taskLinkButton.setOnClickListener {
            val linkString = task.link
            if (linkString != null && linkString.isNotBlank()) {
                try {
                    val link: Uri = linkString.toUri()
                    holder.binding.taskLinkButton.context.startActivity(Intent(Intent.ACTION_VIEW, link))
                } catch (e: Exception) {
                }
            }
        }
    }

    override fun getItemCount(): Int = taskList.size

    class CardViewHolder(internal val binding: CardBinding):RecyclerView.ViewHolder(binding.root){
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(task: TaskItem) {
            binding.taskDescriptionTextView.text = task.description
            val formatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            var formattedDate:String ="Sene data"
            if (task.date != null){
                formattedDate = formatter.format(task.date as Date)
            }

            binding.taskDateTextView.text = formattedDate

            binding.taskDurationTextView.text = task.duration.toString()
            binding.doneRadioButton.isChecked = task.isDone ?: false
        }

    }
}