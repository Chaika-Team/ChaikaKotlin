package com.example.chaika.data.room.entities

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

/**
 * Тесты для сущности Conductor.
 */
class ConductorTest {
    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для создания объекта Conductor с типичными значениями.
     *   - Классы эквивалентности: корректные данные для всех полей, id по умолчанию должно быть 0.
     */
    @Test
    fun testConductorCreation() {
        val conductor =
            Conductor(
                name = "Ivan",
                familyName = "Ivanov",
                givenName = "Ivanovich",
                employeeID = "EMP001",
                image = "ivan.png",
            )
        assertEquals(0, conductor.id)
        assertEquals("Ivan", conductor.name)
        assertEquals("Ivanov", conductor.familyName)
        assertEquals("Ivanovich", conductor.givenName)
        assertEquals("EMP001", conductor.employeeID)
        assertEquals("ivan.png", conductor.image)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для проверки метода equals() и hashCode() для Conductor.
     *   - Объекты с одинаковыми полями должны быть равны, а с хотя бы одним отличающимся – не равны.
     */
    @Test
    fun testConductorEquality() {
        val conductor1 =
            Conductor(
                name = "Anna",
                familyName = "Petrova",
                givenName = "Anastasia",
                employeeID = "EMP002",
                image = "anna.png",
            )
        val conductor2 =
            Conductor(
                name = "Anna",
                familyName = "Petrova",
                givenName = "Anastasia",
                employeeID = "EMP002",
                image = "anna.png",
            )
        val conductorDifferent =
            Conductor(
                name = "Anna",
                familyName = "Petrova",
                givenName = "Elena", // отличное значение
                employeeID = "EMP002",
                image = "anna.png",
            )
        assertEquals(conductor1, conductor2)
        assertEquals(conductor1.hashCode(), conductor2.hashCode())
        assertNotEquals(conductor1, conductorDifferent)
    }
}
