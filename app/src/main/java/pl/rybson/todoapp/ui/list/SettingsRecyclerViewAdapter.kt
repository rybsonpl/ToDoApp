package pl.rybson.todoapp.ui.list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import pl.rybson.todoapp.R
import pl.rybson.todoapp.databinding.ItemSettingsBinding

class SettingsRecyclerViewAdapter(
    private var settingsList: ArrayList<Pair<Int, String>>,
    private val listener: OnSettingsItemClickListener
    ) : RecyclerView.Adapter<SettingsRecyclerViewAdapter.SettingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val binding = ItemSettingsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettingsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) = holder.bind(settingsList[position])

    override fun getItemCount(): Int = settingsList.size

    inner class SettingsViewHolder(private val binding: ItemSettingsBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
       init {
           binding.root.setOnClickListener(this)
       }

        fun bind(item: Pair<Int, String>) {
            binding.apply {
                ivIcon.load(item.first)
                tvTitle.text = item.second
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) listener.onSettingsClick(adapterPosition)
        }
    }

    interface OnSettingsItemClickListener {
        fun onSettingsClick(position: Int)
    }
}