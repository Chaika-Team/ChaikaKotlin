package com.example.chaika.data.dataSource.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO для станции.
 */
data class StationDto(
    @SerializedName("code") val code: Int,
    @SerializedName("name") val name: String,
    @SerializedName("city") val city: String
)

/**
 * Ответ API со списком станций.
 */
data class StationsResponseDto(
    @SerializedName("stations") val stations: List<StationDto>
)

/**
 * DTO для поездки.
 */
data class TripDto(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("train_number") val trainNumber: String,
    @SerializedName("departure") val departure: String,
    @SerializedName("arrival") val arrival: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("from_station") val from: StationDto,
    @SerializedName("to_station") val to: StationDto
)

/**
 * Ответ API со списком поездок.
 */
data class TripsResponseDto(
    @SerializedName("trips") val trips: List<TripDto>
)

/**
 * DTO для отдельной поездки (detail).
 */
data class TripDetailResponseDto(
    @SerializedName("trip") val trip: TripDto
)

/**
 * DTO для вагона.
 */
data class CarDto(
    @SerializedName("car_number") val carNumber: String,
    @SerializedName("class_type") val classType: String
)

/**
 * Ответ API со списком вагонов.
 */
data class CarsResponseDto(
    @SerializedName("cars") val cars: List<CarDto>
)
