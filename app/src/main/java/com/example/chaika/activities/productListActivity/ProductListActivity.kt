package com.example.chaika.activities.productListActivity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
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

        // Настройка отображения списка продуктов
        setupRecyclerView()

        // Фильтрация списка продуктов в зависимости от ввода в поле поиска
        setupSearchProductTextWatcher()

        // Инициализируем продукты и загружаем их
        productListViewModel.initializeProducts()
        productListViewModel.loadAllProducts()
    }

    private fun setupSearchProductTextWatcher() {
        binding.searchProduct.addTextChangedListener { s ->
            productListViewModel.filterProducts(s.toString())
        }
    }

    // Настройка RecyclerView и подписка на LiveData с продуктами
    private fun setupRecyclerView() {
        adapter = ProductListAdapter(emptyList()) { product ->
            showQuantityDialog(product)
        }
        binding.productsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.productsRecyclerView.adapter = adapter
        productListViewModel.filteredProducts.observe(this, Observer { products ->
            adapter.updateProducts(products)
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
