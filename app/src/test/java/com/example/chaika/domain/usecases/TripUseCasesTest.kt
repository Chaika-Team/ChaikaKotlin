// src/test/java/com/example/chaika/domain/usecases/TripUseCasesTest.kt
package com.example.chaika.domain.usecases

import com.example.chaika.data.dataSource.repo.ChaikaRoutesAdapterApiServiceRepositoryInterface
import com.example.chaika.domain.models.trip.CarriageDomain
import com.example.chaika.domain.models.trip.StationDomain
import com.example.chaika.domain.models.trip.TripDomain
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class TripUseCasesTest {

    // Sample domain objects for tests
    private val stationA = StationDomain(code = 1, name = "A Station", city = "CityA")
    private val stationB = StationDomain(code = 2, name = "B Station", city = "CityB")
    private val trip = TripDomain(
        uuid = "uuid1",
        trainNumber = "100A",
        departure = "2025-01-01T00:00:00Z",
        arrival = "2025-01-01T02:00:00Z",
        duration = "PT2H",
        from = stationA,
        to = stationB
    )
    private val carriage = CarriageDomain(carNumber = "01", classType = "2Я")

    // Fake repository stub
    private class FakeRoutesRepo(
        private val stations: List<StationDomain> = emptyList(),
        private val tripsByRoute: List<TripDomain> = emptyList(),
        private val tripsByStations: List<TripDomain> = emptyList(),
        private val carriages: List<CarriageDomain> = emptyList()
    ) : ChaikaRoutesAdapterApiServiceRepositoryInterface {
        override suspend fun suggestStations(query: String, limit: Int) =
            stations.filter { it.name.contains(query, ignoreCase = true) }.take(limit)

        override suspend fun searchTripsByRoute(date: String, trainNumber: String) =
            tripsByRoute.filter { it.trainNumber == trainNumber }

        override suspend fun searchTripsByStations(date: String, fromCode: Int, toCode: Int) =
            tripsByStations.filter { it.from.code == fromCode && it.to.code == toCode }

        override suspend fun getCarriagesForTrain(tripUuid: String) =
            carriages.takeIf { tripUuid == "uuid1" } ?: emptyList()
    }

    // --- SuggestStationsUseCase tests ---

    @Test
    fun suggestStationsReturnsMatchingStations() = runTest {
        val repo = FakeRoutesRepo(stations = listOf(stationA, stationB))
        val useCase = SuggestStationsUseCase(repo)
        val result = useCase("A St")
        assertEquals(1, result.size)
        assertEquals("A Station", result.first().name)
    }

    @Test
    fun suggestStationsReturnsEmptyWhenNoMatch() = runTest {
        val repo = FakeRoutesRepo(stations = listOf(stationA))
        val useCase = SuggestStationsUseCase(repo)
        val result = useCase("Z")
        assertTrue(result.isEmpty())
    }

    // --- SearchTripsByRouteUseCase tests ---

    @Test
    fun searchTripsByRouteReturnsTripWhenTrainNumberMatches() = runTest {
        val repo = FakeRoutesRepo(tripsByRoute = listOf(trip))
        val useCase = SearchTripsByRouteUseCase(repo)
        val result = useCase("2025-01-01", "100A")
        assertEquals(1, result.size)
        assertEquals("uuid1", result.first().uuid)
    }

    @Test
    fun searchTripsByRouteReturnsEmptyWhenNoMatch() = runTest {
        val repo = FakeRoutesRepo(tripsByRoute = listOf(trip))
        val useCase = SearchTripsByRouteUseCase(repo)
        val result = useCase("2025-01-01", "999X")
        assertTrue(result.isEmpty())
    }

    // --- SearchTripsByStationsUseCase tests ---

    @Test
    fun searchTripsByStationsReturnsTripWhenCodesMatch() = runTest {
        val repo = FakeRoutesRepo(tripsByStations = listOf(trip))
        val useCase = SearchTripsByStationsUseCase(repo)
        val result = useCase("2025-01-01", fromCode = 1, toCode = 2)
        assertEquals(1, result.size)
        assertEquals(1, result.first().from.code)
        assertEquals(2, result.first().to.code)
    }

    @Test
    fun searchTripsByStationsReturnsEmptyWhenNoMatch() = runTest {
        val repo = FakeRoutesRepo(tripsByStations = listOf(trip))
        val useCase = SearchTripsByStationsUseCase(repo)
        val result = useCase("2025-01-01", fromCode = 9, toCode = 8)
        assertTrue(result.isEmpty())
    }

    // --- GetCarriagesForTrainUseCase tests ---

    @Test
    fun getCarriagesForTrainReturnsCarriagesWhenUuidMatches() = runTest {
        val repo = FakeRoutesRepo(carriages = listOf(carriage))
        val useCase = GetCarriagesForTrainUseCase(repo)
        val result = useCase("uuid1")
        assertEquals(1, result.size)
        assertEquals("01", result.first().carNumber)
    }

    @Test
    fun getCarriagesForTrainReturnsEmptyWhenNoMatch() = runTest {
        val repo = FakeRoutesRepo(carriages = listOf(carriage))
        val useCase = GetCarriagesForTrainUseCase(repo)
        val result = useCase("no-such-uuid")
        assertTrue(result.isEmpty())
    }
}
