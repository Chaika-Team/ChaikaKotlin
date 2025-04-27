package com.example.chaika.data.room.repo

import com.example.chaika.domain.models.trip.ConductorTripShiftDomain
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для работы с сущностью ConductorTripShift в Room.
 */
interface RoomConductorTripShiftRepositoryInterface {

    /** Вставить новую смену или обновить существующую */
    suspend fun insertOrUpdate(shift: ConductorTripShiftDomain)

    /**
     * Обновить статус и при необходимости отчёт
     * @param uuid       — идентификатор смены
     * @param newStatus  — новый статус
     * @param reportJson — JSON-отчёт, или null, чтобы оставить старое значение
     */
    suspend fun updateStatusAndReport(
        uuid: String,
        newStatus: Int,
        reportJson: String?,
        updatedAt: Long
    )

    /** Одноразово получить текущую активную смену (или null, если нет) */
    suspend fun getActiveShift(): ConductorTripShiftDomain?

    /** Наблюдать за текущей активной сменой — эмиттит при изменении */
    fun observeActiveShift(): Flow<ConductorTripShiftDomain?>

    /** Наблюдать за всеми сменами проводника */
    fun observeAllShifts(): Flow<List<ConductorTripShiftDomain>>
}
