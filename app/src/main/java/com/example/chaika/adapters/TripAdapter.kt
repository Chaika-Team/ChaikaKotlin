package com.example.chaika.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chaika.activities.productTableActivity.ProductTableActivity
import com.example.chaika.dataBase.entities.Trip
import com.example.chaika.databinding.TripItemBinding
import com.example.chaika.utils.collapse
import com.example.chaika.utils.expand

class TripAdapter : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    private var trips: List<Trip> = listOf()

    fun setTrips(trips: List<Trip>) {
        this.trips = trips
        notifyDataSetChanged()
    }

    lateinit var listener: TripListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = TripItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.bind(trip, listener)
    }

    override fun getItemCount() = trips.size

    class TripViewHolder(private val binding: TripItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip, listener: TripListener) {
            binding.tripNameTextView.text = trip.name
            binding.tripDateTextView.text = trip.date

            itemView.setOnClickListener {
                if (binding.expandableSection.visibility == ViewGroup.VISIBLE) {
                    binding.expandableSection.collapse()
                } else {
                    binding.expandableSection.expand()
                }
            }

            binding.renameText.setOnClickListener {
                listener.onRenameTrip(trip)
            }

            binding.deleteText.setOnClickListener {
                listener.onDeleteTrip(trip)
            }

            binding.productsText.setOnClickListener {
                listener.onOpenProducts(trip.id)
            }
        }
    }
}

interface TripListener {
    fun onDeleteTrip(trip: Trip)
    fun onRenameTrip(trip: Trip)
    fun onOpenProducts(tripId: Int)
}
