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
        // 1) Открываем экран NewTrip
        composeRule
            .onNodeWithTag("newTripButton")
            .assertIsDisplayed()
            .performClick()

        // 2) Вводим «са» в поле станции отправки
        composeRule
            .onNodeWithTag("startStationField")
            .assertIsDisplayed()
            .performTextInput("са")

        // 3) Ждём пока дебаунс (300 мс) сработает и меню появится
        composeRule.waitUntil(
            condition = {
                // найдётся хотя бы один узел с нужным текстом
                composeRule.onAllNodesWithText("СУМСКИЙ ПОСАД").fetchSemanticsNodes().isNotEmpty()
            },
            timeoutMillis = 5_000L
        )

        // 4) Теперь проверяем, что нужная подсказка отображается
        composeRule
            .onNodeWithText("СУМСКИЙ ПОСАД")
            .assertIsDisplayed()
    }

}
