package com.example.chaika.tests

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chaika.R

class ProductAdapter(private val productOLDS: List<ProductOLD>, private var context: Context) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.products_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productOLDS[position]
        // Заполните поля в ViewHolder данными из объекта Product
        holder.title.text = product.name
        holder.quantity.text = product.quantity.toString()
        holder.soldCount.text = product.soldCount.toString()
        holder.price.text = "${product.price}р"
    }

    override fun getItemCount(): Int = productOLDS.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.product_title)
        val quantity: TextView = itemView.findViewById(R.id.product_quantity)
        val soldCount: TextView = itemView.findViewById(R.id.product_soldCount)
        val price: TextView = itemView.findViewById(R.id.product_price)
    }
}