package com.example.chaika.e2eTests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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

    // 0. HiltRule первым
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // 1. ComposeTestRule поднимет MainActivity и инициализирует Compose
    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        // Не забываем инжектить до старта активности
        hiltRule.inject()
    }

    @Test
    fun typingSa_showsSaintPetersburg() {
        // 1) Кликаем по кнопке "Новая поездка", чтобы попасть в экран выбора станций
        composeRule
            .onNodeWithTag("newTripButton")
            .assertIsDisplayed()
            .performClick()

        // 2) Вводим "са" в поле отправки
        composeRule
            .onNodeWithTag("startStationField")
            .assertIsDisplayed()
            .performTextInput("са")

        // 3) Проверяем, что среди подсказок есть "Санкт-Петербург"
        composeRule
            .onNodeWithText("Санкт-Петербург")
            .assertIsDisplayed()
    }
}
