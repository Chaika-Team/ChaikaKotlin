package com.chaikasoft.app.e2e.tests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
class CompatibilityApi26SmokeE2ETest {

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
    fun launch_opensTripMainOnApi26Lane() {
        waitForTag("tripMainScreen")
        composeRule.onNodeWithTag("newTripButton").assertIsDisplayed()
    }

    @Test
    fun bottomBar_navigationWorksOnApi26Lane() {
        waitForTag("bottomBarProfile")
        composeRule.onNodeWithTag("bottomBarProfile").performClick()
        waitForTag("profileScreen")
        composeRule.onNodeWithTag("bottomBarTrip").performClick()
        waitForTag("tripMainScreen")
    }

    private fun waitForTag(tag: String, timeoutMillis: Long = 10_000L) {
        composeRule.waitUntil(timeoutMillis = timeoutMillis) {
            composeRule.onAllNodesWithTag(tag, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
