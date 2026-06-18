package com.chaikasoft.app.startup

import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.sealed.RefreshProductsResult
import com.chaikasoft.app.domain.sealed.RefreshStationsResult
import com.chaikasoft.app.domain.usecases.RefreshProductsOnLaunchUseCase
import com.chaikasoft.app.domain.usecases.RefreshStationsOnLaunchUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class PostAuthStartupCoordinatorTest : FunSpec({

    lateinit var refreshStationsOnLaunchUseCase: RefreshStationsOnLaunchUseCase
    lateinit var refreshProductsOnLaunchUseCase: RefreshProductsOnLaunchUseCase
    lateinit var coordinator: PostAuthStartupCoordinator

    beforeTest {
        refreshStationsOnLaunchUseCase = mockk()
        refreshProductsOnLaunchUseCase = mockk()
        coordinator = PostAuthStartupCoordinator(
            refreshStationsOnLaunchUseCase = refreshStationsOnLaunchUseCase,
            refreshProductsOnLaunchUseCase = refreshProductsOnLaunchUseCase
        )
    }

    test("prepare returns no failure when refreshes succeed or skip") {
        runTest {
            coEvery { refreshStationsOnLaunchUseCase() } returns
                RefreshStationsResult.Success(stationCount = 2)
            coEvery { refreshProductsOnLaunchUseCase() } returns
                RefreshProductsResult.SkippedFreshCache

            val result = coordinator.prepare()

            result shouldBe PostAuthStartupOutcome(hadRefreshFailure = false)
            coVerifyOrder {
                refreshStationsOnLaunchUseCase()
                refreshProductsOnLaunchUseCase()
            }
        }
    }

    test("prepare treats active shift skips as successful readiness") {
        runTest {
            coEvery { refreshStationsOnLaunchUseCase() } returns
                RefreshStationsResult.SkippedActiveShift
            coEvery { refreshProductsOnLaunchUseCase() } returns
                RefreshProductsResult.SkippedActiveShift

            val result = coordinator.prepare()

            result shouldBe PostAuthStartupOutcome(hadRefreshFailure = false)
        }
    }

    test("prepare still refreshes products when stations fail") {
        runTest {
            coEvery { refreshStationsOnLaunchUseCase() } returns
                RefreshStationsResult.RemoteFailure(AppError.Network())
            coEvery { refreshProductsOnLaunchUseCase() } returns
                RefreshProductsResult.Success(productCount = 3)

            val result = coordinator.prepare()

            result shouldBe PostAuthStartupOutcome(hadRefreshFailure = true)
            coVerifyOrder {
                refreshStationsOnLaunchUseCase()
                refreshProductsOnLaunchUseCase()
            }
        }
    }

    test("prepare reports failure when products fail") {
        runTest {
            val error = IllegalStateException("db")
            coEvery { refreshStationsOnLaunchUseCase() } returns
                RefreshStationsResult.SkippedFreshCache
            coEvery { refreshProductsOnLaunchUseCase() } returns
                RefreshProductsResult.LocalFailure(error)

            val result = coordinator.prepare()

            result shouldBe PostAuthStartupOutcome(hadRefreshFailure = true)
        }
    }
})
