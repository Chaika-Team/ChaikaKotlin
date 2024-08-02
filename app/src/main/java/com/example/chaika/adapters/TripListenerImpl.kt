package com.example.chaika.adapters

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.chaika.activities.productTableActivity.ProductTableActivity
import com.example.chaika.dataBase.entities.Trip
import com.example.chaika.services.TripViewModel
import com.example.chaika.utils.dialogs.AddRenameTripDialog

class TripListenerImpl(
    private val context: Context,
    private val tripViewModel: TripViewModel
) : TripListener {

    override fun onDeleteTrip(trip: Trip) {
        tripViewModel.deleteTripAndActions(trip)
        Toast.makeText(context, "Поездка удалена", Toast.LENGTH_SHORT).show()
    }

    override fun onRenameTrip(trip: Trip) {
        AddRenameTripDialog(context, tripViewModel, trip).show()
    }

    override fun onOpenProducts(tripId: Int) {
        val intent = Intent(context, ProductTableActivity::class.java)
        intent.putExtra("TRIP_ID", tripId)
        context.startActivity(intent)
    }
}
