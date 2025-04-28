package com.example.chaika.e2eTests

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.chaika.ui.activities.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class LoadProductE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun openProductList_displaysAtLeastOneProduct() {
        // 1) Дождаться появления иконки "товары"
        // кликаем по вкладке «товары»
        composeRule.onNodeWithTag("productTab").performClick()

// кликаем «View products»
        composeRule.onNodeWithTag("viewProductsButton").performClick()

// ждём пока исчезнет индикатор загрузки
        composeRule.waitUntil(timeoutMillis = 10_000L) {
            // если индикатор больше не виден
            composeRule.onAllNodes(hasTestTag("productSpinner"))
                .fetchSemanticsNodes().isEmpty()
        }

// а затем убеждаемся, что хотя бы одна карточка продукта отрисована
        composeRule
            .onAllNodesWithContentDescription("Add to cart")
            .onFirst()
            .assertIsDisplayed()

    }
}
