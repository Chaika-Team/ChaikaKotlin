package com.example.chaika.ui.dialogs

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.chaika.R
import com.example.chaika.domain.models.old.Trip
import com.example.chaika.ui.view_models.TripViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddRenameTripDialog(
    private val context: Context,
    private val tripViewModel: TripViewModel,
    private val tripToRename: Trip? = null
) {

    fun show() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_trip, null)
        val tripNameEditText = dialogView.findViewById<EditText>(R.id.setTripName)

        // Установка текста, если tripToRename не равно null
        tripToRename?.let {
            tripNameEditText.setText(it.name)
        }

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .show().apply {
                window?.setBackgroundDrawableResource(android.R.color.transparent)
            }

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnCreate).apply {
            text = if (tripToRename == null) context.getString(R.string.create) else context.getString(R.string.rename)
            setOnClickListener {
                val tripName = tripNameEditText.text.toString().trim()
                if (tripName.isNotEmpty()) {
                    val newTrip = tripToRename?.copy(name = tripName) ?: Trip(id = 0, name = tripName, date = getCurrentDate())
                    handleTripOperation(newTrip, alertDialog)
                } else {
                    Toast.makeText(context, context.getString(R.string.enter_trip_name), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleTripOperation(newTrip: Trip, alertDialog: AlertDialog) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                if (tripToRename == null) {
                    tripViewModel.insertTrip(newTrip)
                } else {
                    tripViewModel.updateTrip(newTrip)
                }
                alertDialog.dismiss()
                Toast.makeText(context, context.getString(R.string.trip_added), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("AddRenameTripDialog", "Error performing trip operation", e)
                Toast.makeText(context, context.getString(R.string.error_performing_operation, e.message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // TODO: Перенести отсюда к хуям собачим в рум
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
