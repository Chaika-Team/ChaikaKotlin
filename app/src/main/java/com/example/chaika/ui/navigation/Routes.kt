package com.example.chaika.ui.navigation

object Routes {
    const val TRIP = "trip"
    const val TRIP_NEW = "trip/graph/new"
    const val TRIP_BY_NUMBER = "trip/graph/by_number"
    const val TRIP_BY_STATION = "trip/graph/by_station"
    const val TRIP_SELECT_CARRIAGE = "trip/graph/select_carriage"
    const val TRIP_CURRENT = "trip/graph/current"
    const val TRIP_GRAPH = "trip/graph"
    const val PRODUCT_ENTRY = "product_entry"
    const val PRODUCT = "product"
    const val OPERATION = "operation"
    const val PROFILE = "profile"

    val mainRoutes = mapOf(
        TRIP to listOf(
            TRIP, TRIP_NEW, TRIP_GRAPH,
            TRIP_BY_NUMBER, TRIP_BY_STATION,
            TRIP_SELECT_CARRIAGE, TRIP_CURRENT
        ),
        PRODUCT to listOf(PRODUCT_ENTRY, PRODUCT),
        OPERATION to listOf(OPERATION),
        PROFILE to listOf(PROFILE)
    )
}