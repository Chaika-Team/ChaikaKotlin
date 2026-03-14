package com.chaikasoft.app.ui.viewModels

import com.chaikasoft.app.R
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.sealed.SearchTripsResult
import com.chaikasoft.app.domain.sealed.SendReportResult
import com.chaikasoft.app.domain.usecases.CompleteShiftAndSendUseCase
import com.chaikasoft.app.domain.usecases.GetActiveShiftUseCase
import com.chaikasoft.app.domain.usecases.GetPagedStationSuggestionsUseCase
import com.chaikasoft.app.domain.usecases.GetShiftHistoryUseCase
import com.chaikasoft.app.domain.usecases.SearchTripsByStationsUseCase
import com.chaikasoft.app.domain.usecases.SendShiftReportUseCase
import com.chaikasoft.app.domain.usecases.StartShiftUseCase
import com.chaikasoft.app.ui.shift
import com.chaikasoft.app.ui.station
import com.chaikasoft.app.ui.state.TripsSearchUiState
import com.chaikasoft.app.ui.trip
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class TripViewModelTest : FunSpec({

    lateinit var searchTrips: SearchTripsByStationsUseCase
    lateinit var getStations: GetPagedStationSuggestionsUseCase
    lateinit var startShift: StartShiftUseCase
    lateinit var getActiveShift: GetActiveShiftUseCase
    lateinit var completeShift: CompleteShiftAndSendUseCase
    lateinit var getShiftHistory: GetShiftHistoryUseCase
    lateinit var sendShiftReport: SendShiftReportUseCase
    lateinit var vm: TripViewModel

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        searchTrips = mockk()
        getStations = mockk()
        startShift = mockk()
        getActiveShift = mockk()
        completeShift = mockk()
        getShiftHistory = mockk()
        sendShiftReport = mockk()

        every { getStations(any(), any()) } returns emptyFlow()
        coEvery { searchTrips(any(), any(), any()) } returns SearchTripsResult.Success(emptyList())
        coEvery { startShift(any(), any()) } returns true
        every { getActiveShift() } returns flowOf(null)
        coEvery { completeShift(any()) } returns SendReportResult.Success
        every { getShiftHistory() } returns flowOf(emptyList())
        coEvery { sendShiftReport(any()) } returns SendReportResult.Success

        vm = TripViewModel(
            searchTrips,
            getStations,
            startShift,
            getActiveShift,
            completeShift,
            getShiftHistory,
            sendShiftReport
        )
    }

    afterTest {
        Dispatchers.resetMain()
    }

    test("onCarriageNumberChanged normalizes input and rejects invalid values") {
        vm.onCarriageNumberChanged("07")
        vm.carriageNumber.value shouldBe "7"

        vm.onCarriageNumberChanged("000")
        vm.carriageNumber.value shouldBe ""

        vm.onCarriageNumberChanged("123")
        vm.carriageNumber.value shouldBe ""
    }

    test("confirmCarriageInput with valid state starts shift flow and calls callback") {
        runTest {
            val selectedTrip = trip(uuid = "u-1")
            vm.selectTrip(selectedTrip)
            vm.onCarriageNumberChanged("05")
            var callbackCalled = false

            vm.confirmCarriageInput { callbackCalled = true }
            advanceUntilIdle()

            callbackCalled shouldBe true
            coVerify(exactly = 1) { startShift(selectedTrip, any()) }
            vm.selectedTripRecord.value shouldBe null
        }
    }

    test("getTrips maps success result to Content and uses cache for same params") {
        runTest {
            val trips = listOf(trip(uuid = "1"), trip(uuid = "2"))
            coEvery { searchTrips("2026-01-01", "100", "200") } returns SearchTripsResult.Success(trips)

            vm.getTrips("2026-01-01", "100", "200")
            advanceUntilIdle()

            vm.tripsSearchState.value shouldBe TripsSearchUiState.Content(trips)

            vm.getTrips("2026-01-01", "100", "200")
            advanceUntilIdle()
            coVerify(exactly = 1) { searchTrips("2026-01-01", "100", "200") }
        }
    }

    test("getTrips maps empty result to Empty") {
        runTest {
            coEvery { searchTrips("2026-01-01", "100", "200") } returns SearchTripsResult.Success(emptyList())

            vm.getTrips("2026-01-01", "100", "200")
            advanceUntilIdle()

            vm.tripsSearchState.value shouldBe TripsSearchUiState.Empty
        }
    }

    test("getTrips maps failure to Error via AppErrorUiMapper") {
        runTest {
            coEvery { searchTrips("2026-01-01", "100", "200") } returns
                SearchTripsResult.Failure(AppError.Network)

            vm.getTrips("2026-01-01", "100", "200")
            advanceUntilIdle()

            val state = vm.tripsSearchState.value
            (state is TripsSearchUiState.Error) shouldBe true
            val error = state as TripsSearchUiState.Error
            error.messageRes shouldBe R.string.error_no_connection
            error.retryable shouldBe true
        }
    }

    test("finishCurrentTrip maps SendReportResult to dialog message") {
        runTest {
            vm.selectTrip(trip(uuid = "u-finish"))
            coEvery { completeShift("u-finish") } returns SendReportResult.AlreadySent

            vm.finishCurrentTrip()
            advanceUntilIdle()

            vm.finishTripDialog.value?.messageRes shouldBe R.string.trip_finish_already_sent
        }
    }

    test("checkActiveShift updates selected trip and shift status") {
        runTest {
            val activeShift = shift(trip = trip(uuid = "active"))
            every { getActiveShift() } returns flowOf(activeShift)

            vm = TripViewModel(
                searchTrips,
                getStations,
                startShift,
                getActiveShift,
                completeShift,
                getShiftHistory,
                sendShiftReport
            )
            vm.checkActiveShift()
            advanceUntilIdle()

            vm.selectedTripRecord.value?.uuid shouldBe "active"
        }
    }

    test("onFindByNumberScreenShown resets search when preserve flag not set") {
        vm.onSearchDateChanged("2026-01-01")
        vm.onStartStationChanged(station(code = "100", name = "A"))
        vm.onFinishStationChanged(station(code = "200", name = "B"))
        vm.getTrips("2026-01-01", "100", "200")

        vm.onFindByNumberScreenShown()

        vm.searchDate.value shouldBe ""
        vm.searchStartStation.value shouldBe null
        vm.searchFinishStation.value shouldBe null
        vm.tripsSearchState.value shouldBe TripsSearchUiState.Idle
    }

    test("preserveSearchForBackNavigation keeps search state on next screen show") {
        vm.onSearchDateChanged("2026-01-01")
        vm.onStartStationChanged(station(code = "100", name = "A"))
        vm.onFinishStationChanged(station(code = "200", name = "B"))

        vm.preserveSearchForBackNavigation()
        vm.onFindByNumberScreenShown()

        vm.searchDate.value shouldBe "2026-01-01"
        vm.searchStartStation.value?.code shouldBe "100"
        vm.searchFinishStation.value?.code shouldBe "200"
    }

    test("loadHistory observes shifts from history delegate") {
        runTest {
            val historyItems = listOf(shift(trip = trip(uuid = "h-1")))
            every { getShiftHistory() } returns flowOf(historyItems)

            vm = TripViewModel(
                searchTrips,
                getStations,
                startShift,
                getActiveShift,
                completeShift,
                getShiftHistory,
                sendShiftReport
            )
            vm.loadHistory()
            advanceUntilIdle()

            vm.shiftHistory.value shouldBe historyItems
            vm.stopHistoryObserving()
        }
    }

    test("retry send flow updates confirm and result dialogs") {
        runTest {
            coEvery { sendShiftReport("u-retry") } returns SendReportResult.AlreadySent

            vm.requestRetrySend("u-retry")
            vm.retryConfirm.value?.uuid shouldBe "u-retry"

            vm.confirmRetrySend()
            advanceUntilIdle()

            vm.retryConfirm.value shouldBe null
            vm.retryResult.value?.messageRes shouldBe R.string.trip_finish_already_sent

            vm.dismissRetryResult()
            vm.retryResult.value shouldBe null
        }
    }
})
