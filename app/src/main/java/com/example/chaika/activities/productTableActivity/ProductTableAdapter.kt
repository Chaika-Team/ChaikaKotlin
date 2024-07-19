package com.example.chaika.activities.productTableActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chaika.databinding.ItemProductTableBinding

class ProductTableAdapter(
    private var products: List<ProductInTrip>
) : RecyclerView.Adapter<ProductTableAdapter.ProductTableViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductTableViewHolder {
        val binding = ItemProductTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductTableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductTableViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<ProductInTrip>) {
        products = newProducts
        notifyDataSetChanged()
    }

    inner class ProductTableViewHolder(private val binding: ItemProductTableBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductInTrip) {
            binding.textViewProductName.text = item.title
            binding.textViewProductAdded.text = "Added: ${item.added}"
            binding.textViewProductBoughtCash.text = "Bought (Cash): ${item.boughtCash}"
            binding.textViewProductBoughtCard.text = "Bought (Card): ${item.boughtCard}"
        }
    }
}
