// ProductTableAdapter.kt
package com.example.chaika.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chaika.databinding.ItemProductTableBinding
import com.example.chaika.services.ProductTableViewModel
import com.example.chaika.utils.ProductInTrip
import com.example.chaika.utils.collapse
import com.example.chaika.utils.dialogs.ReplenishAddProductDialog
import com.example.chaika.utils.dialogs.SellProductDialog
import com.example.chaika.utils.expand

class ProductTableAdapter(
    private val listener: ProductTableListener
) : RecyclerView.Adapter<ProductTableAdapter.ProductTableViewHolder>() {

    private var products: List<ProductInTrip> = listOf()

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
                listener.onSellProduct(products[adapterPosition])
            }

            binding.buyMoreText.setOnClickListener {
                listener.onReplenishProduct(products[adapterPosition])
            }

            binding.deleteText.setOnClickListener {
                listener.onDeleteProduct(products[adapterPosition])
            }
        }

        fun bind(item: ProductInTrip) {
            binding.textViewProductName.text = item.title
            binding.textViewProductPrice.text = "${item.price}р"
            val addedText = if (item.replenished > 0) {
                "Добавлено: ${item.added} + ${item.replenished}"
            } else {
                "Добавлено: ${item.added}"
            }
            binding.textViewProductAdded.text = addedText
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

    fun setProducts(newProducts: List<ProductInTrip>) {
        products = newProducts
        notifyDataSetChanged()
    }
}

// Интерфейс ProductTableListener
interface ProductTableListener {
    fun onSellProduct(product: ProductInTrip)
    fun onReplenishProduct(product: ProductInTrip)
    fun onDeleteProduct(product: ProductInTrip)
}
