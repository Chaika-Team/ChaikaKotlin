package com.chaikasoft.app.startup

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class PostAuthStartupSeamTest : FunSpec({

    test("normal seam delegates to coordinator") {
        runTest {
            val coordinator = mockk<PostAuthStartupCoordinator>()
            val outcome = PostAuthStartupOutcome(hadRefreshFailure = true)
            coEvery { coordinator.prepare() } returns outcome

            val result = NormalPostAuthStartupSeam(coordinator).prepareForAuthenticatedApp()

            result shouldBe outcome
            coVerify(exactly = 1) { coordinator.prepare() }
        }
    }

    test("disabled seam immediately reports success") {
        runTest {
            DisabledPostAuthStartupSeam()
                .prepareForAuthenticatedApp() shouldBe
                PostAuthStartupOutcome(hadRefreshFailure = false)
        }
    }

    test("fake success seam immediately reports success") {
        runTest {
            FakeSuccessPostAuthStartupSeam()
                .prepareForAuthenticatedApp() shouldBe
                PostAuthStartupOutcome(hadRefreshFailure = false)
        }
    }
})
