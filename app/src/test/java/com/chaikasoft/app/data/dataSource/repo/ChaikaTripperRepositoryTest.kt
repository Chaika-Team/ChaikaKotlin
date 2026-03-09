package com.chaikasoft.app.data.dataSource.repo

import com.chaikasoft.app.data.dataSource.apiService.ChaikaSoftApiService
import com.chaikasoft.app.data.dataSource.dto.StationDto
import com.chaikasoft.app.data.dataSource.dto.StationsResponseDto
import com.chaikasoft.app.data.dataSource.dto.TripDto
import com.chaikasoft.app.data.dataSource.dto.TripsResponseDto
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.RemoteResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.net.UnknownHostException

class ChaikaTripperRepositoryTest : FunSpec({

    lateinit var api: ChaikaSoftApiService
    lateinit var repository: ChaikaTripperRepository

    beforeTest {
        api = mockk()
        repository = ChaikaTripperRepository(api)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Описание:
     *   - Граница: поле trips может приходить nullable, внутри используется orEmpty().
     *   - Ожидаемое поведение: корректный маппинг списка поездок без падений.
     *   - Цель: защитить ветку поиска поездок по дате от NPE и искажений данных.
     */
    test("searchTripsByRoute maps trips and handles null list as empty") {
        runTest {
            coEvery { api.findTrips(fromCode = null, toCode = null, date = "2026-03-10") } returns
                TripsResponseDto(
                    trips = listOf(
                        TripDto(
                            uuid = "trip-1",
                            trainNumber = "A1",
                            departure = "2026-03-10T10:00:00Z",
                            arrival = "2026-03-10T12:00:00Z",
                            duration = "PT2H",
                            from = StationDto(code = "MOW", name = "Moscow", city = "Moscow"),
                            to = StationDto(code = "SPB", name = "Saint-Petersburg", city = "SPB"),
                        ),
                    ),
                )

            val result = repository.searchTripsByRoute("2026-03-10")

            result.size shouldBe 1
            result.first().uuid shouldBe "trip-1"
            result.first().from.code shouldBe "MOW"
        }
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Описание:
     *   - Комбинация: успешный ответ для поиска по станциям.
     *   - Ожидаемое поведение: RemoteResult.Success с маппингом TripDto -> TripDomain.
     *   - Цель: зафиксировать корректный happy-path ветки через remoteCall.
     */
    test("searchTripsByStations success returns RemoteResult.Success with mapped trips") {
        runTest {
            coEvery { api.findTrips(fromCode = "MOW", toCode = "SPB", date = "2026-03-10") } returns
                TripsResponseDto(
                    trips = listOf(
                        TripDto(
                            uuid = "trip-2",
                            trainNumber = "B2",
                            departure = "2026-03-10T10:00:00Z",
                            arrival = "2026-03-10T12:00:00Z",
                            duration = "PT2H",
                            from = StationDto(code = "MOW", name = "Moscow", city = "Moscow"),
                            to = StationDto(code = "SPB", name = "Saint-Petersburg", city = "SPB"),
                        ),
                    ),
                )

            val result = repository.searchTripsByStations("2026-03-10", "MOW", "SPB")

            (result as RemoteResult.Success).data.first().uuid shouldBe "trip-2"
        }
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing
     *
     * Описание:
     *   - Сценарий: сетевой сбой DNS в поиске поездок.
     *   - Ожидаемое поведение: RemoteResult.Failure(AppError.Network).
     *   - Цель: зафиксировать преобразование сетевой ошибки в доменный результат.
     */
    test("searchTripsByStations network error returns RemoteResult.Failure(Network)") {
        runTest {
            coEvery { api.findTrips(fromCode = "MOW", toCode = "SPB", date = "2026-03-10") } throws
                UnknownHostException("dns")

            val result = repository.searchTripsByStations("2026-03-10", "MOW", "SPB")

            result shouldBe RemoteResult.Failure(AppError.Network)
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Эквивалентный класс: успешная загрузка списка станций.
     *   - Ожидаемое поведение: RemoteResult.Success с корректным маппингом станций.
     *   - Цель: защитить контракт репозитория для инициализационной загрузки станций.
     */
    test("fetchAllStations success maps stations") {
        runTest {
            coEvery { api.findStations(limit = 100, offset = 0) } returns
                StationsResponseDto(
                    stations = listOf(
                        StationDto(code = "MOW", name = "Moscow", city = "Moscow"),
                        StationDto(code = "SPB", name = "Saint-Petersburg", city = "SPB"),
                    ),
                )

            val result = repository.fetchAllStations(limit = 100)

            (result as RemoteResult.Success).data.map { it.code } shouldBe listOf("MOW", "SPB")
        }
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing
     *
     * Описание:
     *   - Сценарий: сетевой сбой при загрузке станций.
     *   - Ожидаемое поведение: RemoteResult.Failure(AppError.Network).
     *   - Цель: закрепить стабильный сигнал о сетевой ошибке в этой ветке.
     */
    test("fetchAllStations network error returns RemoteResult.Failure(Network)") {
        runTest {
            coEvery { api.findStations(limit = 100, offset = 0) } throws UnknownHostException("dns")

            val result = repository.fetchAllStations(limit = 100)

            result shouldBe RemoteResult.Failure(AppError.Network)
        }
    }
})
