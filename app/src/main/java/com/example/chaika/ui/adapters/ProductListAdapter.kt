package com.example.chaika.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chaika.data.room.entities.old.Product
import com.example.chaika.databinding.ListProductItemBinding

class ProductListAdapter(
    private var products: List<Product>,
    private val onProductSelected: (Product) -> Unit
) : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

    fun setProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ListProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        Log.d("ProductListAdapter", "Binding product: ${product.title} with ID: ${product.id}")
        holder.bind(product, onProductSelected)
    }

    override fun getItemCount() = products.size

    class ProductViewHolder(private val binding: ListProductItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, onProductSelected: (Product) -> Unit) {
            binding.productTitleTextView.text = product.title
            binding.productPriceTextView.text = product.price.toString()

            binding.root.setOnClickListener {
                Log.d("ProductViewHolder", "Product clicked: ${product.title} with ID: ${product.id}")
                onProductSelected(product)
            }
        }
    }
}
