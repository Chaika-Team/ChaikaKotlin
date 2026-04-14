package com.chaikasoft.app.domain.usecases.conductorUseCases

import com.chaikasoft.app.data.datasource.repo.IAMApiServiceRepositoryInterface
import com.chaikasoft.app.domain.models.ConductorDomain
import com.chaikasoft.app.domain.usecases.FetchConductorByTokenUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

class FetchConductorByTokenUseCaseTest : FunSpec({

    lateinit var conductorApiRepository: IAMApiServiceRepositoryInterface
    lateinit var useCase: FetchConductorByTokenUseCase

    val accessToken = "token-123"
    val conductor = ConductorDomain(
        id = 7,
        name = "John",
        familyName = "Doe",
        givenName = "John",
        employeeID = "123",
        image = "https://example.test/avatar.jpg",
    )

    beforeTest {
        conductorApiRepository = mockk()
        useCase = FetchConductorByTokenUseCase(
            conductorApiRepository = conductorApiRepository,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    /**
     * Test design technique: #1 Equivalence classes
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Input class: repository succeeds with ConductorDomain.
     *   - Expected behavior:
     *       1) use case returns the same conductor,
     *       2) repository is called once.
     *   - Goal: protect the rule "successful repository result is forwarded".
     */
    test("when repository returns success - returns conductor") {
        runTest {
            coEvery { conductorApiRepository.fetchUserInfo(accessToken) } returns Result.success(conductor)

            val result = useCase(accessToken)

            result shouldBe conductor
            coVerify(exactly = 1) { conductorApiRepository.fetchUserInfo(accessToken) }
            confirmVerified(conductorApiRepository)
        }
    }

    /**
     * Test design technique: #5 Error guessing / Equivalence classes
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Input class: repository returns failure.
     *   - Expected behavior:
     *       1) use case throws the same error,
     *       2) repository is called once.
     *   - Goal: keep error propagation intact for network and server errors.
     */
    test("when repository returns failure - throws error") {
        runTest {
            val error = IllegalStateException("network error")
            coEvery { conductorApiRepository.fetchUserInfo(accessToken) } returns Result.failure(error)

            val thrown = shouldThrow<IllegalStateException> { useCase(accessToken) }

            thrown.message shouldBe error.message
            coVerify(exactly = 1) { conductorApiRepository.fetchUserInfo(accessToken) }
            confirmVerified(conductorApiRepository)
        }
    }
})
