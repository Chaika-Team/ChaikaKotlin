package com.example.chaika.activities.productListActivity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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
        binding.productsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.productsRecyclerView.adapter = adapter

        productViewModel.allProducts.observe(this, Observer { products ->
            Log.d("ProductListActivity", "All Products: $products")
            adapter.updateProducts(products)
        })

        // Инициализируем продукты и загружаем их
        productViewModel.initializeProducts()
        productViewModel.loadAllProducts()

        // Добавляем слушатель для поля поиска
        binding.searchProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable) {
                adapter.filter.filter(s.toString())
            }
        })
    }

    private fun showQuantityDialog(product: Product) {
        // Инфлейтим кастомный лейаут
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_enter_product_quantity, null)
        val editTextQuantity = dialogView.findViewById<EditText>(R.id.editTextQuantity)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .show()

        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnAdd).setOnClickListener {
            val quantity = editTextQuantity.text.toString().toIntOrNull() ?: 0
            if (quantity in 1..99) {
                CoroutineScope(Dispatchers.IO).launch {
                    (application as MyApp).actionRepository.addAction(
                        tripId = tripId,
                        productId = product.id,
                        operationId = 1, // Assuming 1 means "added"
                        count = quantity
                    )
                }
                alertDialog.dismiss()
            } else {
                Toast.makeText(this@ProductListActivity, "Введите корректное количество", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
