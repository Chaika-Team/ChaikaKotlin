package com.chaikasoft.app.e2e.tests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.LargeTest
import com.chaikasoft.app.e2e.rules.FailureDiagnosticsRule
import com.chaikasoft.app.ui.activities.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
@HiltAndroidTest
class HermeticSmokeE2ETest {
    private companion object {
        const val SAVELOVSKAYA_CODE = "2001002"
    }

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 2)
    val diagnosticsRule = FailureDiagnosticsRule()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun launch_authenticatedSessionOpensTripScreen() {
        waitForTag("tripMainScreen")
        composeRule.onNodeWithTag("newTripButton").assertIsDisplayed()
    }

    @Test
    fun tripSearch_showsStationSuggestionsWithoutNetwork() {
        waitForTag("newTripButton")
        composeRule.onNodeWithTag("newTripButton").performClick()
        waitForTag("findTripScreen")

        composeRule.onNodeWithTag("tripFromStationInput").performTextInput("СА")
        waitForTag("tripFromStationItem_$SAVELOVSKAYA_CODE")
        composeRule.onNodeWithTag("tripFromStationItem_$SAVELOVSKAYA_CODE").performClick()
    }

    @Test
    fun bottomBar_canNavigateToAllMainSections() {
        waitForTag("tripMainScreen")

        composeRule.onNodeWithTag("bottomBarProduct").performClick()
        waitForAnyTag("productEntryScreen", "productPackageScreen")

        composeRule.onNodeWithTag("bottomBarStatistics").performClick()
        waitForTag("statisticsScreen")

        composeRule.onNodeWithTag("bottomBarOperation").performClick()
        waitForTag("operationScreen")

        composeRule.onNodeWithTag("bottomBarProfile").performClick()
        waitForTag("profileScreen")

        composeRule.onNodeWithTag("bottomBarTrip").performClick()
        waitForTag("tripMainScreen")
    }

    @Test
    fun productEntry_hasPrimaryCtaTag() {
        waitForTag("bottomBarProduct")
        composeRule.onNodeWithTag("bottomBarProduct").performClick()

        composeRule.waitUntil(timeoutMillis = 10_000L) {
            composeRule.onAllNodesWithTag("productEntryFillPackageButton").fetchSemanticsNodes().isNotEmpty() ||
                composeRule.onAllNodesWithTag("packageListGrid").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun logout_returnsToLoginScreen() {
        waitForTag("bottomBarProfile")
        composeRule.onNodeWithTag("bottomBarProfile").performClick()
        waitForTag("profileScreen")

        composeRule.onNodeWithTag("profileLogoutButton").performClick()
        waitForTag("profileLogoutConfirmButton")
        composeRule.onNodeWithTag("profileLogoutConfirmButton").performClick()

        waitForTag("loginScreen")
        composeRule.onNodeWithTag("loginButton").assertIsDisplayed()
    }

    private fun waitForTag(tag: String, timeoutMillis: Long = 10_000L) {
        composeRule.waitUntil(timeoutMillis = timeoutMillis) {
            composeRule.onAllNodesWithTag(tag, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForAnyTag(vararg tags: String, timeoutMillis: Long = 10_000L) {
        composeRule.waitUntil(timeoutMillis = timeoutMillis) {
            tags.any { tag ->
                composeRule.onAllNodesWithTag(tag, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
            }
        }
    }
}
