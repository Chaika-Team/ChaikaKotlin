package com.chaikasoft.app.ui.navigation

object Routes {
    const val ERROR = "error"
    const val LOADING = "loading"
    const val AUTH_GRAPH = "auth/graph"
    const val LOGIN = "login"
    const val TRIP = "trip"
    const val TEMPLATE = "template"
    const val TRIP_MAIN = "trip/graph/main"
    const val TRIP_AUTONOMOUS = "trip/graph/autonomous"
    const val TRIP_BY_NUMBER = "trip/graph/by_number"
    const val TRIP_BY_STATION = "trip/graph/by_station"
    const val TRIP_SELECT_CARRIAGE = "trip/graph/select_carriage"
    const val TRIP_GRAPH = "trip/graph"
    const val PRODUCT = "product"
    const val PRODUCT_GRAPH = "product/graph"
    const val PRODUCT_GATE = "product/graph/gate"
    const val PRODUCT_ENTRY = "product/graph/product_entry"
    const val PRODUCT_CART = "product/graph/cart"
    const val PRODUCT_PACKAGE = "product/graph/package"
    const val PRODUCT_LIST_EMPTY = "product/graph/product_list_empty"
    const val PRODUCT_REPLENISH = "product/replenish"
    const val STATISTICS = "statistics"
    const val STATISTICS_GRAPH = "statistics/graph"
    const val OPERATION = "operation"
    const val OPERATION_GRAPH = "operation/graph"
    const val PROFILE = "profile"
    const val PROFILE_PERSONAL_DATA = "profile/personal_data"
    const val PROFILE_SETTINGS = "profile/settings"
    const val PROFILE_FAQS = "profile/faqs"
    const val PROFILE_FEEDBACK = "profile/feedback"
    const val PROFILE_ABOUT = "profile/about"
    const val PROFILE_GRAPH = "profile/graph"
    const val TEMPLATE_GRAPH = "template/graph"
    const val TEMPLATE_SEARCH = "template/search"
    const val TEMPLATE_DETAIL = "template_detail/{templateId}"
    const val TEMPLATE_EDIT = "template/edit"
    const val TEMPLATE_CONFIRM = "template/package_confirm"

    val routesWithoutBottomBar = setOf(
        LOGIN, LOADING, PRODUCT_GATE, PRODUCT_CART,
        TEMPLATE_SEARCH, TEMPLATE_DETAIL, TEMPLATE_EDIT,
        TEMPLATE_CONFIRM, PRODUCT_REPLENISH, TRIP_AUTONOMOUS,
        TRIP_AUTONOMOUS, TRIP_BY_STATION, TRIP_BY_NUMBER,
        TRIP_SELECT_CARRIAGE,
    )

    val routesWithoutTopBar = setOf(
        LOGIN, PROFILE
    )

    val mainRoutes = mapOf(
        TRIP to listOf(
            TRIP, TRIP_MAIN, TRIP_GRAPH,
            TRIP_BY_NUMBER, TRIP_BY_STATION,
            TRIP_SELECT_CARRIAGE, TRIP_AUTONOMOUS
        ),
        PRODUCT to listOf(
            PRODUCT_GATE,
            PRODUCT_ENTRY, PRODUCT,
            PRODUCT_CART, PRODUCT_PACKAGE, PRODUCT_GRAPH,
            PRODUCT_LIST_EMPTY
        ),
        STATISTICS to listOf(STATISTICS, STATISTICS_GRAPH),
        OPERATION to listOf(OPERATION),
        PROFILE to listOf(
            PROFILE, PROFILE_PERSONAL_DATA, PROFILE_SETTINGS,
            PROFILE_FAQS, PROFILE_FEEDBACK, PROFILE_ABOUT,
            PROFILE_GRAPH
        ),
        TEMPLATE to listOf(
            TEMPLATE_GRAPH, TEMPLATE_SEARCH, TEMPLATE_DETAIL,
            TEMPLATE_EDIT, TEMPLATE_CONFIRM
        )
    )
}