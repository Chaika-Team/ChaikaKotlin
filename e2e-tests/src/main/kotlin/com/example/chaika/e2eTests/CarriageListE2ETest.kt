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
class CarriageListE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun searchTrip_and_then_showCarriages() {
        // -- Повторяем все шаги из SearchTripE2ETest до клика по найденному рейсу --

        // 1) Нажать «Новая поездка»
        composeRule.onNodeWithTag("newTripButton")
            .assertIsDisplayed()
            .performClick()

        // 2) Ввести дату
        val dateInput = composeRule.onNodeWithTag("SEARCH_TRIP_INPUT_FIELD")
            .performClick()
            .onChild()
        dateInput.performTextInput("2025-04-29")

        // 3) Стартовая станция
        composeRule.onNodeWithTag("startStationField")
            .performClick()
            .performTextInput("Пе")
        composeRule.waitUntil(condition = {
            composeRule.onAllNodesWithText("ПЕТРОЗАВОДСК")
                .fetchSemanticsNodes().isNotEmpty()
        }, timeoutMillis = 5_000L)
        composeRule.onAllNodesWithText("ПЕТРОЗАВОДСК")
            .onFirst()
            .performClick()

        // 4) Конечная станция
        composeRule.onNodeWithTag("finishStationField")
            .performClick()
            .performTextInput("Ме")
        composeRule.waitUntil(condition = {
            composeRule.onAllNodesWithText("МЕДВЕЖЬЯ ГОРА")
                .fetchSemanticsNodes().isNotEmpty()
        }, timeoutMillis = 5_000L)
        composeRule.onAllNodesWithText("МЕДВЕЖЬЯ ГОРА")
            .onFirst()
            .performClick()

        // 5) Ждём, пока карточки рейсов появятся, и кликаем первую
        composeRule.waitUntil(condition = {
            composeRule.onAllNodesWithText("6372")
                .fetchSemanticsNodes().isNotEmpty()
        }, timeoutMillis = 10_000L)
        composeRule.onAllNodesWithText("6372")
            .onFirst()
            .performClick()

        // **Новый шаг**: проверяем, что на экране выбора вагона отображается список
        composeRule.onNodeWithTag("carriageList")
            .assertIsDisplayed()

        // и хотя бы одна карточка
        composeRule.onAllNodesWithTag("carriageCard")
            .onFirst()
            .assertIsDisplayed()
    }
}
