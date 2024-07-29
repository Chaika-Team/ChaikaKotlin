package com.example.chaika.activities.productListActivity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chaika.MyApp
import com.example.chaika.adapters.ProductListAdapter
import com.example.chaika.dataBase.entities.Product
import com.example.chaika.databinding.ActivityProductListBinding
import com.example.chaika.services.ProductListViewModel
import com.example.chaika.services.ProductListViewModelFactory
import com.example.chaika.utils.ProductInTrip
import com.example.chaika.utils.dialogs.ReplenishAddProductDialog

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private lateinit var adapter: ProductListAdapter
    private val productListViewModel: ProductListViewModel by viewModels {
        ProductListViewModelFactory(
            (application as MyApp).productRepository,
            (application as MyApp).actionRepository
        )
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

        productListViewModel.allProducts.observe(this, Observer { products ->
            Log.d("ProductListActivity", "All Products: $products")
            adapter.updateProducts(products)
        })

        // Инициализируем продукты и загружаем их
        productListViewModel.initializeProducts()
        productListViewModel.loadAllProducts()

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
        ReplenishAddProductDialog(this, product.toProductInTrip()) { quantity ->
            productListViewModel.addAction(tripId, product.id, 1, quantity)
        }.show()
    }

    // Дополнительная функция для преобразования Product в ProductInTrip
    private fun Product.toProductInTrip(): ProductInTrip {
        return ProductInTrip(id, title, price, 0, 0, 0, 0)
    }
}
