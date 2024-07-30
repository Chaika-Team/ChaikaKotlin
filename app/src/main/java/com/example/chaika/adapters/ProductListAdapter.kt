package com.example.chaika.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.chaika.dataBase.entities.Product
import com.example.chaika.databinding.ListProductItemBinding
import com.example.chaika.utils.GenericFilter
import java.util.Locale

class ProductListAdapter(
    private var products: List<Product>,
    private val onProductSelected: (Product) -> Unit
) : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>(), Filterable {

    private var allProducts: List<Product> = ArrayList(products)

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        allProducts = ArrayList(newProducts)
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

    override fun getFilter(): Filter {
        return GenericFilter(
            originalList = allProducts,
            filterCriteria = { product, query -> product.title.lowercase(Locale.getDefault()).contains(query) },
            onFiltered = { filteredList ->
                products = filteredList
                notifyDataSetChanged()
            },
            clazz = Product::class.java
        )
    }

    class ProductViewHolder(private val binding: ListProductItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, onProductSelected: (Product) -> Unit) {
            binding.productTitleTextView.text = product.title
            binding.productPriceTextView.text = product.price.toString()

            itemView.setOnClickListener {
                Log.d("ProductViewHolder", "Product clicked: ${product.title} with ID: ${product.id}")
                onProductSelected(product)
            }
        }
    }
}