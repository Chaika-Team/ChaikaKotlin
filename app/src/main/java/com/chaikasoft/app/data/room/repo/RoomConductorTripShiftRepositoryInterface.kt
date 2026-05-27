package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для работы с сущностью ConductorTripShift в Room.
 */
interface RoomConductorTripShiftRepositoryInterface {

    /** Вставить новую смену или обновить существующую */
    /** Попытаться создать новую ACTIVE-смену. Возвращает false, если активная уже есть. */
    suspend fun tryStartNewShift(shift: ConductorTripShiftDomain): Boolean

    /** Получить поездку по UUID */
    suspend fun getShiftByUuid(uuid: String): ConductorTripShiftDomain?

    /**
     * Обновить статус и при необходимости отчёт
     * @param uuid       — идентификатор смены
     * @param newStatus  — новый статус
     * @param reportJson — JSON-отчёт, или null, чтобы оставить старое значение
     */
    suspend fun updateStatusAndReport(
        uuid: String,
        newStatus: Int,
        reportJson: String? = null,
        updatedAt: Long
    )

    /** Одноразово получить текущую активную смену (или null, если нет) */
    suspend fun getActiveShift(): ConductorTripShiftDomain?

    /** Наблюдать за текущей активной сменой — эмиттит при изменении */
    fun observeActiveShift(): Flow<ConductorTripShiftDomain?>

    /** Наблюдать за всеми сменами проводника */
    fun observeShiftHistory(): Flow<List<ConductorTripShiftDomain>>

    /** Получить пару "статус смены и отчёт" */
    suspend fun getStatusAndReport(uuid: String): Pair<TripShiftStatusDomain, String?>
}
