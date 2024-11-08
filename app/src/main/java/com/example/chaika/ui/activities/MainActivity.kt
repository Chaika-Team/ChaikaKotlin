package com.example.chaika.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.chaika.domain.usecases.AddProductInfoUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var addProductInfoUseCase: AddProductInfoUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Выполняем фейковую загрузку данных при старте активности
        performFakeDataLoading()
    }

    private fun performFakeDataLoading() {
        // Используем lifecycleScope для выполнения корутины
        lifecycleScope.launch {
            try {
                addProductInfoUseCase()
                // Уведомляем, что загрузка завершена
                println("Fake data loading completed successfully.")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Failed to load fake data: ${e.message}")
            }
        }
    }
}
