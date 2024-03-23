package com.example.chaika.tests

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chaika.R

class TestProductListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_product_test)









        val productsList: RecyclerView = findViewById(R.id.ProductsList)
        val productOLDS = arrayListOf<ProductOLD>()

        productOLDS.add(ProductOLD("Круассаны", 4, 1, 110))
        productOLDS.add(ProductOLD("Фисташки", 3, 2, 55))
        productOLDS.add(ProductOLD("Кофе Жокей", 5, 3, 30))
        productOLDS.add(ProductOLD("Чай чёрный", 10, 8, 25))
        productOLDS.add(ProductOLD("Чай зелёный", 10, 4, 25))
        productOLDS.add(ProductOLD("Сахар", 30, 12, 10))

        productsList.layoutManager = LinearLayoutManager(this)
        productsList.adapter = ProductAdapter(productOLDS, this)
    }
}