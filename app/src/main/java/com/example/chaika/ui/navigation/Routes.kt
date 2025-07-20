package com.example.chaika.ui.navigation

object Routes {
    const val LOGIN = "login"
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
    const val PRODUCT_PACKAGE = "product/graph/package"
    const val OPERATION = "operation"
    const val PROFILE = "profile"
    const val PROFILE_PERSONAL_DATA = "profile/personal_data"
    const val PROFILE_SETTINGS = "profile/settings"
    const val PROFILE_FAQS = "profile/faqs"
    const val PROFILE_FEEDBACK = "profile/feedback"
    const val PROFILE_ABOUT = "profile/about"
    const val PROFILE_GRAPH = "profile/graph"
    const val TEMPLATE_SEARCH = "template_search"

    val routesWithoutBottomBar = setOf(
        LOGIN, PRODUCT_CART
    )

    val routesWithoutTopBar = setOf(
        LOGIN, PROFILE
    )

    val mainRoutes = mapOf(
        TRIP to listOf(
            TRIP, TRIP_NEW, TRIP_GRAPH,
            TRIP_BY_NUMBER, TRIP_BY_STATION,
            TRIP_SELECT_CARRIAGE, TRIP_CURRENT
        ),
        PRODUCT to listOf(
            PRODUCT_ENTRY, PRODUCT, PRODUCT_LIST,
            PRODUCT_CART, PRODUCT_PACKAGE, PRODUCT_GRAPH
        ),
        OPERATION to listOf(OPERATION),
        PROFILE to listOf(
            PROFILE, PROFILE_PERSONAL_DATA, PROFILE_SETTINGS,
            PROFILE_FAQS, PROFILE_FEEDBACK, PROFILE_ABOUT,
            PROFILE_GRAPH
        )
    )
}