package com.example.chaika.activities.productActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chaika.R
import com.example.chaika.dataBase.entities.Product

class TableProductAdapter : RecyclerView.Adapter<TableProductAdapter.ProductViewHolder>() {

    private var products: List<Product> = emptyList()

    fun setProducts(products: List<Product>) {
        this.products = products
        notifyDataSetChanged() // Уведомляет RecyclerView об изменении данных
    }

    fun getProductAt(position: Int): Product {
        return products[position]
    }


    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        var textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.table_product_item, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = products[position]
        holder.textViewTitle.text = currentProduct.title
        holder.textViewPrice.text = "${currentProduct.price} ₽"
    }

    override fun getItemCount(): Int = products.size
}
