package com.chaikasoft.app.ui.navigation

import com.chaikasoft.app.R

sealed class Screen(
    val route: String,
    val titleResId: Int,
    val showBackButton: Boolean = true,
    val showMenuIcon: Boolean = false
) {
    object Trip : Screen(Routes.TRIP, R.string.trip_title, showBackButton = false)
    object MainTrip : Screen(Routes.TRIP_MAIN, R.string.trip_title, showBackButton = false)
    object AutonomousTrip : Screen(Routes.TRIP_AUTONOMOUS, R.string.offline_trip_title)
    object FindTripByNumber : Screen(
        Routes.TRIP_BY_NUMBER,
        R.string.new_trip_title,
        showMenuIcon = true
    )
    object SelectCarriage : Screen(Routes.TRIP_SELECT_CARRIAGE, R.string.new_trip_title)

    object Product : Screen(Routes.PRODUCT, R.string.products_title, false)
    object ProductEntry : Screen(
        Routes.PRODUCT_ENTRY,
        R.string.product_entry_title,
        showBackButton = false
    )
    object Cart : Screen(Routes.PRODUCT_CART, R.string.cart_title)
    object Package : Screen(
        Routes.PRODUCT_PACKAGE,
        R.string.package_title,
        showBackButton = false,
        showMenuIcon = true
    )
    object Replenish : Screen(Routes.PRODUCT_REPLENISH, R.string.product_replenish)

    object Statistics : Screen(Routes.STATISTICS, R.string.statistics_title, showBackButton = false)
    object HistoricalStatistics : Screen(Routes.HISTORY_STATISTICS, R.string.statistics_title)

    object Operation : Screen(Routes.OPERATION, R.string.operations_title, showBackButton = false)
    object HistoricalOperation : Screen(Routes.HISTORY_OPERATIONS, R.string.operations_title)

    object Profile : Screen(Routes.PROFILE, R.string.profile_title, showBackButton = false)
    object ProfilePersonalData : Screen(
        Routes.PROFILE_PERSONAL_DATA,
        R.string.profile_personal_data
    )
    object ProfileSettings : Screen(Routes.PROFILE_SETTINGS, R.string.profile_settings)
    object ProfileFaqs : Screen(Routes.PROFILE_FAQS, R.string.profile_faqs)
    object ProfileFeedback : Screen(Routes.PROFILE_FEEDBACK, R.string.profile_feedback)
    object ProfileAbout : Screen(Routes.PROFILE_ABOUT, R.string.profile_about)

    object TemplateSearch : Screen(Routes.TEMPLATE_SEARCH, R.string.templates)
    object TemplateDetail : Screen(Routes.TEMPLATE_DETAIL, R.string.templates)
    object TemplateEdit : Screen(Routes.TEMPLATE_EDIT, R.string.edit)
    object TemplateConfirm : Screen(
        Routes.TEMPLATE_CONFIRM,
        titleResId = R.string.template_confirm_title
    )

    companion object {
        private val screensByRoute = mapOf(
            Routes.TRIP to Trip,
            Routes.TRIP_MAIN to MainTrip,
            Routes.TRIP_AUTONOMOUS to AutonomousTrip,
            Routes.TRIP_BY_NUMBER to FindTripByNumber,
            Routes.TRIP_SELECT_CARRIAGE to SelectCarriage,
            Routes.PRODUCT to Product,
            Routes.PRODUCT_ENTRY to ProductEntry,
            Routes.PRODUCT_CART to Cart,
            Routes.PRODUCT_PACKAGE to Package,
            Routes.PRODUCT_REPLENISH to Replenish,
            Routes.OPERATION to Operation,
            Routes.PROFILE to Profile,
            Routes.PROFILE_PERSONAL_DATA to ProfilePersonalData,
            Routes.PROFILE_SETTINGS to ProfileSettings,
            Routes.PROFILE_FAQS to ProfileFaqs,
            Routes.PROFILE_FEEDBACK to ProfileFeedback,
            Routes.PROFILE_ABOUT to ProfileAbout,
            Routes.TEMPLATE_SEARCH to TemplateSearch,
            Routes.TEMPLATE_DETAIL to TemplateDetail,
            Routes.TEMPLATE_EDIT to TemplateEdit,
            Routes.TEMPLATE_CONFIRM to TemplateConfirm,
            Routes.STATISTICS to Statistics
        )

        fun fromRoute(route: String?): Screen = when {
            Routes.isHistoricalStatisticsRoute(route) -> HistoricalStatistics
            Routes.isHistoricalOperationsRoute(route) -> HistoricalOperation
            else -> route?.let(screensByRoute::get) ?: Trip
        }
    }
}
