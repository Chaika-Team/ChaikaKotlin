package com.example.chaika.activities.productTableActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chaika.data.room.entities.old.Action
import com.example.chaika.databinding.ItemActionBinding

class ActionAdapter(
    private var actions: List<Action>
) : RecyclerView.Adapter<ActionAdapter.ActionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
        val binding = ItemActionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        holder.bind(actions[position])
    }

    override fun getItemCount(): Int = actions.size

    fun updateActions(newActions: List<Action>) {
        actions = newActions
        notifyDataSetChanged()
    }

    inner class ActionViewHolder(private val binding: ItemActionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(action: Action) {
            // Здесь вы можете настроить привязку данных к элементам интерфейса.
            // Например:
            // binding.textViewActionTime.text = action.time
            // binding.textViewActionProductId.text = action.productId.toString()
            // и т.д.
        }
    }
}
