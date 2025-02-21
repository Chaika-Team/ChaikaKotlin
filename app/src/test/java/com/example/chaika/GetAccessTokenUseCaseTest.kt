package com.example.chaika

import com.example.chaika.data.crypto.FakeEncryptedTokenManager
import com.example.chaika.domain.usecases.GetAccessTokenUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAccessTokenUseCaseTest {

    // Создаём фиктивную реализацию зависимости
    private val fakeTokenManager = FakeEncryptedTokenManager()

    // Инстанцируем use case, передавая в конструктор fakeTokenManager.
    // В реальном приложении Hilt бы подставил нужную реализацию.
    private val getAccessTokenUseCase = GetAccessTokenUseCase(fakeTokenManager)

    @Test
    fun `when token is available, use case returns token`() = runBlocking {
        // Вызываем use case
        val token = getAccessTokenUseCase()

        // Проверяем, что возвращённый токен соответствует тому, что возвращает Fake
        assertEquals("fake_token", token)
    }
}
