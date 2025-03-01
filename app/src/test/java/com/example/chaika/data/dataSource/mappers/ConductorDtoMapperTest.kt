package com.example.chaika.data.dataSource.mappers

import com.example.chaika.data.dataSource.dto.ConductorDto
import com.example.chaika.domain.models.ConductorDomain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Тесты для маппера ConductorDto.toDomain.
 */
class ConductorDtoMapperTest {
    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для маппера ConductorDto.toDomain, когда все поля заполнены, включая image.
     *   - Классы эквивалентности: все поля присутствуют, и image маппится напрямую.
     */
    @Test
    fun testToDomain_withImage() {
        // Arrange: создаем ConductorDto с заполненным полем image
        val dto =
            ConductorDto(
                name = "John",
                familyName = "Doe",
                givenName = "Johnny",
                nickname = "EMP123",
                image = "https://example.com/image.png",
            )
        // Act: преобразуем DTO в доменную модель
        val domain: ConductorDomain = dto.toDomain()

        // Assert: проверяем, что все поля корректно скопированы
        assertEquals(0, domain.id)
        assertEquals("John", domain.name)
        assertEquals("Doe", domain.familyName)
        assertEquals("Johnny", domain.givenName)
        // nickname маппится на employeeID
        assertEquals("EMP123", domain.employeeID)
        assertEquals("https://example.com/image.png", domain.image)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для маппера ConductorDto.toDomain, когда поле image равно null.
     *   - Граничные значения: если image отсутствует, подставляется дефолтное значение.
     */
    @Test
    fun testToDomain_withoutImage() {
        // Arrange: создаем ConductorDto без значения image (null)
        val dto =
            ConductorDto(
                name = "Alice",
                familyName = "Smith",
                givenName = "Ally",
                nickname = "EMP456",
                image = null,
            )
        // Act: преобразуем DTO в доменную модель
        val domain: ConductorDomain = dto.toDomain()

        // Assert: проверяем, что подставлено дефолтное значение для image
        assertEquals(0, domain.id)
        assertEquals("Alice", domain.name)
        assertEquals("Smith", domain.familyName)
        assertEquals("Ally", domain.givenName)
        assertEquals("EMP456", domain.employeeID)
        assertEquals(
            "https://i.pinimg.com/736x/5b/d3/e7/5bd3e779f192cb04cf35b859e0d50cbc.jpg",
            domain.image,
        )
    }
}
