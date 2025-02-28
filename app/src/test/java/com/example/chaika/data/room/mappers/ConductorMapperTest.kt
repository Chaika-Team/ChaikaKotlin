package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.Conductor
import com.example.chaika.domain.models.ConductorDomain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Тесты для мапперов проводника.
 */
class ConductorMapperTest {
    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для функции Conductor.toDomain.
     *   - Классы эквивалентности: корректное преобразование сущности Conductor в доменную модель ConductorDomain.
     */
    @Test
    fun testConductorToDomain() {
        // Arrange
        val conductorEntity =
            Conductor(
                id = 12,
                name = "Ivan",
                familyName = "Ivanov",
                givenName = "Ivanovich",
                employeeID = "EMP123",
                image = "ivan.png",
            )
        // Act
        val domain: ConductorDomain = conductorEntity.toDomain()
        // Assert: проверяем, что все поля корректно перенесены
        assertEquals(12, domain.id)
        assertEquals("Ivan", domain.name)
        assertEquals("Ivanov", domain.familyName)
        assertEquals("Ivanovich", domain.givenName)
        assertEquals("EMP123", domain.employeeID)
        assertEquals("ivan.png", domain.image)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для функции ConductorDomain.toEntity.
     *   - Граничные значения: проверка случая, когда id доменной модели равен null, и должен быть установлен в 0.
     */
    @Test
    fun testConductorDomainToEntity_NullId() {
        // Arrange
        val domain =
            ConductorDomain(
                id = null,
                name = "Anna",
                familyName = "Petrova",
                givenName = "Sergeevna",
                employeeID = "EMP456",
                image = "anna.png",
            )
        // Act
        val entity: Conductor = domain.toEntity()
        // Assert: если id null, то должен вернуться 0
        assertEquals(0, entity.id)
        assertEquals("Anna", entity.name)
        assertEquals("Petrova", entity.familyName)
        assertEquals("Sergeevna", entity.givenName)
        assertEquals("EMP456", entity.employeeID)
        assertEquals("anna.png", entity.image)
    }
}
