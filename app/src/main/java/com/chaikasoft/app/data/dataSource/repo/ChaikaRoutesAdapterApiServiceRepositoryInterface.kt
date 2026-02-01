package com.chaikasoft.app.data.dataSource.repo

import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain

/**
 * Интерфейс репозитория для работы с ChaikaRoutesAdapter.
 */
interface ChaikaRoutesAdapterApiServiceRepositoryInterface {

    /**
     * Ищет поездки по заданной дате и номеру поезда.
     */
    suspend fun searchTripsByRoute(date: String): List<TripDomain>

    /**
     * Ищет поездки по дате и кодам станций отправления/прибытия.
     */
    suspend fun searchTripsByStations(date: String, fromCode: String, toCode: String): List<TripDomain>

    /**
     * Возвращает список вагонов для конкретной поездки.
     */
    suspend fun getCarriagesForTrain(tripUuid: String): List<CarriageDomain>

    /**
     * Загрузить все станции на старте (большим лимитом, без query)
     */
    suspend fun fetchAllStations(limit: Int): List<StationDomain>
}
