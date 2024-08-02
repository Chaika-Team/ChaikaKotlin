package com.example.chaika.activities.mainActivity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chaika.MyApp
import com.example.chaika.adapters.TripAdapter
import com.example.chaika.adapters.TripListenerImpl
import com.example.chaika.databinding.ActivityMainBinding
import com.example.chaika.services.TripViewModel
import com.example.chaika.services.TripViewModelFactory
import com.example.chaika.utils.dialogs.AddRenameTripDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val tripViewModel: TripViewModel by viewModels {
        TripViewModelFactory((application as MyApp).tripRepository)
    }
    private val tripAdapter = TripAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка отображения списка поездок
        setupRecyclerView()

        // Фильтрация списка поездок в зависимости от ввода в поле поиска
        setupSearchTripTextWatcher()

        // Обработка нажатия кнопки добавления новой поездки
        setupAddTripButton()

        // Назначаем слушателя адаптеру
        tripAdapter.listener = TripListenerImpl(this, tripViewModel)
    }

    private fun setupSearchTripTextWatcher() {
        binding.searchTrip.addTextChangedListener { s ->
            tripViewModel.filterTrips(s.toString())
        }
    }

    private fun setupAddTripButton() {
        binding.addTripFab.setOnClickListener {
            AddRenameTripDialog(this, tripViewModel).show() // вызов диалога для создания новой поездки
        }
    }

    private fun setupRecyclerView() {
        binding.tripsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.tripsRecyclerView.adapter = tripAdapter
        tripViewModel.filteredTrips.observe(this) { trips ->
            trips?.let { tripAdapter.setTrips(it) }
        }
    }
}
