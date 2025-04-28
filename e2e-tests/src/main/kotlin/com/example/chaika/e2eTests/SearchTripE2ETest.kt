package com.example.chaika.e2eTests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.chaika.ui.activities.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SearchTripE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun typingDateAndStations_showsTrips() {
        // 1) Открываем NewTrip
        composeRule.onNodeWithTag("newTripButton")
            .assertIsDisplayed()
            .performClick()

        // 2) Фокусируемся на SearchBar-контейнере и кликаем
        composeRule.onNodeWithTag("SEARCH_TRIP_INPUT_FIELD")
            .assertIsDisplayed()
            .performClick()

        // 3) Спускаемся на реальный TextField и вводим дату
        val dateInput = composeRule
            .onNodeWithTag("SEARCH_TRIP_INPUT_FIELD")
            .onChild()               // ваш внутренний текстовый узел
            .assertIsDisplayed()

        dateInput.performTextInput("2025-04-29")
        dateInput.assertTextEquals("2025-04-29")

        // 5) Станция отправки
        composeRule.onNodeWithTag("startStationField")
            .performClick()
            .performTextInput("Пе")
        composeRule.waitUntil(
            condition = {
                composeRule
                    .onAllNodesWithText("ПЕТРОЗАВОДСК")
                    .fetchSemanticsNodes().isNotEmpty()
            }, timeoutMillis = 5_000L
        )
        composeRule.onAllNodesWithText("ПЕТРОЗАВОДСК")
            .onFirst()
            .performClick()

        // 6) Станция прибытия
        composeRule.onNodeWithTag("finishStationField")
            .performClick()
            .performTextInput("Ме")
        composeRule.waitUntil(
            condition = {
                composeRule
                    .onAllNodesWithText("МЕДВЕЖЬЯ ГОРА")
                    .fetchSemanticsNodes().isNotEmpty()
            }, timeoutMillis = 5_000L
        )
        composeRule.onAllNodesWithText("МЕДВЕЖЬЯ ГОРА")
            .onFirst()
            .performClick()

        composeRule.waitUntil(
            condition = {
                composeRule
                    .onAllNodesWithText("6372")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            },
            timeoutMillis = 10_000L
        )

        // И проверяем, что она действительно отображается
        composeRule
            .onAllNodesWithText("6372")
            .onFirst()
            .assertIsDisplayed()
    }
}
