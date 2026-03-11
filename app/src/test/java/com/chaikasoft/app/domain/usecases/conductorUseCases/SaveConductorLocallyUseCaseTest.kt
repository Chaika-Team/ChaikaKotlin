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
        useCase = SaveConductorLocallyUseCase(conductorRepository, imageRepository)
    }

    /**
     * Test design technique: #1 Equivalence classes
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Input class: image saved successfully and conductor is found after insert.
     *   - Expected behavior:
     *       1) use case returns updated conductor,
     *       2) repository inserts conductor with updated image,
     *       3) repository lookup returns the stored entity.
     *   - Goal: keep the happy path stable across refactors.
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
     * Test design technique: #5 Error guessing / Equivalence classes
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Input class: image saving fails (null result).
     *   - Expected behavior:
     *       1) use case throws IllegalArgumentException,
     *       2) no database calls are made.
     *   - Goal: prevent accidental DB writes when image download fails.
     */
    test("when image save fails - throws IllegalArgumentException and does not touch DB") {
        runTest {
            coEvery {
                imageRepository.saveImageFromUrl(
                    imageUrl = imageUrl,
                    fileName = "${conductor.employeeID}.jpg",
                    subDir = ImageSubDir.CONDUCTORS.folder,
                )
            } returns null

            shouldThrow<IllegalArgumentException> { useCase(conductor, imageUrl) }

            coVerify(exactly = 1) {
                imageRepository.saveImageFromUrl(
                    imageUrl = imageUrl,
                    fileName = "${conductor.employeeID}.jpg",
                    subDir = ImageSubDir.CONDUCTORS.folder,
                )
            }
            coVerify(exactly = 0) { conductorRepository.insertConductor(any()) }
            coVerify(exactly = 0) { conductorRepository.getConductorByEmployeeID(any()) }
            confirmVerified(conductorRepository, imageRepository)
        }
    }

    /**
     * Test design technique: #5 Error guessing
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Input class: conductor inserted, but lookup returns null.
     *   - Expected behavior:
     *       1) use case throws IllegalStateException,
     *       2) insert is attempted exactly once.
     *   - Goal: ensure "insert then read-back" invariant is enforced.
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

    /**
     * Test design technique: #2 Boundary values
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Input class: use case receives an explicit imageUrl parameter.
     *   - Expected behavior:
     *       1) imageRepository.saveImageFromUrl uses the provided url,
     *       2) fileName is based on employeeID,
     *       3) subDir targets conductors.
     *   - Goal: protect the contract for image saving parameters.
     */
    test("saves image with correct parameters") {
        runTest {
            val explicitUrl = "https://example.test/explicit.png"
            val updatedConductor = conductor.copy(image = savedImagePath)

            coEvery {
                imageRepository.saveImageFromUrl(
                    imageUrl = explicitUrl,
                    fileName = "${conductor.employeeID}.jpg",
                    subDir = ImageSubDir.CONDUCTORS.folder,
                )
            } returns savedImagePath
            coEvery { conductorRepository.insertConductor(any()) } returns Unit
            coEvery { conductorRepository.getConductorByEmployeeID(conductor.employeeID) } returns updatedConductor

            val result = useCase(conductor, explicitUrl)

            result shouldBe updatedConductor
            coVerify(exactly = 1) {
                imageRepository.saveImageFromUrl(
                    imageUrl = explicitUrl,
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
