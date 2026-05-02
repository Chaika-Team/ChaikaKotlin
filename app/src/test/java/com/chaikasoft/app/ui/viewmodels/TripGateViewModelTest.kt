package com.chaikasoft.app.ui.viewmodels

import com.chaikasoft.app.startup.TripGateStartupOutcome
import com.chaikasoft.app.startup.TripGateStartupSeam
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class TripGateViewModelTest : FunSpec({

    lateinit var tripGateStartupSeam: TripGateStartupSeam

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        tripGateStartupSeam = mockk()
    }

    afterTest {
        Dispatchers.resetMain()
    }

    fun createVm(): TripGateViewModel = TripGateViewModel(
        tripGateStartupSeam = tripGateStartupSeam
    )

    test("prepare marks ready without failure when refresh succeeds") {
        runTest {
            coEvery { tripGateStartupSeam.prepareForTripEntry() } returns
                TripGateStartupOutcome(hadRefreshFailure = false)
            val vm = createVm()

            vm.prepare()
            advanceUntilIdle()

            vm.uiState.value shouldBe TripGateViewModel.TripGateUiState.Ready(hadRefreshFailure = false)
            coVerify(exactly = 1) { tripGateStartupSeam.prepareForTripEntry() }
        }
    }

    test("prepare marks ready with failure when refresh returns failure") {
        runTest {
            coEvery { tripGateStartupSeam.prepareForTripEntry() } returns
                TripGateStartupOutcome(hadRefreshFailure = true)
            val vm = createVm()

            vm.prepare()
            advanceUntilIdle()

            vm.uiState.value shouldBe TripGateViewModel.TripGateUiState.Ready(hadRefreshFailure = true)
            coVerify(exactly = 1) { tripGateStartupSeam.prepareForTripEntry() }
        }
    }

    test("prepare is idempotent and does not launch refresh twice") {
        runTest {
            coEvery { tripGateStartupSeam.prepareForTripEntry() } returns
                TripGateStartupOutcome(hadRefreshFailure = false)
            val vm = createVm()

            vm.prepare()
            vm.prepare()
            advanceUntilIdle()

            vm.uiState.value shouldBe TripGateViewModel.TripGateUiState.Ready(hadRefreshFailure = false)
            coVerify(exactly = 1) { tripGateStartupSeam.prepareForTripEntry() }
        }
    }
})
