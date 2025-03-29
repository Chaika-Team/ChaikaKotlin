package com.example.chaika.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chaika.R
import com.example.chaika.databinding.ItemProductBinding
import com.example.chaika.domain.models.ProductInfoDomain
import java.io.File

class ProductAdapter :
    PagingDataAdapter<ProductInfoDomain, ProductAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        // Получаем элемент из PagingDataAdapter (он может быть null)
        getItem(position)?.let { product ->
            holder.bind(product)
        }
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductInfoDomain) {
            binding.tvProductName.text = product.name
            binding.tvProductDescription.text = product.description
            binding.tvProductPrice.text =
                binding.tvProductPrice.context.getString(R.string.product_price, product.price)
            Glide.with(binding.ivProduct.context)
                .load(File(product.image))
                .placeholder(R.drawable.image_not_found_icon)
                .into(binding.ivProduct)
        }

    }

    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<ProductInfoDomain>() {
            override fun areItemsTheSame(
                oldItem: ProductInfoDomain,
                newItem: ProductInfoDomain
            ): Boolean {
                // Считаем, что товары идентичны, если их id совпадают
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ProductInfoDomain,
                newItem: ProductInfoDomain
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
