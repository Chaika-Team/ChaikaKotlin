package com.example.chaika.e2eTests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
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

/**
 * E2E–тест сценария «предложение станций» на экране NewTrip.
 */
@HiltAndroidTest
class SuggestStationsE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun typingSa_showsSumskiyPosad() {
        // 1) Открываем экран создания новой поездки
        composeRule
            .onNodeWithTag("newTripButton")
            .assertIsDisplayed()
            .performClick()

        // 2) Фокусируемся на поле «Станция отправки» и вводим «са»
        composeRule
            .onNodeWithTag("startStationField")
            .assertIsDisplayed()
            .performClick()            // запросить фокус
            .performTextInput("са")    // вводим текст

        // 3) Дождаться debounce и появления хотя бы одной подсказки
        composeRule.waitUntil(
            condition = {
                composeRule
                    .onAllNodesWithText("СУМСКИЙ ПОСАД")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            },
            timeoutMillis = 5_000L
        )

        // 4) Проверить, что в списке подсказок есть нужный город
        composeRule
            .onNodeWithText("СУМСКИЙ ПОСАД")
            .assertIsDisplayed()
    }
}
