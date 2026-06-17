package com.chaikasoft.app.ui.viewmodels

import androidx.paging.PagingData
import app.cash.turbine.test
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.sealed.StartShiftResult
import com.chaikasoft.app.domain.usecases.GetPagedStationSuggestionsUseCase
import com.chaikasoft.app.domain.usecases.StartShiftUseCase
import com.chaikasoft.app.ui.helpers.OfflineTripBuildHelper
import com.chaikasoft.app.ui.station
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class AutonomousViewModelTest : FunSpec({

    lateinit var getPagedStationSuggestions: GetPagedStationSuggestionsUseCase
    lateinit var startShift: StartShiftUseCase
    lateinit var vm: AutonomousViewModel

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        getPagedStationSuggestions = mockk()
        startShift = mockk()
        every {
            getPagedStationSuggestions(any(), any())
        } returns flowOf(PagingData.empty<StationDomain>())
        coEvery { startShift(any(), any()) } returns StartShiftResult.Started

        vm = AutonomousViewModel(getPagedStationSuggestions, startShift)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    fun fillValidForm() {
        vm.onTrainNumberChange("A-12")
        vm.onSelectFrom(station(code = "100", name = "From"))
        vm.onSelectTo(station(code = "200", name = "To"))
        vm.onDepartureChange(LocalDateTime.of(2026, 1, 1, 10, 0))
        vm.onArrivalChange(LocalDateTime.of(2026, 1, 1, 11, 0))
        vm.onCarriageNumberChange("4")
    }

    test("submit invalid input populates buildErrors") {
        runTest {
            vm.submit()
            advanceUntilIdle()

            vm.state.value.buildErrors shouldContain OfflineTripBuildHelper.BuildError.TrainNumberEmpty
            vm.state.value.buildErrors shouldContain OfflineTripBuildHelper.BuildError.FromStationMissing
        }
    }

    test("submit valid input emits ShiftStarted when startShift succeeds") {
        runTest {
            fillValidForm()
            coEvery { startShift(any(), any()) } returns StartShiftResult.Started

            vm.events.test {
                vm.submit()
                advanceUntilIdle()
                val event = awaitItem()
                (event is AutonomousViewModel.Event.ShiftStarted) shouldBe true
            }
        }
    }

    test("submit valid input emits Info when active shift already exists") {
        runTest {
            fillValidForm()
            coEvery {
                startShift(any(), any())
            } returns StartShiftResult.ActiveShiftAlreadyExists

            vm.events.test {
                vm.submit()
                advanceUntilIdle()
                val event = awaitItem()
                event shouldBe AutonomousViewModel.Event.Info(R.string.active_shift_already_exists)
            }
        }
    }

    test("submit valid input emits Info when trip was already registered") {
        runTest {
            fillValidForm()
            coEvery {
                startShift(any(), any())
            } returns StartShiftResult.TripAlreadyRegistered

            vm.events.test {
                vm.submit()
                advanceUntilIdle()
                val event = awaitItem()
                event shouldBe AutonomousViewModel.Event.Info(R.string.trip_already_registered)
            }
        }
    }

    test("clearState resets form values") {
        runTest {
            fillValidForm()

            vm.clearState()

            vm.state.value shouldBe AutonomousViewModel.UiState()
        }
    }
})
