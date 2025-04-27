package com.example.chaika.ui.navigation

object Routes {
    const val TRIP = "trip"
    const val TRIP_NEW = "trip/graph/new"
    const val TRIP_BY_NUMBER = "trip/graph/by_number"
    const val TRIP_BY_STATION = "trip/graph/by_station"
    const val TRIP_SELECT_CARRIAGE = "trip/graph/select_carriage"
    const val TRIP_CURRENT = "trip/graph/current"
    const val TRIP_GRAPH = "trip/graph"
    const val PRODUCT = "product"
    const val PRODUCT_GRAPH = "product/graph"
    const val PRODUCT_ENTRY = "product/graph/product_entry"
    const val PRODUCT_LIST = "product/graph/product_list"
    const val PRODUCT_CART = "product/graph/cart"
    const val OPERATION = "operation"
    const val PROFILE = "profile"

    val mainRoutes = mapOf(
        TRIP to listOf(
            TRIP, TRIP_NEW, TRIP_GRAPH,
            TRIP_BY_NUMBER, TRIP_BY_STATION,
            TRIP_SELECT_CARRIAGE, TRIP_CURRENT
        ),
        PRODUCT to listOf(PRODUCT_ENTRY, PRODUCT, PRODUCT_LIST, PRODUCT_CART),
        OPERATION to listOf(OPERATION),
        PROFILE to listOf(PROFILE)
    )
}