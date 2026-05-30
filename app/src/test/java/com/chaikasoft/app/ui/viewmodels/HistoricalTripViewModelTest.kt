package com.chaikasoft.app.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.HistoricalTripSnapshot
import com.chaikasoft.app.domain.usecases.GetHistoricalTripSnapshotUseCase
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.state.HistoricalTripUiState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class HistoricalTripViewModelTest : FunSpec({

    lateinit var getHistoricalTripSnapshot: GetHistoricalTripSnapshotUseCase

    val shiftUuid = "shift-uuid"
    val savedStateHandle = SavedStateHandle(mapOf(Routes.ARG_SHIFT_UUID to shiftUuid))

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        getHistoricalTripSnapshot = mockk()
    }

    afterTest {
        Dispatchers.resetMain()
    }

    test("loads historical snapshot on init") {
        runTest {
            val snapshot = HistoricalTripSnapshot(
                statistics = emptyList(),
                cashRevenue = 0,
                cashlessChecksCount = 0,
                operations = emptyList()
            )
            coEvery { getHistoricalTripSnapshot(shiftUuid) } returns snapshot

            val vm = HistoricalTripViewModel(savedStateHandle, getHistoricalTripSnapshot)
            advanceUntilIdle()

            vm.uiState.value shouldBe HistoricalTripUiState.Content(snapshot)
            coVerify(exactly = 1) { getHistoricalTripSnapshot(shiftUuid) }
        }
    }

    test("maps snapshot loading failure to report error state") {
        runTest {
            coEvery { getHistoricalTripSnapshot(shiftUuid) } throws IllegalStateException("bad json")

            val vm = HistoricalTripViewModel(savedStateHandle, getHistoricalTripSnapshot)
            advanceUntilIdle()

            vm.uiState.value shouldBe HistoricalTripUiState.Error(R.string.trip_finish_missing_report)
            coVerify(exactly = 1) { getHistoricalTripSnapshot(shiftUuid) }
        }
    }

    test("does not map snapshot loading cancellation to report error state") {
        runTest {
            coEvery { getHistoricalTripSnapshot(shiftUuid) } throws
                CancellationException("cancelled")

            val vm = HistoricalTripViewModel(savedStateHandle, getHistoricalTripSnapshot)
            advanceUntilIdle()

            vm.uiState.value shouldBe HistoricalTripUiState.Loading
            coVerify(exactly = 1) { getHistoricalTripSnapshot(shiftUuid) }
        }
    }
})
