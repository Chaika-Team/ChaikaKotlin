package com.chaikasoft.app.domain.usecases.conductorUseCases

import com.chaikasoft.app.domain.models.ConductorDomain
import com.chaikasoft.app.domain.usecases.AuthorizeAndSaveConductorUseCase
import com.chaikasoft.app.domain.usecases.FetchConductorByTokenUseCase
import com.chaikasoft.app.domain.usecases.SaveConductorLocallyUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class AuthorizeAndSaveConductorUseCaseTest : FunSpec({

    lateinit var fetchConductorByTokenUseCase: FetchConductorByTokenUseCase
    lateinit var saveConductorLocallyUseCase: SaveConductorLocallyUseCase
    lateinit var useCase: AuthorizeAndSaveConductorUseCase

    val accessToken = "token-123"
    val remoteConductor = ConductorDomain(
        id = 7,
        name = "John",
        familyName = "Doe",
        givenName = "John",
        employeeID = "123",
        image = "https://example.test/avatar.jpg",
    )
    val localConductor = remoteConductor.copy(image = "files/conductors/123.jpg")

    beforeTest {
        fetchConductorByTokenUseCase = mockk()
        saveConductorLocallyUseCase = mockk()
        useCase = AuthorizeAndSaveConductorUseCase(fetchConductorByTokenUseCase, saveConductorLocallyUseCase)
    }

    /**
     * Test design technique: #1 Equivalence classes
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Input class: fetch returns conductor and save succeeds.
     *   - Expected behavior:
     *       1) use case returns the updated conductor,
     *       2) fetch and save are called once with expected args.
     *   - Goal: ensure the use case wires fetch -> save and returns the saved model.
     */
    test("when fetch succeeds - saves conductor locally and returns updated conductor") {
        runTest {
            coEvery { fetchConductorByTokenUseCase(accessToken) } returns remoteConductor
            coEvery { saveConductorLocallyUseCase(remoteConductor, remoteConductor.image) } returns localConductor

            val result = useCase(accessToken)

            result shouldBe localConductor
            coVerify(exactly = 1) { fetchConductorByTokenUseCase(accessToken) }
            coVerify(exactly = 1) { saveConductorLocallyUseCase(remoteConductor, remoteConductor.image) }
            confirmVerified(fetchConductorByTokenUseCase, saveConductorLocallyUseCase)
        }
    }

    /**
     * Test design technique: #5 Error guessing
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Input class: fetch throws an error.
     *   - Expected behavior:
     *       1) use case rethrows the error,
     *       2) save is not invoked.
     *   - Goal: keep fetch failures visible and avoid local side effects.
     */
    test("when fetch fails - rethrows error and does not save locally") {
        runTest {
            val error = IllegalStateException("network error")
            coEvery { fetchConductorByTokenUseCase(accessToken) } throws error

            val thrown = shouldThrow<IllegalStateException> { useCase(accessToken) }

            thrown.message shouldBe error.message
            coVerify(exactly = 1) { fetchConductorByTokenUseCase(accessToken) }
            coVerify(exactly = 0) { saveConductorLocallyUseCase(any(), any()) }
            confirmVerified(fetchConductorByTokenUseCase, saveConductorLocallyUseCase)
        }
    }

    /**
     * Test design technique: #5 Error guessing
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Input class: save throws an error after successful fetch.
     *   - Expected behavior:
     *       1) use case rethrows the error,
     *       2) fetch is called once and save is attempted once.
     *   - Goal: ensure local save errors are not swallowed.
     */
    test("when save fails - rethrows error after fetch") {
        runTest {
            val error = IllegalArgumentException("save failed")
            coEvery { fetchConductorByTokenUseCase(accessToken) } returns remoteConductor
            coEvery { saveConductorLocallyUseCase(remoteConductor, remoteConductor.image) } throws error

            val thrown = shouldThrow<IllegalArgumentException> { useCase(accessToken) }

            thrown.message shouldBe error.message
            coVerify(exactly = 1) { fetchConductorByTokenUseCase(accessToken) }
            coVerify(exactly = 1) { saveConductorLocallyUseCase(remoteConductor, remoteConductor.image) }
            confirmVerified(fetchConductorByTokenUseCase, saveConductorLocallyUseCase)
        }
    }
})
