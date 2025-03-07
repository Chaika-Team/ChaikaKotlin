@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.chaika.domain.usecases

import com.example.chaika.data.dataSource.repo.IAMApiServiceRepositoryInterface
import com.example.chaika.data.local.ImageSubDir
import com.example.chaika.data.local.LocalImageRepositoryInterface
import com.example.chaika.data.room.repo.RoomConductorRepositoryInterface
import com.example.chaika.domain.models.ConductorDomain
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ConductorUseCasesTest {

    @Mock
    lateinit var conductorApiRepository: IAMApiServiceRepositoryInterface

    @Mock
    lateinit var conductorRepository: RoomConductorRepositoryInterface

    @Mock
    lateinit var imageRepository: LocalImageRepositoryInterface

    // Dummy данные для тестов:
    private val dummyConductor = ConductorDomain(
        id = 1,
        name = "John",
        familyName = "Doe",
        givenName = "John",
        employeeID = "123",
        image = "old_image_path"
    )

    private val updatedImagePath = "new_image_path"

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для FetchConductorByTokenUseCase.
     *   - Проверяется, что при успешном вызове репозитория use case возвращает данные проводника.
     */
    @Test
    fun `FetchConductorByTokenUseCase returns conductor when repository succeeds`() = runTest {
        val accessToken = "token123"
        whenever(conductorApiRepository.fetchUserInfo(accessToken))
            .thenReturn(Result.success(dummyConductor))
        val fetchUseCase = FetchConductorByTokenUseCase(conductorApiRepository)
        val result = fetchUseCase.invoke(accessToken)
        assertEquals(dummyConductor, result)
    }

    /**
     * Техника тест-дизайна: Прогнозирование ошибок / Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для FetchConductorByTokenUseCase.
     *   - Проверяется, что при ошибке в репозитории use case выбрасывает исключение с корректным сообщением.
     */
    @Test
    fun `FetchConductorByTokenUseCase throws exception when repository fails`() = runTest {
        val accessToken = "token123"
        val errorMessage = "Network error"
        whenever(conductorApiRepository.fetchUserInfo(accessToken))
            .thenReturn(Result.failure(Exception(errorMessage)))
        val fetchUseCase = FetchConductorByTokenUseCase(conductorApiRepository)
        val exception = assertThrows<Exception> {
            fetchUseCase.invoke(accessToken)
        }
        assertEquals(errorMessage, exception.message)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для SaveConductorLocallyUseCase.
     *   - Проверяется, что если сохранение изображения происходит успешно, use case возвращает обновлённого проводника
     *     с изменённым полем image и вызывает insertConductor.
     */
    @Test
    fun `SaveConductorLocallyUseCase returns updated conductor when image is saved successfully`() =
        runTest {
            whenever(
                imageRepository.saveImageFromUrl(
                    imageUrl = dummyConductor.image,
                    fileName = "${dummyConductor.employeeID}.jpg",
                    subDir = ImageSubDir.CONDUCTORS.folder
                )
            ).thenReturn(updatedImagePath)
            val saveUseCase = SaveConductorLocallyUseCase(conductorRepository, imageRepository)
            val updatedConductor = saveUseCase.invoke(dummyConductor, dummyConductor.image)
            assertEquals(updatedImagePath, updatedConductor.image)
            verify(conductorRepository).insertConductor(updatedConductor)
        }

    /**
     * Техника тест-дизайна: Прогнозирование ошибок / Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для SaveConductorLocallyUseCase.
     *   - Проверяется, что если сохранение изображения возвращает null, use case выбрасывает IllegalArgumentException.
     */
    @Test
    fun `SaveConductorLocallyUseCase throws exception when image saving fails`() = runTest {
        whenever(
            imageRepository.saveImageFromUrl(
                imageUrl = dummyConductor.image,
                fileName = "${dummyConductor.employeeID}.jpg",
                subDir = ImageSubDir.CONDUCTORS.folder
            )
        ).thenReturn(null)
        val saveUseCase = SaveConductorLocallyUseCase(conductorRepository, imageRepository)
        val exception = assertThrows<IllegalArgumentException> {
            saveUseCase.invoke(dummyConductor, dummyConductor.image)
        }
        assertEquals("Не удалось сохранить изображение проводника", exception.message)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для AuthorizeAndSaveConductorUseCase.
     *   - Проверяется, что use case получает данные проводника с сервера и успешно сохраняет их локально,
     *     возвращая обновлённого проводника с изменённым полем image.
     */
    @Test
    fun `AuthorizeAndSaveConductorUseCase returns updated conductor`() = runTest {
        val accessToken = "token123"
        val fetchUseCase = FetchConductorByTokenUseCase(conductorApiRepository)
        whenever(conductorApiRepository.fetchUserInfo(accessToken))
            .thenReturn(Result.success(dummyConductor))
        val saveUseCase = SaveConductorLocallyUseCase(conductorRepository, imageRepository)
        whenever(
            imageRepository.saveImageFromUrl(
                imageUrl = dummyConductor.image,
                fileName = "${dummyConductor.employeeID}.jpg",
                subDir = ImageSubDir.CONDUCTORS.folder
            )
        ).thenReturn(updatedImagePath)
        // Мокаем вставку проводника – проверка вызова выполняется через verify
        whenever(conductorRepository.insertConductor(any())).then { }
        val authorizeUseCase = AuthorizeAndSaveConductorUseCase(fetchUseCase, saveUseCase)
        val result = authorizeUseCase.invoke(accessToken)
        assertEquals(updatedImagePath, result.image)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для GetAllConductorsUseCase.
     *   - Проверяется, что use case возвращает Flow со списком проводников, полученным из репозитория.
     */
    @Test
    fun `GetAllConductorsUseCase returns flow of conductors`() {
        val flowOfConductors = flowOf(listOf(dummyConductor))
        whenever(conductorRepository.getAllConductors()).thenReturn(flowOfConductors)

        val getAllUseCase = GetAllConductorsUseCase(conductorRepository)
        val result = getAllUseCase.invoke()
        runTest {
            val list = result.first()
            assertEquals(listOf(dummyConductor), list)
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для DeleteAllConductorsUseCase.
     *   - Проверяется, что use case вызывает метод deleteAllConductors() у репозитория.
     */
    @Test
    fun `DeleteAllConductorsUseCase calls deleteAllConductors`() = runTest {
        val deleteUseCase = DeleteAllConductorsUseCase(conductorRepository)
        deleteUseCase.invoke()
        verify(conductorRepository).deleteAllConductors()
    }
}
