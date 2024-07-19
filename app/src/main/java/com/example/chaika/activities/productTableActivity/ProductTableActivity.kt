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
import com.example.chaika.services.ProductViewModel
import com.example.chaika.services.ProductViewModelFactory

class ProductTableActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductTableBinding
    private lateinit var adapter: ProductTableAdapter
    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory((application as MyApp).productRepository)
    }

    private var tripId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tripId = intent.getIntExtra("TRIP_ID", 0)
        Log.d("ProductTableActivity", "Trip ID: $tripId")

        adapter = ProductTableAdapter(emptyList())
        binding.productTableRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.productTableRecyclerView.adapter = adapter

        productViewModel.productsInTrip.observe(this, Observer { products ->
            Log.d("ProductTableActivity", "Products in trip: $products")
            adapter.updateProducts(products)
        })

        productViewModel.loadProductsByTrip(tripId)

        binding.addProductFab.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
            intent.putExtra("TRIP_ID", tripId) // Передаем tripId
            startActivityForResult(intent, REQUEST_CODE_ADD_PRODUCT)
        }
    }

    override fun onResume() {
        super.onResume()
        productViewModel.loadProductsByTrip(tripId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_PRODUCT && resultCode == Activity.RESULT_OK) {
            productViewModel.loadProductsByTrip(tripId) // Перезагрузка продуктов после возврата из ProductListActivity
        }
    }

    companion object {
        private const val REQUEST_CODE_ADD_PRODUCT = 1
    }
}
