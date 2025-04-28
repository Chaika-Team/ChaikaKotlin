package com.example.chaika.e2eTests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onFirst
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
class StartAndFinishTripE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() = hiltRule.inject()

    @Test
    fun startTrip_then_finishTrip_returnsToNewTrip() {
        // === 1. Тут повторяем все шаги из SearchTripE2ETest до выбора вагона ===

        // Нажать “Новая поездка”
        composeRule.onNodeWithTag("newTripButton")
            .assertIsDisplayed()
            .performClick()

        // Ввести дату
        val dateInput = composeRule
            .onNodeWithTag("SEARCH_TRIP_INPUT_FIELD")
            .performClick()
            .onChild()
        dateInput.performTextInput("2025-04-29")

        // Стартовая станция
        composeRule.onNodeWithTag("startStationField")
            .performClick().performTextInput("Пе")
        composeRule.waitUntil(condition = {
            composeRule
                .onAllNodesWithText("ПЕТРОЗАВОДСК")
                .fetchSemanticsNodes().isNotEmpty()
        }, timeoutMillis = 5_000L)
        composeRule.onAllNodesWithText("ПЕТРОЗАВОДСК").onFirst().performClick()

        // Конечная станция
        composeRule.onNodeWithTag("finishStationField")
            .performClick().performTextInput("Ме")
        composeRule.waitUntil(condition = {
            composeRule
                .onAllNodesWithText("МЕДВЕЖЬЯ ГОРА")
                .fetchSemanticsNodes().isNotEmpty()
        }, timeoutMillis = 5_000L)
        composeRule.onAllNodesWithText("МЕДВЕЖЬЯ ГОРА").onFirst().performClick()

        // Ждём и кликаем первый найденный рейс
        composeRule.waitUntil(condition = {
            composeRule
                .onAllNodesWithText("6372")
                .fetchSemanticsNodes().isNotEmpty()
        }, timeoutMillis = 10_000L)
        composeRule.onAllNodesWithText("6372").onFirst().performClick()

        // === 2. Проверяем, что попали на экран выбора вагона ===
        composeRule.onNodeWithTag("carriageList")
            .assertIsDisplayed()
        composeRule.onAllNodesWithTag("carriageCard")
            .onFirst()
            .performClick()     // выбираем первый вагон

        // === 3. Проверяем, что появилась кнопка “Завершить смену” ===
        composeRule.onNodeWithTag("finishTripButton")
            .assertIsDisplayed()
            .performClick()

        // === 4. В результате снова видим кнопку “Новая поездка” ===
        composeRule.onNodeWithTag("newTripButton")
            .assertIsDisplayed()
    }
}
