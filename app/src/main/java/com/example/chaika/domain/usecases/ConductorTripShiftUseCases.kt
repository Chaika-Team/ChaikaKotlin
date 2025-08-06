package com.example.chaika.domain.usecases

import com.example.chaika.data.room.repo.RoomCartItemRepositoryInterface
import com.example.chaika.data.room.repo.RoomCartOperationRepositoryInterface
import com.example.chaika.data.room.repo.RoomConductorTripShiftRepositoryInterface
import com.example.chaika.domain.models.report.CartIdReport
import com.example.chaika.domain.models.report.CartItemReport
import com.example.chaika.domain.models.report.CartReport
import com.example.chaika.domain.models.report.ShiftReportReport
import com.example.chaika.domain.models.report.TripIdReport
import com.example.chaika.domain.models.trip.CarriageDomain
import com.example.chaika.domain.models.trip.ConductorTripShiftDomain
import com.example.chaika.domain.models.trip.TripDomain
import com.example.chaika.domain.models.trip.TripShiftStatusDomain
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
 * Генерирует JSON-отчёт по смене и сохраняет его в поле `report` таблицы `conductor_trip_shifts`.
 */
class GenerateShiftReportUseCase @Inject constructor(
    private val shiftRepo: RoomConductorTripShiftRepositoryInterface,
    private val cartOpRepo: RoomCartOperationRepositoryInterface,
    private val cartItemRepo: RoomCartItemRepositoryInterface,
    moshi: Moshi
) {
    private val jsonAdapter = moshi.adapter(ShiftReportReport::class.java)

    /**
     * @param uuid UUID смены (TripDomain.uuid)
     * @return сгенерированный JSON
     * @throws IllegalStateException если нет активной смены или не задан вагон
     */
    suspend operator fun invoke(uuid: String): String {
        // 1) Берём активную смену
        val shift = shiftRepo.getActiveShift()
            ?: throw IllegalStateException("Active shift with uuid=$uuid not found")

        // 2) Собираем список CartReport
        val carts: List<CartReport> = cartOpRepo
            .getCartOperationReportsWithIds() // Flow<List<Pair<opId, CartOperationReport>>>
            .first()
            .map { (opId, opReport) ->
                // 2.1) товары в операции
                val items = cartItemRepo
                    .getCartItemReportsByOperationId(opId) // Flow<List<CartItemReport>>
                    .first()
                    .map { itemReport ->
                        CartItemReport(
                            productId = itemReport.productId,
                            quantity  = itemReport.quantity,
                            price     = itemReport.price
                        )
                    }

                // 2.2) один CartReport
                CartReport(
                    cartId        = CartIdReport(
                        employeeId    = opReport.employeeID,
                        operationTime = opReport.operationTime
                    ),
                    operationType = opReport.operationType,
                    items         = items
                )
            }

        // 3) Делаем корневой ShiftReportReport
        val report = ShiftReportReport(
            tripId     = TripIdReport(
                routeId   = shift.trip.trainNumber,
                startTime = shift.trip.departure
            ),
            endTime    = shift.trip.arrival,
            carriageId = shift.activeCarriage
                ?.carNumber
                ?.toIntOrNull()
                ?: throw IllegalStateException("Active carriage is not set for shift $uuid"),
            carts      = carts
        )

        // 4) Сериализация и indent для читаемости
        val json = jsonAdapter.toJson(report)

        // 5) Сохраняем JSON в БД и обновляем статус на FINISHED
        val now = System.currentTimeMillis()
        shiftRepo.updateStatusAndReport(
            uuid       = uuid,
            newStatus  = TripShiftStatusDomain.FINISHED.ordinal,
            reportJson = json,
            updatedAt  = now
        )

        return json
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