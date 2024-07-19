package com.example.chaika.activities.productListActivity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chaika.MyApp
import com.example.chaika.R
import com.example.chaika.dataBase.entities.Product
import com.example.chaika.databinding.ActivityProductListBinding
import com.example.chaika.services.ProductViewModel
import com.example.chaika.services.ProductViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private lateinit var adapter: ProductListAdapter
    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory((application as MyApp).productRepository)
    }

    private var tripId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tripId = intent.getIntExtra("TRIP_ID", 0) // Получаем tripId из intent

        adapter = ProductListAdapter(emptyList()) { product ->
            showQuantityDialog(product)
        }
        binding.productListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.productListRecyclerView.adapter = adapter

        productViewModel.allProducts.observe(this, Observer { products ->
            Log.d("ProductListActivity", "All Products: $products")
            adapter.updateProducts(products)
        })

        // Инициализируем продукты и загружаем их
        productViewModel.initializeProducts()
        productViewModel.loadAllProducts()
    }

    private fun showQuantityDialog(product: Product) {
        // Создаем билдер для диалога
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Введите количество")

        // Инфлейтим кастомный лейаут
        val view = layoutInflater.inflate(R.layout.dialog_enter_product_quantity, null)
        val editTextQuantity = view.findViewById<EditText>(R.id.editTextQuantity)

        builder.setView(view)

        builder.setPositiveButton("Подтвердить") { dialog, _ ->
            // Получаем количество из поля ввода
            val quantity = editTextQuantity.text.toString().toIntOrNull() ?: 1
            // Используем CoroutineScope для выполнения операции вставки
            CoroutineScope(Dispatchers.IO).launch {
                (application as MyApp).actionRepository.addAction(
                    tripId = tripId,
                    productId = product.id,
                    operationId = 1, // Assuming 1 means "added"
                    count = quantity
                )
            }

            dialog.dismiss()
        }

        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        // Показываем диалог
        builder.create().show()
    }
}
