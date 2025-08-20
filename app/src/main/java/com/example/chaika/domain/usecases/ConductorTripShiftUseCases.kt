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
import kotlinx.coroutines.flow.firstOrNull
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
 * Возвращает true, если есть активная смена в момент вызова.
 */
class HasActiveShiftUseCase @Inject constructor(
    private val getActiveShiftUseCase: GetActiveShiftUseCase
) {
    suspend operator fun invoke(): Boolean {
        return getActiveShiftUseCase()
            .firstOrNull() != null
    }
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
    private val getCartReports: GetCartReportsUseCase,
    moshi: Moshi
) {
    private val jsonAdapter = moshi.adapter(ShiftReportReport::class.java)

    suspend operator fun invoke(uuid: String): String {
        val shift = shiftRepo.getActiveShift()
            ?: throw IllegalStateException("Active shift with uuid=$uuid not found")

        // Вот здесь стало сразу по делу:
        val carts = getCartReports()

        val report = ShiftReportReport(
            tripId     = TripIdReport(shift.trip.trainNumber, shift.trip.departure),
            endTime    = shift.trip.arrival,
            carriageId = shift.activeCarriage
                ?.carNumber?.toIntOrNull()
                ?: throw IllegalStateException("Active carriage not set"),
            carts      = carts
        )

        val json = jsonAdapter.toJson(report)
        shiftRepo.updateStatusAndReport(
            uuid       = uuid,
            newStatus  = TripShiftStatusDomain.FINISHED.ordinal,
            reportJson = json,
            updatedAt  = System.currentTimeMillis()
        )
        return json
    }
}


/**
 * вспомогательный юзкейс сборки отчёта:
 * 1) берёт из CartOperationRepository пары (opId, CartOperationReport);
 * 2) для каждого opId запрашивает товары из CartItemRepository;
 * 3) маппит всё в List<CartReport>.
 */
class GetCartReportsUseCase @Inject constructor(
    private val cartOpRepo: RoomCartOperationRepositoryInterface,
    private val cartItemRepo: RoomCartItemRepositoryInterface
) {
    suspend operator fun invoke(): List<CartReport> {
        // 1) операции
        val ops = cartOpRepo
            .getCartOperationReportsWithIds()
            .first()

        // 2) маппим каждую
        return ops.map { (opId, opReport) ->
            // 2.1) товары
            val items = cartItemRepo
                .getCartItemReportsByOperationId(opId)
                .first()
                .map { itemReport ->
                    CartItemReport(
                        productId = itemReport.productId,
                        quantity  = itemReport.quantity,
                        price     = itemReport.price
                    )
                }

            // 2.2) финальный CartReport
            CartReport(
                cartId        = CartIdReport(
                    employeeId    = opReport.employeeID,
                    operationTime = opReport.operationTime
                ),
                operationType = opReport.operationType,
                items         = items
            )
        }
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