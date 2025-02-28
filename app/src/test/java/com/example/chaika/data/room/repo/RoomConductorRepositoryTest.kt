package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.ConductorDao
import com.example.chaika.data.room.entities.Conductor
import com.example.chaika.domain.models.ConductorDomain
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class RoomConductorRepositoryTest {
    private val conductorDao = mock(ConductorDao::class.java)
    private val repository = RoomConductorRepository(conductorDao)

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для getAllConductors, проверяется корректное маппирование сущностей в ConductorDomain.
     */
    @Test
    fun testGetAllConductors_positive() =
        runBlocking {
            // Arrange
            val conductors =
                listOf(
                    Conductor(
                        id = 1,
                        name = "Ivan",
                        familyName = "Ivanov",
                        givenName = "Ivanovich",
                        employeeID = "EMP001",
                        image = "ivan.png",
                    ),
                    Conductor(
                        id = 2,
                        name = "Anna",
                        familyName = "Petrova",
                        givenName = "Sergeevna",
                        employeeID = "EMP002",
                        image = "anna.png",
                    ),
                )
            whenever(conductorDao.getAllConductors()).thenReturn(flowOf(conductors))

            // Act
            val result: List<ConductorDomain> = repository.getAllConductors().first()

            // Assert
            assertEquals(2, result.size)
            assertEquals("Ivan", result[0].name)
            assertEquals("EMP002", result[1].employeeID)
        }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для getConductorByEmployeeID, когда проводник найден.
     */
    @Test
    fun testGetConductorByEmployeeID_found() =
        runBlocking {
            // Arrange
            val conductor =
                Conductor(
                    id = 3,
                    name = "Petr",
                    familyName = "Petrov",
                    givenName = "Petrovich",
                    employeeID = "EMP003",
                    image = "petr.png",
                )
            whenever(conductorDao.getConductorByEmployeeID("EMP003")).thenReturn(conductor)

            // Act
            val result = repository.getConductorByEmployeeID("EMP003")

            // Assert
            assertNotNull(result)
            assertEquals("Petr", result?.name)
        }

    /**
     * Техника тест-дизайна: #4 Прогнозирование ошибок
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для getConductorByEmployeeID, когда проводник не найден.
     */
    @Test
    fun testGetConductorByEmployeeID_notFound() =
        runBlocking {
            // Arrange
            whenever(conductorDao.getConductorByEmployeeID("EMP999")).thenReturn(null)

            // Act
            val result = repository.getConductorByEmployeeID("EMP999")

            // Assert
            assertNull(result)
        }

    /**
     * Техника тест-дизайна: #2 Граничные значения / #4 Прогнозирование ошибок
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для getEmployeeIDByConductorId, когда проводник найден и когда нет.
     */
    @Test
    fun testGetEmployeeIDByConductorId(): Unit =
        runBlocking {
            // Arrange
            val conductors =
                listOf(
                    Conductor(
                        id = 10,
                        name = "Alex",
                        familyName = "Alexandrov",
                        givenName = "Alex",
                        employeeID = "EMP010",
                        image = "alex.png",
                    ),
                )
            whenever(conductorDao.getAllConductors()).thenReturn(flowOf(conductors))

            // Act & Assert (позитивный случай)
            val employeeID = repository.getEmployeeIDByConductorId(10)
            assertEquals("EMP010", employeeID)

            // Act & Assert (негативный случай)
            assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repository.getEmployeeIDByConductorId(999) }
            }
        }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для insertConductor, updateConductor, deleteConductor и deleteAllConductors.
     *   - Проверяется, что соответствующие методы DAO вызываются с преобразованными данными.
     */
    @Test
    fun testConductorDaoCalls() =
        runBlocking {
            // Arrange
            val conductorDomain =
                ConductorDomain(
                    id = null,
                    name = "Maria",
                    familyName = "Ivanova",
                    givenName = "Mariich",
                    employeeID = "EMP011",
                    image = "maria.png",
                )
            // Act
            repository.insertConductor(conductorDomain)
            repository.updateConductor(conductorDomain)
            repository.deleteConductor(conductorDomain)
            repository.deleteAllConductors()

            // Assert – используем явные типы для any<>:
            verify(conductorDao, times(1)).insertConductor(any())
            verify(conductorDao, times(1)).updateConductor(any())
            verify(conductorDao, times(1)).deleteConductor(any())
            verify(conductorDao, times(1)).deleteAllConductors()
        }
}
