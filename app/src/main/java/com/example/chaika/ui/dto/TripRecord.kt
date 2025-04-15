package com.example.chaika.ui.dto

import java.time.LocalDateTime

data class TripRecord (
    val routeID: Int,
    val trainId: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val carriageID: Int,
)