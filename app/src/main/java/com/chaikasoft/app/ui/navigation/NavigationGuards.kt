package com.chaikasoft.app.ui.navigation

object NavigationGuards {

    fun isProtectedBottomGraph(route: String): Boolean = route in protectedBottomGraphs

    fun requiresActiveShift(route: String?): Boolean = protectedGraphForRoute(route) != null

    fun protectedGraphForRoute(route: String?): String? = when {
        route == null -> null
        route in protectedBottomGraphs -> route
        route == Routes.PRODUCT || route.startsWith("${Routes.PRODUCT}/") -> Routes.PRODUCT_GRAPH
        route == Routes.TEMPLATE || route.startsWith("${Routes.TEMPLATE}/") -> Routes.PRODUCT_GRAPH
        route == Routes.TEMPLATE_DETAIL || route.startsWith("template_detail/") ->
            Routes.PRODUCT_GRAPH
        route == Routes.STATISTICS || route.startsWith("${Routes.STATISTICS}/") ->
            Routes.STATISTICS_GRAPH
        route == Routes.OPERATION || route.startsWith("${Routes.OPERATION}/") ->
            Routes.OPERATION_GRAPH
        else -> null
    }

    private val protectedBottomGraphs = setOf(
        Routes.PRODUCT_GRAPH,
        Routes.STATISTICS_GRAPH,
        Routes.OPERATION_GRAPH
    )
}
