@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.chaika.ui.viewModels

import android.content.Intent
import com.example.chaika.testUtils.InstantTaskExecutorExtension
import com.example.chaika.testUtils.getOrAwaitValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class DeepLinkViewModelTest {

    @Suppress("unused")
    @JvmField
    @RegisterExtension
    val instantTaskExecutorExtension = InstantTaskExecutorExtension()

    private lateinit var viewModel: DeepLinkViewModel

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Проверяется корректность работы метода postDeepLink.
     *   - Класс эквивалентности: передаётся валидный Intent.
     *   - Ожидается, что deepLinkIntent будет обновлён переданным значением.
     */
    @Test
    fun `postDeepLink updates deepLinkIntent`() {
        viewModel = DeepLinkViewModel()
        val testIntent = Intent("action_test")
        viewModel.postDeepLink(testIntent)
        val actual = viewModel.deepLinkIntent.getOrAwaitValue()
        assertEquals(testIntent, actual)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Проверяется корректность работы метода clearDeepLink.
     *   - Класс эквивалентности: вызов метода после postDeepLink.
     *   - Ожидается, что deepLinkIntent будет сброшен в null.
     */
    @Test
    fun `clearDeepLink sets deepLinkIntent to null`() {
        viewModel = DeepLinkViewModel()
        viewModel.postDeepLink(Intent("action_test"))
        viewModel.clearDeepLink()
        val actual = viewModel.deepLinkIntent.getOrAwaitValue()
        assertNull(actual)
    }
}
