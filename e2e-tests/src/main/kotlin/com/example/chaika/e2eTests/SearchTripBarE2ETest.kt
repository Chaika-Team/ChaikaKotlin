package com.example.chaika.e2eTests

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.chaika.ui.activities.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SearchTripBarE2ETest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun searchTripBar_allowsTyping() {
        composeRule
            .onNodeWithTag("newTripButton")
            .assertIsDisplayed()
            .performClick()

        composeRule
            .onNodeWithTag("SEARCH_TRIP_INPUT_FIELD")
            .assertExists()
            .performClick()

        val inputField =
            composeRule
                .onNodeWithTag("SEARCH_TRIP_INPUT_FIELD")
                .onChild()
                .assertExists()

        inputField.assert(hasText(""))

        inputField.performTextInput("31 января 2025")

        inputField.assert(hasText("31 января 2025"))
    }
}
