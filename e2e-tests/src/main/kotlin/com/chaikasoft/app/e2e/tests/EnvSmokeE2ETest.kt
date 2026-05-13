package com.chaikasoft.app.e2e.tests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.chaikasoft.app.e2e.rules.FailureDiagnosticsRule
import com.chaikasoft.app.ui.activities.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
@HiltAndroidTest
class EnvSmokeE2ETest {

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
    fun authBootstrapDisabled_showsLoginScreen() {
        waitForTag("loginScreen")
        composeRule.onNodeWithTag("loginButton").assertIsDisplayed()
    }

    @Test
    fun loginButton_opensExternalBrowserAuthFlow() {
        waitForTag("loginScreen")
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val device = UiDevice.getInstance(instrumentation)
        val appPackage = instrumentation.targetContext.packageName

        composeRule.onNodeWithTag("loginButton").performClick()

        composeRule.waitUntil(timeoutMillis = 15_000L) {
            val currentPackage = device.currentPackageName
            currentPackage != null && currentPackage != appPackage
        }

        assertNotEquals(appPackage, device.currentPackageName)
        device.pressBack()
    }

    private fun waitForTag(tag: String, timeoutMillis: Long = 10_000L) {
        composeRule.waitUntil(timeoutMillis = timeoutMillis) {
            composeRule.onAllNodesWithTag(tag, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
