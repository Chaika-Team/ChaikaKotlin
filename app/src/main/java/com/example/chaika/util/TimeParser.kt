package com.example.chaika.util

import android.util.Log
import com.example.chaika.domain.models.trip.TripDomain
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun getDayMonth(dateTime: String): String {
    return try {
        val instant = Instant.parse(dateTime)
        val zonedDateTime = instant.atZone(ZoneId.systemDefault())
        DateTimeFormatter.ofPattern("dd.MM").format(zonedDateTime)
    } catch (e: Exception) {
        Log.e("TimeParser", e.toString())
        "N/A"
    }
}

fun getTime(dateTime: String): String {
    return try {
        val instant = Instant.parse(dateTime)
        val zonedDateTime = instant.atZone(ZoneId.systemDefault())
        DateTimeFormatter.ofPattern("HH:mm").format(zonedDateTime)
    } catch (e: Exception) {
        Log.e("TimeParser", e.toString())
        "N/A"
    }
}

fun parseDuration(duration: String): Pair<Int, Int> {
    return try {
        val dur = Duration.parse(duration)
        val hours = dur.toHours().toInt()
        val minutes = dur.minusHours(hours.toLong()).toMinutes().toInt()
        hours to minutes
    } catch (e: Exception) {
        Log.e("TimeParser", e.toString())
        0 to 0
    }
}

fun TripDomain.parseTripDetails(): TripDetails {
    return TripDetails(
        departureDayMonth = getDayMonth(this.departure),
        arrivalDayMonth = getDayMonth(this.arrival),
        departureTime = getTime(this.departure),
        arrivalTime = getTime(this.arrival),
        durationHours = parseDuration(this.duration).first,
        durationMinutes = parseDuration(this.duration).second
    )
}

data class TripDetails(
    val departureDayMonth: String,
    val arrivalDayMonth: String,
    val departureTime: String,
    val arrivalTime: String,
    val durationHours: Int,
    val durationMinutes: Int
)

