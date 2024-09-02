package com.example.chaika.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chaika.domain.models.old.Trip
import com.example.chaika.databinding.TripItemBinding
import com.example.chaika.ui.animations.collapse
import com.example.chaika.ui.animations.expand

class TripAdapter(private val listener: TripListener) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    private var trips: List<Trip> = listOf()

    fun setTrips(newTrips: List<Trip>) {
        trips = newTrips
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = TripItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.bind(trip, listener)
    }

    override fun getItemCount(): Int = trips.size

    class TripViewHolder(private val binding: TripItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip, listener: TripListener) {
            binding.tripNameTextView.text = trip.name
            binding.tripDateTextView.text = trip.date

            setupExpandableSection()

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

        private fun setupExpandableSection() {
            binding.root.setOnClickListener {
                if (binding.expandableSection.visibility == ViewGroup.VISIBLE) {
                    binding.expandableSection.collapse()
                } else {
                    binding.expandableSection.expand()
                }
            }
        }
    }
}


interface TripListener {
    fun onDeleteTrip(trip: Trip)
    fun onRenameTrip(trip: Trip)
    fun onOpenProducts(tripId: Int)
}
