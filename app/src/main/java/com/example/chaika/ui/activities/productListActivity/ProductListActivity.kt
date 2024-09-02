package com.example.chaika.ui.activities.productListActivity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chaika.ui.adapters.ProductListAdapter
import com.example.chaika.data.room.entities.old.Product
import com.example.chaika.databinding.ActivityProductListBinding
import com.example.chaika.ui.view_models.ProductListViewModel
import com.example.chaika.domain.models.old.ProductInTrip
import com.example.chaika.ui.dialogs.ReplenishAddProductDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private lateinit var productListAdapter: ProductListAdapter
    private val productListViewModel: ProductListViewModel by viewModels()

    private var tripId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tripId = intent.getIntExtra("TRIP_ID", 0)

        setupRecyclerView()
        setupSearchProductTextWatcher()
    }

    private fun setupSearchProductTextWatcher() {
        binding.searchProduct.addTextChangedListener { s ->
            productListViewModel.filterProducts(s.toString())
        }
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter(emptyList()) { product ->
            showQuantityDialog(product)
        }
        binding.productsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.productsRecyclerView.adapter = productListAdapter
        lifecycleScope.launch {
            productListViewModel.filteredProducts.collect { products ->
                productListAdapter.setProducts(products)
            }
        }
    }

    private fun showQuantityDialog(product: Product) {
        ReplenishAddProductDialog(this, product.toProductInTrip()) { quantity ->
            productListViewModel.addAction(tripId, product.id, 1, quantity)
        }.show()
    }

    private fun Product.toProductInTrip(): ProductInTrip {
        return ProductInTrip(id, title, price, 0, 0, 0, 0)
    }
}
