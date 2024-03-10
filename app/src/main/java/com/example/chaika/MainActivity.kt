package com.example.chaika

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val productsList: RecyclerView = findViewById(R.id.ProductsList)
        val products = arrayListOf<Product>()

        products.add(Product("Круассаны", 4, 1, 110))
        products.add(Product("Фисташки", 3, 2, 55))
        products.add(Product("Кофе Жокей", 5, 3, 30))
        products.add(Product("Чай чёрный", 10, 8, 25))
        products.add(Product("Чай зелёный", 10, 4, 25))
        products.add(Product("Сахар", 30, 12, 10))

        productsList.layoutManager = LinearLayoutManager(this)
        productsList.adapter = ProductAdapter(products, this)
    }
}