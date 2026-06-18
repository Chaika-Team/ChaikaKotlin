package com.chaikasoft.app.ui.viewmodels

import com.chaikasoft.app.R
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.sealed.SearchTripsResult
import com.chaikasoft.app.domain.sealed.SendReportResult
import com.chaikasoft.app.domain.sealed.StartShiftResult
import com.chaikasoft.app.domain.usecases.CompleteShiftAndSendUseCase
import com.chaikasoft.app.domain.usecases.DeleteActiveShiftUseCase
import com.chaikasoft.app.domain.usecases.GetActiveShiftUseCase
import com.chaikasoft.app.domain.usecases.GetPagedStationSuggestionsUseCase
import com.chaikasoft.app.domain.usecases.GetShiftHistoryUseCase
import com.chaikasoft.app.domain.usecases.HasAnyPackageItemsOnceUseCase
import com.chaikasoft.app.domain.usecases.SearchTripsByStationsUseCase
import com.chaikasoft.app.domain.usecases.SendShiftReportUseCase
import com.chaikasoft.app.domain.usecases.StartShiftUseCase
import com.chaikasoft.app.ui.shift
import com.chaikasoft.app.ui.state.TripsSearchUiState
import com.chaikasoft.app.ui.station
import com.chaikasoft.app.ui.trip
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
    lateinit var deleteActiveShift: DeleteActiveShiftUseCase
    lateinit var hasAnyPackageItems: HasAnyPackageItemsOnceUseCase
    lateinit var getShiftHistory: GetShiftHistoryUseCase
    lateinit var sendShiftReport: SendShiftReportUseCase
    lateinit var activeShift: MutableStateFlow<ConductorTripShiftDomain?>
    lateinit var vm: TripViewModel

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        searchTrips = mockk()
        getStations = mockk()
        startShift = mockk()
        getActiveShift = mockk()
        completeShift = mockk()
        deleteActiveShift = mockk()
        hasAnyPackageItems = mockk()
        getShiftHistory = mockk()
        sendShiftReport = mockk()
        activeShift = MutableStateFlow(null)

        every { getStations(any(), any()) } returns emptyFlow()
        coEvery { searchTrips(any(), any(), any()) } returns SearchTripsResult.Success(emptyList())
        coEvery { startShift(any(), any()) } returns StartShiftResult.Started
        every { getActiveShift() } returns activeShift
        coEvery { completeShift(any()) } returns SendReportResult.Success
        coEvery { deleteActiveShift(any(), any()) } returns Unit
        coEvery { hasAnyPackageItems() } returns false
        every { getShiftHistory() } returns flowOf(emptyList())
        coEvery { sendShiftReport(any()) } returns SendReportResult.Success

        vm = TripViewModel(
            searchTrips,
            getStations,
            startShift,
            getActiveShift,
            completeShift,
            deleteActiveShift,
            hasAnyPackageItems,
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
            vm.selectedTripForCreation.value shouldBe null
            vm.activeTripRecord.value shouldBe null
        }
    }

    test("confirmCarriageInput navigates to existing active shift when start reports conflict") {
        runTest {
            val existingTrip = trip(uuid = "already-active")
            activeShift.value = shift(trip = existingTrip)
            coEvery {
                startShift(any(), any())
            } returns StartShiftResult.ActiveShiftAlreadyExists
            vm.selectTrip(trip(uuid = "new-selection"))
            vm.onCarriageNumberChanged("05")
            var callbackCalled = false

            vm.confirmCarriageInput { callbackCalled = true }
            advanceUntilIdle()

            callbackCalled shouldBe true
            vm.selectedTripForCreation.value shouldBe null
            vm.activeTripRecord.value shouldBe existingTrip
        }
    }

    test("confirmCarriageInput keeps form open when trip was already registered") {
        runTest {
            val selectedTrip = trip(uuid = "already-registered")
            coEvery { startShift(any(), any()) } returns StartShiftResult.TripAlreadyRegistered
            vm.selectTrip(selectedTrip)
            vm.onCarriageNumberChanged("05")
            var callbackCalled = false

            vm.confirmCarriageInput { callbackCalled = true }
            advanceUntilIdle()

            callbackCalled shouldBe false
            vm.selectedTripForCreation.value shouldBe selectedTrip
            vm.carriageNumber.value shouldBe "5"
            vm.startShiftErrorMessageRes.value shouldBe R.string.trip_already_registered
        }
    }

    test("confirmCarriageInput keeps creation state when start throws") {
        runTest {
            val selectedTrip = trip(uuid = "u-1")
            coEvery { startShift(any(), any()) } throws IllegalArgumentException("invalid shift")
            vm.selectTrip(selectedTrip)
            vm.onCarriageNumberChanged("05")
            var callbackCalled = false

            vm.confirmCarriageInput { callbackCalled = true }
            advanceUntilIdle()

            callbackCalled shouldBe false
            vm.selectedTripForCreation.value shouldBe selectedTrip
            vm.activeTripRecord.value shouldBe null
            vm.startShiftErrorMessageRes.value shouldBe R.string.start_shift_failed
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
                SearchTripsResult.Failure(AppError.Network())

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
            activeShift.value = shift(trip = trip(uuid = "u-finish"))
            coEvery { completeShift("u-finish") } returns SendReportResult.AlreadySent

            vm.finishCurrentTrip()
            advanceUntilIdle()

            vm.finishTripDialog.value?.messageRes shouldBe R.string.trip_finish_already_sent
        }
    }

    test("active trip follows Room flow without manual refresh") {
        runTest {
            vm.activeTripRecord.value shouldBe null

            activeShift.value = shift(trip = trip(uuid = "active"))
            vm.activeTripRecord.value?.uuid shouldBe "active"

            activeShift.value = null
            vm.activeTripRecord.value shouldBe null
        }
    }

    test("empty active shift does not clear trip selected for creation") {
        runTest {
            val selectedTrip = trip(uuid = "selected")

            vm.selectTrip(selectedTrip)

            vm.selectedTripForCreation.value shouldBe selectedTrip
            vm.activeTripRecord.value shouldBe null
        }
    }

    test("finishCurrentTrip ignores repeated click and keeps card until Room emits null") {
        runTest {
            val finishResult = CompletableDeferred<SendReportResult>()
            activeShift.value = shift(trip = trip(uuid = "u-finish"))
            coEvery { completeShift("u-finish") } coAnswers { finishResult.await() }

            vm.finishCurrentTrip()
            vm.finishCurrentTrip()

            vm.isFinishingTrip.value shouldBe true
            vm.activeTripRecord.value?.uuid shouldBe "u-finish"
            coVerify(exactly = 1) { completeShift("u-finish") }

            activeShift.value = null
            finishResult.complete(SendReportResult.Success)
            advanceUntilIdle()

            vm.isFinishingTrip.value shouldBe false
            vm.activeTripRecord.value shouldBe null
        }
    }

    test("delete request is ignored while trip is finishing") {
        runTest {
            val finishResult = CompletableDeferred<SendReportResult>()
            activeShift.value = shift(trip = trip(uuid = "u-finish"))
            coEvery { completeShift("u-finish") } coAnswers { finishResult.await() }

            vm.finishCurrentTrip()
            vm.requestDeleteCurrentTrip()

            vm.deleteTripDialog.value shouldBe null
            coVerify(exactly = 0) { hasAnyPackageItems() }

            finishResult.complete(SendReportResult.Success)
            advanceUntilIdle()
        }
    }

    test("finish request is ignored while delete dialog is open") {
        runTest {
            activeShift.value = shift(trip = trip(uuid = "u-delete"))
            coEvery { hasAnyPackageItems() } returns true

            vm.requestDeleteCurrentTrip()
            advanceUntilIdle()
            vm.finishCurrentTrip()

            vm.deleteTripDialog.value?.hasPackageItems shouldBe true
            vm.isFinishingTrip.value shouldBe false
            coVerify(exactly = 0) { completeShift(any()) }
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
                deleteActiveShift,
                hasAnyPackageItems,
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

    test("delete dialog shows package and preserves it by default") {
        runTest {
            activeShift.value = shift(trip = trip(uuid = "u-delete"))
            coEvery { hasAnyPackageItems() } returns true

            vm.requestDeleteCurrentTrip()
            advanceUntilIdle()

            vm.deleteTripDialog.value?.hasPackageItems shouldBe true
            vm.deleteTripDialog.value?.preservePackage shouldBe true
        }
    }

    test("delete dialog can be dismissed while package inspection is pending") {
        runTest {
            val packageInspection = CompletableDeferred<Boolean>()
            activeShift.value = shift(trip = trip(uuid = "u-delete"))
            coEvery { hasAnyPackageItems() } coAnswers { packageInspection.await() }

            vm.requestDeleteCurrentTrip()

            vm.deleteTripDialog.value?.hasPackageItems shouldBe null
            vm.deleteTripDialog.value?.isDeleting shouldBe false

            vm.dismissDeleteTripDialog()
            packageInspection.complete(true)
            advanceUntilIdle()

            vm.deleteTripDialog.value shouldBe null
        }
    }

    test("delete current trip clears operations for empty package and resets selected trip") {
        runTest {
            activeShift.value = shift(trip = trip(uuid = "u-delete"))
            coEvery { hasAnyPackageItems() } returns false
            coEvery {
                deleteActiveShift("u-delete", preservePackage = false)
            } coAnswers {
                activeShift.value = null
            }

            vm.requestDeleteCurrentTrip()
            advanceUntilIdle()
            vm.confirmDeleteCurrentTrip()
            advanceUntilIdle()

            coVerify(exactly = 1) { deleteActiveShift("u-delete", preservePackage = false) }
            vm.activeTripRecord.value shouldBe null
            vm.deleteTripDialog.value shouldBe null
        }
    }

    test("delete current trip clears operations when package preservation is disabled") {
        runTest {
            activeShift.value = shift(trip = trip(uuid = "u-delete"))
            coEvery { hasAnyPackageItems() } returns true

            vm.requestDeleteCurrentTrip()
            advanceUntilIdle()
            vm.onPreservePackageChanged(false)
            vm.confirmDeleteCurrentTrip()
            advanceUntilIdle()

            coVerify(exactly = 1) { deleteActiveShift("u-delete", preservePackage = false) }
        }
    }

    test("delete failure keeps dialog open with error") {
        runTest {
            activeShift.value = shift(trip = trip(uuid = "u-delete"))
            coEvery { hasAnyPackageItems() } returns true
            coEvery {
                deleteActiveShift("u-delete", preservePackage = true)
            } throws IllegalStateException("delete failed")

            vm.requestDeleteCurrentTrip()
            advanceUntilIdle()
            vm.confirmDeleteCurrentTrip()
            advanceUntilIdle()

            vm.activeTripRecord.value?.uuid shouldBe "u-delete"
            vm.deleteTripDialog.value?.errorMessageRes shouldBe R.string.trip_delete_failure
        }
    }
})
