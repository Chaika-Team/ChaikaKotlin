package com.example.chaika.ui.navigation

import com.example.chaika.R

sealed class Screen(
    val route: String,
    val titleResId: Int,
    val showBackButton: Boolean = true
) {
    object Trip : Screen(Routes.TRIP, R.string.trip_title, false)
    object NewTrip : Screen(Routes.TRIP_NEW, R.string.trip_title, false)
    object FindTripByNumber : Screen(Routes.TRIP_BY_NUMBER, R.string.new_trip_title)
    object SelectCarriage : Screen(Routes.TRIP_SELECT_CARRIAGE, R.string.new_trip_title)
    object CurrentTrip : Screen(Routes.TRIP_CURRENT, R.string.trip_title)

    object Product : Screen(Routes.PRODUCT, R.string.products_title, false)
    object ProductEntry : Screen(Routes.PRODUCT_ENTRY, R.string.product_entry_title)
    object ProductList : Screen(Routes.PRODUCT_LIST, R.string.product_list_title)
    object Cart : Screen(Routes.PRODUCT_CART, R.string.cart_title)
    object Package : Screen(Routes.PRODUCT_PACKAGE, R.string.package_title)

    object Operation : Screen(Routes.OPERATION, R.string.operations_title)

    object Profile : Screen(Routes.PROFILE, R.string.profile_title)
    object ProfilePersonalData : Screen(Routes.PROFILE_PERSONAL_DATA, R.string.profile_personal_data)
    object ProfileSettings : Screen(Routes.PROFILE_SETTINGS, R.string.profile_settings)
    object ProfileFaqs : Screen(Routes.PROFILE_FAQS, R.string.profile_faqs)
    object ProfileFeedback : Screen(Routes.PROFILE_FEEDBACK, R.string.profile_feedback)
    object ProfileAbout : Screen(Routes.PROFILE_ABOUT, R.string.profile_about)

    object TemplateSearch : Screen(Routes.TEMPLATE_SEARCH, R.string.templates)
    object TemplateDetail : Screen(Routes.TEMPLATE_DETAIL, R.string.templates)

    companion object {
        fun fromRoute(route: String?): Screen {
            return when (route) {
                Routes.TRIP -> Trip
                Routes.TRIP_NEW -> NewTrip
                Routes.TRIP_BY_NUMBER -> FindTripByNumber
                Routes.TRIP_SELECT_CARRIAGE -> SelectCarriage
                Routes.TRIP_CURRENT -> CurrentTrip
                Routes.PRODUCT -> Product
                Routes.PRODUCT_ENTRY -> ProductEntry
                Routes.PRODUCT_LIST -> ProductList
                Routes.PRODUCT_CART -> Cart
                Routes.PRODUCT_PACKAGE -> Package
                Routes.OPERATION -> Operation
                Routes.PROFILE -> Profile
                Routes.PROFILE_PERSONAL_DATA -> ProfilePersonalData
                Routes.PROFILE_SETTINGS -> ProfileSettings
                Routes.PROFILE_FAQS -> ProfileFaqs
                Routes.PROFILE_FEEDBACK -> ProfileFeedback
                Routes.PROFILE_ABOUT -> ProfileAbout
                Routes.TEMPLATE_SEARCH -> TemplateSearch
                Routes.TEMPLATE_DETAIL -> TemplateDetail
                else -> Trip
            }
        }
    }
}