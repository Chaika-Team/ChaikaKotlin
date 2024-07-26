package com.example.chaika.activities.productTableActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chaika.MyApp
import com.example.chaika.activities.productListActivity.ProductListActivity
import com.example.chaika.databinding.ActivityProductTableBinding
import com.example.chaika.services.ProductTableViewModel
import com.example.chaika.services.ProductTableViewModelFactory
import com.example.chaika.services.ProductViewModel
import com.example.chaika.services.ProductViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ProductTableActivity.kt
class ProductTableActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductTableBinding
    private lateinit var adapter: ProductTableAdapter
    private val productTableViewModel: ProductTableViewModel by viewModels {
        ProductTableViewModelFactory(
            (application as MyApp).productRepository,
            (application as MyApp).actionRepository
        )
    }

    private var tripId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tripId = intent.getIntExtra("TRIP_ID", 0)
        Log.d("ProductTableActivity", "Trip ID: $tripId")

        adapter = ProductTableAdapter(
            emptyList(),
            onSellClicked = { product ->
                // Обработчик для "Продать"
            },
            onBuyMoreClicked = { product ->
                // Обработчик для "Добор"
            },
            onDeleteClicked = { product ->
                productTableViewModel.deleteActionsForProductInTrip(product.id, tripId)
            }
        )

        binding.productTableRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.productTableRecyclerView.adapter = adapter

        productTableViewModel.productsInTrip.observe(this, Observer { products ->
            Log.d("ProductTableActivity", "Products in trip: $products")
            adapter.updateProducts(products)
        })

        productTableViewModel.loadProductsByTrip(tripId)

        binding.addProductFab.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
            intent.putExtra("TRIP_ID", tripId)
            startActivityForResult(intent, REQUEST_CODE_ADD_PRODUCT)
        }
    }

    override fun onResume() {
        super.onResume()
        productTableViewModel.loadProductsByTrip(tripId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_PRODUCT && resultCode == Activity.RESULT_OK) {
            productTableViewModel.loadProductsByTrip(tripId)
        }
    }

    companion object {
        private const val REQUEST_CODE_ADD_PRODUCT = 1
    }
}
