package com.chaikasoft.app.ui.viewmodels

import app.cash.turbine.test
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.usecases.GetActiveShiftUseCase
import com.chaikasoft.app.startup.PostAuthStartupOutcome
import com.chaikasoft.app.startup.PostAuthStartupSeam
import com.chaikasoft.app.ui.shift
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class MainNavigationViewModelTest : FunSpec({

    lateinit var getActiveShift: GetActiveShiftUseCase
    lateinit var postAuthStartupSeam: PostAuthStartupSeam
    lateinit var activeShift: MutableStateFlow<ConductorTripShiftDomain?>

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        getActiveShift = mockk()
        postAuthStartupSeam = mockk()
        activeShift = MutableStateFlow(null)
        every { getActiveShift() } returns activeShift
    }

    afterTest {
        Dispatchers.resetMain()
    }

    test("hasActiveShift follows active shift flow") {
        runTest {
            val vm = MainNavigationViewModel(getActiveShift, postAuthStartupSeam)

            vm.hasActiveShift.test {
                awaitItem() shouldBe false

                activeShift.value = shift()
                awaitItem() shouldBe true

                activeShift.value = null
                awaitItem() shouldBe false

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("refreshAuthenticatedAppSilently delegates to post auth startup") {
        runTest {
            coEvery { postAuthStartupSeam.prepareForAuthenticatedApp() } returns
                PostAuthStartupOutcome(hadRefreshFailure = false)
            val vm = MainNavigationViewModel(getActiveShift, postAuthStartupSeam)

            vm.refreshAuthenticatedAppSilently()
            advanceUntilIdle()

            coVerify(exactly = 1) { postAuthStartupSeam.prepareForAuthenticatedApp() }
        }
    }

    test("refreshAuthenticatedAppSilently ignores startup failure and can run again") {
        runTest {
            coEvery { postAuthStartupSeam.prepareForAuthenticatedApp() } throws IOException("boom")
            val vm = MainNavigationViewModel(getActiveShift, postAuthStartupSeam)

            vm.refreshAuthenticatedAppSilently()
            advanceUntilIdle()
            vm.refreshAuthenticatedAppSilently()
            advanceUntilIdle()

            coVerify(exactly = 2) { postAuthStartupSeam.prepareForAuthenticatedApp() }
        }
    }

    test("refreshAuthenticatedAppSilently does not launch parallel startup") {
        runTest {
            val startupResult = CompletableDeferred<PostAuthStartupOutcome>()
            coEvery { postAuthStartupSeam.prepareForAuthenticatedApp() } coAnswers {
                startupResult.await()
            }
            val vm = MainNavigationViewModel(getActiveShift, postAuthStartupSeam)

            vm.refreshAuthenticatedAppSilently()
            vm.refreshAuthenticatedAppSilently()

            coVerify(exactly = 1) { postAuthStartupSeam.prepareForAuthenticatedApp() }

            startupResult.complete(PostAuthStartupOutcome(hadRefreshFailure = false))
            advanceUntilIdle()
        }
    }
})
