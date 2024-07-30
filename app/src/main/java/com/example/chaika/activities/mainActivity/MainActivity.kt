package com.example.chaika.activities.mainActivity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chaika.MyApp
import com.example.chaika.R
import com.example.chaika.adapters.TripAdapter
import com.example.chaika.databinding.ActivityMainBinding
import com.example.chaika.dataBase.entities.Trip
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
        // Установка обработчиков событий адаптера
        setupTripAdapterListener()

        // Фильтрация списка поездок в зависимости от ввода в поле поиска
        setupSearchTripTextWatcher()

        // Обработка нажатия кнопки добавления новой поездки
        setupAddTripButton()
    }

    private fun setupSearchTripTextWatcher() {
        binding.searchTrip.addTextChangedListener(afterTextChanged = { s ->
            tripAdapter.filter.filter(s.toString())
        })
    }

    private fun setupAddTripButton() {
        binding.addTripFab.setOnClickListener {
            AddRenameTripDialog(this, tripViewModel).show() // вызов диалога для создания новой поездки
        }
    }

    // Настройка RecyclerView и подписка на LiveData с поездками
    private fun setupRecyclerView() {
        binding.tripsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.tripsRecyclerView.adapter = tripAdapter
        tripViewModel.allTrips.observe(this) { trips ->
            tripAdapter.setTrips(trips)
        }
    }

    // Установка слушателей для адаптера
    private fun setupTripAdapterListener() {
        tripAdapter.listener = object : TripAdapter.OnTripListener {
            override fun onDeleteTrip(trip: Trip) {
                tripViewModel.deleteTripAndActions(trip)
                Toast.makeText(this@MainActivity, "Поездка удалена", Toast.LENGTH_SHORT).show()
            }
            override fun onRenameTrip(trip: Trip) {
                AddRenameTripDialog(this@MainActivity, tripViewModel, trip).show() // вызов диалога для переименования существующей поездки
            }
        }
    }
}
