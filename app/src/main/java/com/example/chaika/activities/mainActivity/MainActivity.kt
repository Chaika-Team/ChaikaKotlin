package com.example.chaika.activities.mainActivity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chaika.MyApp
import com.example.chaika.R
import com.example.chaika.databinding.ActivityMainBinding
import com.example.chaika.dataBase.entities.Trip
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val tripViewModel: TripViewModel by viewModels {
        TripViewModelFactory((application as MyApp).repository)
    }
    private val tripAdapter = TripAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupTripAdapterListener()

        binding.searchTrip.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Не используется
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Не используется
            }

            override fun afterTextChanged(s: Editable) {
                tripAdapter.filter.filter(s.toString())
            }
        })

        binding.addTripFab.setOnClickListener {
            showAddRenameTripDialog() // вызов диалога для создания новой поездки
        }

    }

    private fun setupRecyclerView() {
        binding.tripsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.tripsRecyclerView.adapter = tripAdapter
        tripViewModel.allTrips.observe(this) { trips ->
            tripAdapter.setTrips(trips)
        }
    }

    private fun setupTripAdapterListener() {
        tripAdapter.listener = object : TripAdapter.OnTripListener {
            override fun onDeleteTrip(trip: Trip) {
                tripViewModel.deleteTrip(trip)
                Toast.makeText(this@MainActivity, "Поездка удалена", Toast.LENGTH_SHORT).show()
            }
            override fun onRenameTrip(trip: Trip) {
                showAddRenameTripDialog(trip) // вызов диалога для переименования существующей поездки
            }
        }
    }

    private fun showAddRenameTripDialog(tripToRename: Trip? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_trip, null)
        val tripNameEditText = dialogView.findViewById<EditText>(R.id.etTripName)
        tripNameEditText.setText(tripToRename?.name)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .show()

        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnCreate).apply {
            text = if (tripToRename == null) getString(R.string.create) else getString(R.string.rename)
            setOnClickListener {
                val tripName = tripNameEditText.text.toString().trim()
                if (tripName.isNotEmpty()) {
                    val newTrip = tripToRename?.copy(name = tripName) ?: Trip(id = 0, name = tripName, date = getCurrentDate())
                    if (tripToRename == null) {
                        tripViewModel.insertTrip(newTrip)
                    } else {
                        tripViewModel.updateTrip(newTrip)
                    }
                    alertDialog.dismiss()
                } else {
                    Toast.makeText(this@MainActivity, "Введите название поездки", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
