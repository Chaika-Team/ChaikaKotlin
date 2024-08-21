// ProductTableActivity.kt
package com.example.chaika.activities.productTableActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chaika.activities.productListActivity.ProductListActivity
import com.example.chaika.adapters.ProductTableAdapter
import com.example.chaika.adapters.ProductTableListenerImpl
import com.example.chaika.databinding.ActivityProductTableBinding
import com.example.chaika.services.ProductTableViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductTableActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductTableBinding
    private lateinit var productTableAdapter: ProductTableAdapter
    private val productTableViewModel: ProductTableViewModel by viewModels()

    private var tripId: Int = 0

    // Регистрация ActivityResultLauncher
    private val addProductLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            productTableViewModel.loadProductsByTrip(tripId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tripId = intent.getIntExtra("TRIP_ID", 0)

        // Инициализация адаптера с использованием слушателя
        productTableAdapter = ProductTableAdapter(ProductTableListenerImpl(this, productTableViewModel, tripId))

        binding.productTableRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.productTableRecyclerView.adapter = productTableAdapter

        productTableViewModel.productsInTrip.observe(this) { products ->
            productTableAdapter.setProducts(products)
        }

        productTableViewModel.loadProductsByTrip(tripId)

        binding.addProductFab.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
            intent.putExtra("TRIP_ID", tripId)
            addProductLauncher.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        productTableViewModel.loadProductsByTrip(tripId)
    }
}
