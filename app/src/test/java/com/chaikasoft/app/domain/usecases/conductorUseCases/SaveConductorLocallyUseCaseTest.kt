package com.chaikasoft.app.domain.usecases.conductorUseCases

import com.chaikasoft.app.data.local.ImageSubDir
import com.chaikasoft.app.data.local.LocalImageRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomConductorRepositoryInterface
import com.chaikasoft.app.domain.models.ConductorDomain
import com.chaikasoft.app.domain.usecases.SaveConductorLocallyUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

class SaveConductorLocallyUseCaseTest : FunSpec({

    lateinit var conductorRepository: RoomConductorRepositoryInterface
    lateinit var imageRepository: LocalImageRepositoryInterface
    lateinit var useCase: SaveConductorLocallyUseCase

    val conductor = ConductorDomain(
        id = 7,
        name = "John",
        familyName = "Doe",
        givenName = "John",
        employeeID = "123",
        image = "https://example.test/old.jpg",
    )
    val imageUrl = "https://example.test/new.jpg"
    val savedImagePath = "files/conductors/123.jpg"

    beforeTest {
        conductorRepository = mockk()
        imageRepository = mockk()
        useCase = SaveConductorLocallyUseCase(
            conductorRepository = conductorRepository,
            imageRepository = imageRepository,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Вход: проводник с валидным remote URL, изображение успешно сохраняется локально.
     *   - Ожидаемое поведение: в БД вставляется проводник с локальным путём, затем возвращается сохранённая запись.
     *   - Цель: сохранить happy path локального сохранения аватара проводника.
     */
    test("when image saved and conductor found - returns updated conductor") {
        runTest {
            val updatedConductor = conductor.copy(image = savedImagePath)
            val insertSlot = slot<ConductorDomain>()

            coEvery {
                imageRepository.saveImageFromUrl(
                    imageUrl = imageUrl,
                    fileName = "${conductor.employeeID}.jpg",
                    subDir = ImageSubDir.CONDUCTORS.folder,
                )
            } returns savedImagePath
            coEvery { conductorRepository.insertConductor(capture(insertSlot)) } returns Unit
            coEvery { conductorRepository.getConductorByEmployeeID(conductor.employeeID) } returns updatedConductor

            val result = useCase(conductor, imageUrl)

            result shouldBe updatedConductor
            insertSlot.captured shouldBe updatedConductor
            coVerify(exactly = 1) {
                imageRepository.saveImageFromUrl(
                    imageUrl = imageUrl,
                    fileName = "${conductor.employeeID}.jpg",
                    subDir = ImageSubDir.CONDUCTORS.folder,
                )
            }
            coVerify(exactly = 1) { conductorRepository.insertConductor(any()) }
            coVerify(exactly = 1) { conductorRepository.getConductorByEmployeeID(conductor.employeeID) }
            confirmVerified(conductorRepository, imageRepository)
        }
    }

    /**
     * Техника тест-дизайна: #5 Error guessing
     *
     * Описание:
     *   - Вход: проводник с валидным remote URL, но LocalImageRepository возвращает null.
     *   - Ожидаемое поведение: проводник всё равно сохраняется, а image остаётся remote URL.
     *   - Цель: подтвердить best-effort поведение, чтобы ошибка изображения не ломала авторизацию.
     */
    test("when image save fails - keeps remote url and still writes conductor") {
        runTest {
            val updatedConductor = conductor.copy(image = imageUrl)
            val insertSlot = slot<ConductorDomain>()

            coEvery {
                imageRepository.saveImageFromUrl(
                    imageUrl = imageUrl,
                    fileName = "${conductor.employeeID}.jpg",
                    subDir = ImageSubDir.CONDUCTORS.folder,
                )
            } returns null
            coEvery { conductorRepository.insertConductor(capture(insertSlot)) } returns Unit
            coEvery { conductorRepository.getConductorByEmployeeID(conductor.employeeID) } returns updatedConductor

            val result = useCase(conductor, imageUrl)

            result shouldBe updatedConductor
            insertSlot.captured.image shouldBe imageUrl
            coVerify(exactly = 1) {
                imageRepository.saveImageFromUrl(
                    imageUrl = imageUrl,
                    fileName = "${conductor.employeeID}.jpg",
                    subDir = ImageSubDir.CONDUCTORS.folder,
                )
            }
            coVerify(exactly = 1) { conductorRepository.insertConductor(any()) }
            coVerify(exactly = 1) { conductorRepository.getConductorByEmployeeID(conductor.employeeID) }
            confirmVerified(conductorRepository, imageRepository)
        }
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Описание:
     *   - Вход: проводник с blank image.
     *   - Ожидаемое поведение: image repository не вызывается, в БД сохраняется пустая строка image.
     *   - Цель: исключить попытку скачать несуществующее изображение проводника.
     */
    test("when image is missing - does not call image repository and stores empty image") {
        runTest {
            val updatedConductor = conductor.copy(image = "")
            val insertSlot = slot<ConductorDomain>()

            coEvery { conductorRepository.insertConductor(capture(insertSlot)) } returns Unit
            coEvery { conductorRepository.getConductorByEmployeeID(conductor.employeeID) } returns updatedConductor

            val result = useCase(conductor, "   ")

            result shouldBe updatedConductor
            insertSlot.captured.image shouldBe ""
            coVerify(exactly = 0) { imageRepository.saveImageFromUrl(any(), any(), any()) }
            coVerify(exactly = 1) { conductorRepository.insertConductor(any()) }
            coVerify(exactly = 1) { conductorRepository.getConductorByEmployeeID(conductor.employeeID) }
            confirmVerified(conductorRepository, imageRepository)
        }
    }

    /**
     * Техника тест-дизайна: #5 Error guessing
     *
     * Описание:
     *   - Вход: изображение сохранено, insert выполнен, но повторное чтение проводника возвращает null.
     *   - Ожидаемое поведение: use case выбрасывает IllegalStateException.
     *   - Цель: защитить инвариант insert-then-read для локально сохранённого проводника.
     */
    test("when conductor missing after insert - throws IllegalStateException") {
        runTest {
            coEvery {
                imageRepository.saveImageFromUrl(
                    imageUrl = imageUrl,
                    fileName = "${conductor.employeeID}.jpg",
                    subDir = ImageSubDir.CONDUCTORS.folder,
                )
            } returns savedImagePath
            coEvery { conductorRepository.insertConductor(any()) } returns Unit
            coEvery { conductorRepository.getConductorByEmployeeID(conductor.employeeID) } returns null

            shouldThrow<IllegalStateException> { useCase(conductor, imageUrl) }

            coVerify(exactly = 1) {
                imageRepository.saveImageFromUrl(
                    imageUrl = imageUrl,
                    fileName = "${conductor.employeeID}.jpg",
                    subDir = ImageSubDir.CONDUCTORS.folder,
                )
            }
            coVerify(exactly = 1) { conductorRepository.insertConductor(any()) }
            coVerify(exactly = 1) { conductorRepository.getConductorByEmployeeID(conductor.employeeID) }
            confirmVerified(conductorRepository, imageRepository)
        }
    }
})
