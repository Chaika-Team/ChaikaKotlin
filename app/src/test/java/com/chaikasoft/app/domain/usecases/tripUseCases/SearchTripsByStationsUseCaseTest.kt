package com.chaikasoft.app.domain.usecases.tripUseCases

import com.chaikasoft.app.data.datasource.repo.ChaikaTripperRepositoryInterface
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.sealed.SearchTripsResult
import com.chaikasoft.app.domain.usecases.SearchTripsByStationsUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class SearchTripsByStationsUseCaseTest : FunSpec({

    lateinit var repository: ChaikaTripperRepositoryInterface
    lateinit var useCase: SearchTripsByStationsUseCase

    val date = "2025-01-01"
    val fromCode = "100"
    val toCode = "200"
    val stationFrom = StationDomain(code = fromCode, name = "Station A", city = "City A")
    val stationTo = StationDomain(code = toCode, name = "Station B", city = "City B")
    val trips = listOf(
        TripDomain(
            uuid = "uuid-1",
            trainNumber = "100A",
            departure = "2025-01-01T00:00:00Z",
            arrival = "2025-01-01T02:00:00Z",
            duration = "PT2H",
            from = stationFrom,
            to = stationTo,
        )
    )

    beforeTest {
        repository = mockk()
        useCase = SearchTripsByStationsUseCase(repository)
    }

    /**
     * Test design technique: #1 Equivalence classes
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Input class: repository returns RemoteResult.Success with trips.
     *   - Expected behavior:
     *       1) use case maps it to SearchTripsResult.Success,
     *       2) repository is called with the same parameters.
     *   - Goal: protect the "success maps to success" invariant.
     */
    test("when repository returns success - returns Success with trips") {
        runTest {
            coEvery { repository.searchTripsByStations(date, fromCode, toCode) } returns
                RemoteResult.Success(trips)

            val result = useCase(date, fromCode, toCode)

            result shouldBe SearchTripsResult.Success(trips)
            coVerify(exactly = 1) { repository.searchTripsByStations(date, fromCode, toCode) }
            confirmVerified(repository)
        }
    }

    /**
     * Test design technique: #5 Error guessing
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Input class: repository returns RemoteResult.Failure.
     *   - Expected behavior:
     *       1) use case maps it to SearchTripsResult.Failure with the same error,
     *       2) repository is called with the same parameters.
     *   - Goal: keep error propagation intact for network or backend issues.
     */
    test("when repository returns failure - returns Failure with the same error") {
        runTest {
            val error = AppError.Network()
            coEvery { repository.searchTripsByStations(date, fromCode, toCode) } returns
                RemoteResult.Failure(error)

            val result = useCase(date, fromCode, toCode)

            result shouldBe SearchTripsResult.Failure(error)
            coVerify(exactly = 1) { repository.searchTripsByStations(date, fromCode, toCode) }
            confirmVerified(repository)
        }
    }
})
