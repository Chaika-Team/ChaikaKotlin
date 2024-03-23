package com.example.chaika.activities.mainActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import android.animation.ValueAnimator
import android.view.View
import android.widget.Filter
import android.widget.Filterable
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.RecyclerView
import com.example.chaika.databinding.TripItemBinding
import com.example.chaika.dataBase.entities.Trip
import java.util.Locale

class TripAdapter : RecyclerView.Adapter<TripAdapter.TripViewHolder>(), Filterable {

    private var trips: List<Trip> = listOf()
    private var allTrips: List<Trip> = listOf()

    fun setTrips(trips: List<Trip>) {
        this.trips = trips
        this.allTrips = ArrayList(trips) // Сохраняем полный список поездок
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

            // Expand or collapse section when the entire item is clicked
            itemView.setOnClickListener {
                if (binding.expandableSection.visibility == ViewGroup.VISIBLE) {
                    animateCollapse(binding.expandableSection)
                } else {
                    animateExpand(binding.expandableSection)
                }
            }

            binding.renameText.setOnClickListener {
                listener?.onRenameTrip(trip)
            }

            binding.deleteText.setOnClickListener {
                listener?.onDeleteTrip(trip)
            }
        }

        private fun animateExpand(view: ViewGroup) {
            view.visibility = ViewGroup.VISIBLE
            val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec((view.parent as View).width, View.MeasureSpec.EXACTLY)
            val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
            val targetHeight = view.measuredHeight

            val animation = ValueAnimator.ofInt(0, targetHeight).apply {
                addUpdateListener { valueAnimator ->
                    val layoutParams = view.layoutParams
                    layoutParams.height = valueAnimator.animatedValue as Int
                    view.layoutParams = layoutParams
                }
                duration = 200L
            }
            animation.start()
        }

        private fun animateCollapse(view: ViewGroup) {
            val initialHeight = view.measuredHeight

            val animation = ValueAnimator.ofInt(initialHeight, 0).apply {
                addUpdateListener { valueAnimator ->
                    val layoutParams = view.layoutParams
                    layoutParams.height = valueAnimator.animatedValue as Int
                    view.layoutParams = layoutParams
                }
                duration = 200L
                doOnEnd { view.visibility = ViewGroup.GONE }
            }
            animation.start()
        }
    }

}
