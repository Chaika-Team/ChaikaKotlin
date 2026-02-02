package com.chaikasoft.app.domain.sealed

import com.chaikasoft.app.domain.common.AppError

sealed interface RefreshStationsResult {
    /** Обновление не выполняли, потому что активная смена */
    data object SkippedActiveShift : RefreshStationsResult

    /** Станции успешно скачаны и записаны в локальную БД */
    data class Success(val stationCount: Int) : RefreshStationsResult

    /** Ошибка удалённого сервиса/сети */
    data class RemoteFailure(val error: AppError) : RefreshStationsResult

    /** Ошибка локальной БД/Room */
    data class LocalFailure(val cause: Exception) : RefreshStationsResult
}
