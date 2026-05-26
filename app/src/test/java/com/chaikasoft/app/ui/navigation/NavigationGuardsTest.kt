package com.chaikasoft.app.ui.navigation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NavigationGuardsTest : FunSpec({

    test("requiresActiveShift returns true for product statistics and operation routes") {
        val protectedRoutes = listOf(
            Routes.PRODUCT_GRAPH,
            Routes.PRODUCT_GATE,
            Routes.PRODUCT_ENTRY,
            Routes.PRODUCT_CART,
            Routes.PRODUCT_PACKAGE,
            Routes.PRODUCT_REPLENISH,
            Routes.TEMPLATE_SEARCH,
            Routes.TEMPLATE_DETAIL,
            Routes.TEMPLATE_EDIT,
            Routes.TEMPLATE_CONFIRM,
            Routes.STATISTICS,
            Routes.STATISTICS_GRAPH,
            Routes.OPERATION,
            Routes.OPERATION_GRAPH
        )

        protectedRoutes.forEach { route ->
            NavigationGuards.requiresActiveShift(route) shouldBe true
        }
    }

    test("requiresActiveShift returns false for trip profile auth and utility routes") {
        val publicRoutes = listOf(
            null,
            Routes.LOADING,
            Routes.LOGIN,
            Routes.AUTH_GRAPH,
            Routes.TRIP_GRAPH,
            Routes.TRIP_GATE,
            Routes.TRIP_MAIN,
            Routes.TRIP_AUTONOMOUS,
            Routes.TRIP_BY_NUMBER,
            Routes.TRIP_SELECT_CARRIAGE,
            Routes.PROFILE,
            Routes.PROFILE_GRAPH,
            Routes.PROFILE_PERSONAL_DATA,
            Routes.ERROR
        )

        publicRoutes.forEach { route ->
            NavigationGuards.requiresActiveShift(route) shouldBe false
        }
    }

    test("protectedGraphForRoute maps protected routes to their parent graph") {
        NavigationGuards.protectedGraphForRoute(Routes.PRODUCT_GRAPH) shouldBe
            Routes.PRODUCT_GRAPH
        NavigationGuards.protectedGraphForRoute(Routes.STATISTICS_GRAPH) shouldBe
            Routes.STATISTICS_GRAPH
        NavigationGuards.protectedGraphForRoute(Routes.OPERATION_GRAPH) shouldBe
            Routes.OPERATION_GRAPH
        NavigationGuards.protectedGraphForRoute(Routes.PRODUCT_PACKAGE) shouldBe
            Routes.PRODUCT_GRAPH
        NavigationGuards.protectedGraphForRoute(Routes.TEMPLATE_CONFIRM) shouldBe
            Routes.PRODUCT_GRAPH
        NavigationGuards.protectedGraphForRoute(Routes.STATISTICS) shouldBe
            Routes.STATISTICS_GRAPH
        NavigationGuards.protectedGraphForRoute(Routes.OPERATION) shouldBe
            Routes.OPERATION_GRAPH
        NavigationGuards.protectedGraphForRoute(Routes.TRIP_MAIN) shouldBe null
    }
})
