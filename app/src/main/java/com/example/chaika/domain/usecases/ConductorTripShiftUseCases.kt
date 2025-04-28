package com.example.chaika.domain.usecases

import com.example.chaika.data.room.repo.RoomConductorTripShiftRepositoryInterface
import com.example.chaika.domain.models.trip.CarriageDomain
import com.example.chaika.domain.models.trip.ConductorTripShiftDomain
import com.example.chaika.domain.models.trip.TripDomain
import com.example.chaika.domain.models.trip.TripShiftStatusDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Возвращает поток со всеми сменами проводника.
 */
class GetAllShiftsUseCase @Inject constructor(
    private val repository: RoomConductorTripShiftRepositoryInterface
) {
    operator fun invoke(): Flow<List<ConductorTripShiftDomain>> =
        repository.observeAllShifts()
}

/**
 * Возвращает поток с единственной активной сменой, либо null, когда нет активной.
 */
class GetActiveShiftUseCase @Inject constructor(
    private val repository: RoomConductorTripShiftRepositoryInterface
) {
    operator fun invoke(): Flow<ConductorTripShiftDomain?> =
        repository.observeActiveShift()
}

/**
 * Пытается создать новую смену (ACTIVE) из переданных TripDomain и CarriageDomain.
 * Если в базе уже есть активная смена, ничего не делает и возвращает false.
 * Иначе создаёт новую смену со статусом ACTIVE и возвращает true.
 */
class StartShiftUseCase @Inject constructor(
    private val repository: RoomConductorTripShiftRepositoryInterface
) {
    suspend operator fun invoke(
        trip: TripDomain,
        activeCarriage: CarriageDomain?
    ): Boolean {
        // не допускаем более одной активной смены
        if (repository.getActiveShift() != null) return false

        val newShift = ConductorTripShiftDomain(
            trip = trip,
            activeCarriage = activeCarriage,
            status = TripShiftStatusDomain.ACTIVE
        )
        repository.insertOrUpdate(newShift)
        return true
    }
}

/**
 * CompleteShiftUseCase — объединяет оба шага: генерация + попытка отправки
 */
class CompleteShiftUseCase @Inject constructor(
    private val generate: GenerateShiftReportUseCase,
    private val send: SendShiftReportUseCase
) {
    /**
     * Завершает смену проводника:
     * 1) генерирует отчёт и переводит в FINISHED,
     * 2) пытается отправить отчёт и, если удалось, переводит в SENT.
     *
     * @return true — если отчёт отправлен; false — если отправка не удалась.
     */
    suspend operator fun invoke(uuid: String): Boolean {
        // 1) генерим и сохраняем отчёт
        generate(uuid)
        // 2) пробуем отправить
        return send(uuid)
    }
}

/**
 * Генерирует отчёт по поездке
 */
class GenerateShiftReportUseCase @Inject constructor(
    private val repository: RoomConductorTripShiftRepositoryInterface
) {
    suspend operator fun invoke(uuid: String): String {

        //TODO: Здесь будет настоящая генерация отчёта; пока–что просто мокаем простой json
        val generatedAt = System.currentTimeMillis()
        val reportJson = """
      {
        "tripUuid":"$uuid",
        "generatedAt":$generatedAt,
        "notes":"Это временный плейсхолдер отчёта"
      }
    """.trimIndent()
        // 2) Обновляем в БД статус → FINISHED и сохраняем report
        repository.updateStatusAndReport(
            uuid = uuid,
            newStatus = TripShiftStatusDomain.FINISHED.ordinal,
            reportJson = reportJson,
            updatedAt = generatedAt
        )
        return reportJson
    }
}

/**
 * Отправляет отчёт по поездке
 */
class SendShiftReportUseCase @Inject constructor(
    private val repository: RoomConductorTripShiftRepositoryInterface
) {
    suspend operator fun invoke(uuid: String): Boolean {
        //TODO: Здесь будет настоящий сетевой слой; пока–что просто мокаем «успех»
        val success = true

        if (success) {
            val now = System.currentTimeMillis()
            // При успешной «отправке» переводим статус в SENT, reportJson = null => оставляем старый отчёт
            repository.updateStatusAndReport(
                uuid = uuid,
                newStatus = TripShiftStatusDomain.SENT.ordinal,
                reportJson = null,
                updatedAt = now
            )
        }

        return success
    }
}