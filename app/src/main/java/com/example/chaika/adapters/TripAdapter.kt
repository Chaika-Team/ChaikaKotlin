// TripAdapter.kt
package com.example.chaika.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.chaika.databinding.TripItemBinding
import com.example.chaika.dataBase.entities.Trip
import java.util.Locale
import com.example.chaika.activities.productTableActivity.ProductTableActivity
import android.content.Intent
import com.example.chaika.utils.expand
import com.example.chaika.utils.collapse

class TripAdapter : RecyclerView.Adapter<TripAdapter.TripViewHolder>(), Filterable {

    private var trips: List<Trip> = listOf()
    private var allTrips: List<Trip> = listOf()

    fun setTrips(trips: List<Trip>) {
        this.trips = trips
        this.allTrips = ArrayList(trips)
        notifyDataSetChanged()
    }

    var listener: OnTripListener? = null

    interface OnTripListener {
        fun onDeleteTrip(trip: Trip)
        fun onRenameTrip(trip: Trip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = TripItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.bind(trip, listener)
    }

    override fun getItemCount() = trips.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = if (constraint.isNullOrBlank()) {
                    allTrips
                } else {
                    val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim()
                    allTrips.filter {
                        it.name.lowercase(Locale.getDefault()).contains(filterPattern)
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                trips = results?.values as List<Trip>
                notifyDataSetChanged()
            }
        }
    }

    class TripViewHolder(private val binding: TripItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip, listener: OnTripListener?) {
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
                listener?.onRenameTrip(trip)
            }

            binding.deleteText.setOnClickListener {
                listener?.onDeleteTrip(trip)
            }

            binding.productsText.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, ProductTableActivity::class.java)
                intent.putExtra("TRIP_ID", trip.id)
                context.startActivity(intent)
            }
        }
    }
}
