// ProductTableAdapter.kt
package com.example.chaika.activities.productTableActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chaika.databinding.ItemProductTableBinding
import com.example.chaika.utils.expand
import com.example.chaika.utils.collapse

// ProductTableAdapter.kt
class ProductTableAdapter(
    private var products: List<ProductInTrip>,
    private val onSellClicked: (ProductInTrip) -> Unit,
    private val onBuyMoreClicked: (ProductInTrip) -> Unit,
    private val onDeleteClicked: (ProductInTrip) -> Unit
) : RecyclerView.Adapter<ProductTableAdapter.ProductTableViewHolder>() {

    inner class ProductTableViewHolder(private val binding: ItemProductTableBinding) : RecyclerView.ViewHolder(binding.root) {
        private var isExpanded: Boolean = false

        init {
            itemView.setOnClickListener {
                isExpanded = !isExpanded
                if (isExpanded) {
                    binding.expandableSection.expand()
                } else {
                    binding.expandableSection.collapse()
                }
            }

            binding.sellText.setOnClickListener {
                onSellClicked(products[adapterPosition])
            }

            binding.buyMoreText.setOnClickListener {
                onBuyMoreClicked(products[adapterPosition])
            }

            binding.deleteText.setOnClickListener {
                onDeleteClicked(products[adapterPosition])
            }
        }

        fun bind(item: ProductInTrip) {
            binding.textViewProductName.text = item.title
            binding.textViewProductPrice.text = "${item.price}р"
            binding.textViewProductAdded.text = "Добавлено: ${item.added}"
            binding.textViewProductBoughtCash.text = "Продано наличными: ${item.boughtCash}"
            binding.textViewProductBoughtCard.text = "Продано картой: ${item.boughtCard}"
        }
    }

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
}
