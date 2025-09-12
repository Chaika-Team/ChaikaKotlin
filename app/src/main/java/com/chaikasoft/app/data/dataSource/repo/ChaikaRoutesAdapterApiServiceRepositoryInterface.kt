package com.chaikasoft.app.data.dataSource.repo

import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain

/**
 * Интерфейс репозитория для работы с ChaikaRoutesAdapter.
 */
interface ChaikaRoutesAdapterApiServiceRepositoryInterface {
    /**
     * Предлагает список станций для автокомплита.
     */
    suspend fun suggestStations(query: String, limit: Int = 10): List<StationDomain>

    /**
     * Ищет поездки по заданной дате и номеру поезда.
     */
    suspend fun searchTripsByRoute(date: String, trainNumber: String): List<TripDomain>

    /**
     * Ищет поездки по дате и кодам станций отправления/прибытия.
     */
    suspend fun searchTripsByStations(date: String, fromCode: Int, toCode: Int): List<TripDomain>

    /**
     * Возвращает список вагонов для конкретной поездки.
     */
    suspend fun getCarriagesForTrain(tripUuid: String): List<CarriageDomain>

    /**
     * Загрузить все станции на старте (большим лимитом, без query)
     */
    suspend fun fetchAllStations(limit: Int): List<StationDomain>
}
