package pl.rybson.todoapp.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.rybson.todoapp.R
import pl.rybson.todoapp.data.models.Priority
import pl.rybson.todoapp.data.models.TaskModel
import pl.rybson.todoapp.databinding.ItemTaskBinding

class TasksRecyclerViewAdapter(private val listener: OnTaskClickListener) :
    ListAdapter<TaskModel, TasksRecyclerViewAdapter.TasksViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class TasksViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(task: TaskModel) {
            binding.apply {
                tvTitle.text = task.title

                if (task.description!!.isNotBlank()) {
                    tvDescription.text = task.description
                } else tvDescription.visibility = View.GONE

                when (task.priority) {
                    Priority.HIGH -> ivPriority.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.red))
                    Priority.MEDIUM -> ivPriority.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.yellow))
                    Priority.LOW -> ivPriority.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.green))
                }
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) listener.onTaskClick(adapterPosition)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TaskModel>() {
        override fun areItemsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean = oldItem == newItem
    }

    interface OnTaskClickListener {
        fun onTaskClick(position: Int)
    }
}