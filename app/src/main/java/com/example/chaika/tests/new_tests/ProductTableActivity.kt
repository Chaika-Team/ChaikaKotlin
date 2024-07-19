package com.example.chaika.tests.new_tests

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chaika.R
import com.example.chaika.dataBase.entities.Product

class ProductTableActivity : AppCompatActivity() {
    private lateinit var viewModel: ProductViewModel
    private lateinit var adapter: TableProductAdapter
    private lateinit var editTextProductName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_table)

        editTextProductName = findViewById(R.id.editTextProductName)
        val buttonAddProduct: Button = findViewById(R.id.buttonAddProduct)
        adapter = TableProductAdapter()
        val recyclerView: RecyclerView = findViewById(R.id.productsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        //----------- SWIPE TO DEL -------------------

        val swipeToDeleteCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                // Не нужно здесь ничего менять
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Получаем позицию элемента, который был свайпнут
                val position = viewHolder.adapterPosition

                // Получаем продукт из адаптера по этой позиции
                val productToDelete = adapter.getProductAt(position)

                // Удаляем продукт из базы данных
                viewModel.delete(productToDelete)
            }
        }

// Присоединяем ItemTouchHelper к RecyclerView
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        //----------- /SWIPE TO DEL -------------------

        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        viewModel.allProducts.observe(this, Observer { products ->
            products?.let { adapter.setProducts(it) }
        })

        buttonAddProduct.setOnClickListener {
            addProduct()
        }
    }

    private fun addProduct() {
        val productName = editTextProductName.text.toString().trim()
        if (productName.isNotEmpty()) {
            val newProduct = Product(id = 0, title = productName, price = 0.0) // Price по умолчанию равен нулю
            viewModel.insert(newProduct) // Добавление нового продукта в базу данных
            editTextProductName.setText("") // Очистка поля ввода
        }
    }
}
