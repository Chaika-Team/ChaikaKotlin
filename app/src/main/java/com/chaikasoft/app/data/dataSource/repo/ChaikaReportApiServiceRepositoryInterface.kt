package com.chaikasoft.app.data.dataSource.repo

import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain

/**
 * Интерфейс репозитория для работы с ChaikaReports.*
 */
interface ChaikaReportApiServiceRepositoryInterface {
    /**
     * Возвращает историю поездок проводника.
     *
     * @return Список назначений смен проводника, содержащих данные поездки и активный вагон.
     */
    suspend fun getTripHistory(): List<ConductorTripShiftDomain>
}
