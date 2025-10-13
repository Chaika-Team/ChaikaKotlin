package com.chaikasoft.app.domain.usecases

import com.chaikasoft.app.data.dataSource.repo.ChaikaSoftReportsRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomCartItemRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomCartOperationRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomConductorTripShiftRepositoryInterface
import com.chaikasoft.app.domain.models.report.CartIdReport
import com.chaikasoft.app.domain.models.report.CartItemReport
import com.chaikasoft.app.domain.models.report.CartReport
import com.chaikasoft.app.domain.models.report.ShiftReportReport
import com.chaikasoft.app.domain.models.report.TripIdReport
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.chaikasoft.app.domain.sealed.SendReportResult
import com.chaikasoft.app.domain.sealed.UploadResult
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import android.util.Log

private const val SEND_TAG = "SHIFT-REPORT"

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
    private val repository: RoomConductorTripShiftRepositoryInterface
) {
    /** true, если в БД уже есть активная смена на момент вызова */
    suspend operator fun invoke(): Boolean =
        repository.getActiveShift() != null
}

/**
 * Пытается создать новую смену (ACTIVE) из переданных TripDomain и CarriageDomain.
 * Если в базе уже есть активная смена, ничего не делает и возвращает false.
 * Иначе создаёт новую смену со статусом ACTIVE и возвращает true.
 */
class StartShiftUseCase @Inject constructor(
    private val repository: RoomConductorTripShiftRepositoryInterface,
    private val hasActiveShift: HasActiveShiftUseCase
) {
    suspend operator fun invoke(
        trip: TripDomain,
        activeCarriage: CarriageDomain?
    ): Boolean {
        // не допускаем более одной активной смены
        if (hasActiveShift()) return false

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
 * Генерирует отчёт и сразу пытается его отправить.
 * Возвращает структурированный результат отправки (без UI-строк).
 */
class CompleteShiftAndSendUseCase @Inject constructor(
    private val generate: GenerateShiftReportUseCase,
    private val send: SendShiftReportUseCase
) {
    suspend operator fun invoke(uuid: String): SendReportResult {
        Log.d(SEND_TAG, "CompleteShiftAndSendUseCase: start, uuid=$uuid")
        try {
            generate(uuid)
            Log.d(SEND_TAG, "CompleteShiftAndSendUseCase: report generated for uuid=$uuid")
        } catch (t: Throwable) {
            Log.e(SEND_TAG, "CompleteShiftAndSendUseCase: generation FAILED for uuid=$uuid, ${t.message}", t)
            throw t
        }
        val res = send(uuid)
        Log.d(SEND_TAG, "CompleteShiftAndSendUseCase: send() returned $res for uuid=$uuid")
        return res
    }
}

/**
 * Генерирует JSON-отчёт по смене и сохраняет его в поле `report` таблицы `conductor_trip_shifts`.
 */
class GenerateShiftReportUseCase @Inject constructor(
    private val shiftRepo: RoomConductorTripShiftRepositoryInterface,
    private val getCartReports: GetCartReportsUseCase,
    moshi: Moshi,
    private val clearOpsAndPackage: ClearOperationsAndPackageUseCase
) {
    private val jsonAdapter = moshi.adapter(ShiftReportReport::class.java)

    suspend operator fun invoke(uuid: String): String {
        val shift = shiftRepo.getActiveShift()
            ?: throw IllegalStateException("Active shift with uuid=$uuid not found")

        // 1) Собираем CartReport'ы из текущих операций (пока они ещё в БД)
        val carts = getCartReports()
        // 2) Формируем итоговый отчёт
        val report = ShiftReportReport(
            tripId     = TripIdReport(shift.trip.trainNumber, shift.trip.departure),
            endTime    = shift.trip.arrival,
            carriageId = shift.activeCarriage
                ?.carNumber?.toIntOrNull()
                ?: throw IllegalStateException("Active carriage not set"),
            carts      = carts
        )
        // 3) Фиксируем в БД: статус FINISHED + сам JSON
        val json = jsonAdapter.toJson(report)
        shiftRepo.updateStatusAndReport(
            uuid       = uuid,
            newStatus  = TripShiftStatusDomain.FINISHED.ordinal,
            reportJson = json,
            updatedAt  = System.currentTimeMillis()
        )
        // 4) Сразу же очищаем операции → «пакет» пустой
        clearOpsAndPackage()
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
 * Получить отчёт по поездке из базы данных
 */
class GetShiftReportJsonUseCase @Inject constructor(
    private val repo: RoomConductorTripShiftRepositoryInterface
) {
    suspend operator fun invoke(uuid: String): Pair<TripShiftStatusDomain, String?> {
        val (status, json) = repo.getStatusAndReport(uuid)
        Log.d(SEND_TAG, "GetShiftReportJsonUseCase: uuid=$uuid, status=$status, reportPresent=${!json.isNullOrBlank()}, reportLen=${json?.length ?: 0}")
        return status to json
    }
}

/**
 * Отправить JSON в API, разрулить коды
 */
class UploadShiftReportUseCase @Inject constructor(
    private val repo: ChaikaSoftReportsRepositoryInterface
) {
    suspend operator fun invoke(json: String): SendReportResult {
        Log.d(SEND_TAG, "UploadShiftReportUseCase: uploading jsonLen=${json.length}")
        val r = when (val repoRes = repo.uploadShiftReport(json)) {
            is UploadResult.Ok -> {
                Log.d(SEND_TAG, "UploadShiftReportUseCase: server OK (2xx)")
                SendReportResult.Success
            }
            is UploadResult.HttpError -> {
                val bodySnippet = repoRes.body?.take(256)
                Log.w(SEND_TAG, "UploadShiftReportUseCase: HTTP ${repoRes.code}, errSnippet=$bodySnippet")
                when (repoRes.code) {
                    409 -> SendReportResult.Success // идемпотентность
                    in 400..499 -> SendReportResult.PermanentFailure(repoRes.code, repoRes.body)
                    else -> SendReportResult.TemporaryFailure(httpCode = repoRes.code)
                }
            }
            is UploadResult.NetworkError -> {
                Log.w(SEND_TAG, "UploadShiftReportUseCase: network error: $repoRes")
                SendReportResult.TemporaryFailure(isNetwork = true)
            }
        }
        Log.d(SEND_TAG, "UploadShiftReportUseCase: mapped result=$r")
        return r
    }
}

/**
 * Пометить смену «отправлено»
 */
class MarkShiftSentUseCase @Inject constructor(
    private val repo: RoomConductorTripShiftRepositoryInterface
) {
    suspend operator fun invoke(uuid: String) {
        Log.d(SEND_TAG, "MarkShiftSentUseCase: marking SENT, uuid=$uuid")
        repo.updateStatusAndReport(
            uuid = uuid,
            newStatus = TripShiftStatusDomain.SENT.ordinal,
            reportJson = null,
            updatedAt = System.currentTimeMillis()
        )
        Log.d(SEND_TAG, "MarkShiftSentUseCase: done, uuid=$uuid")
    }
}
/**
 * Отправляет отчёт по поездке
 */
class SendShiftReportUseCase @Inject constructor(
    private val getReport: GetShiftReportJsonUseCase,
    private val upload: UploadShiftReportUseCase,
    private val markSent: MarkShiftSentUseCase
) {
    /**
     * @return SendReportResult:
     *  - Success / AlreadySent / MissingReport / TemporaryFailure / PermanentFailure
     */
    suspend operator fun invoke(uuid: String): SendReportResult {
        Log.d(SEND_TAG, "SendShiftReportUseCase: start, uuid=$uuid")
        val (status, reportJson) = getReport(uuid)
        Log.d(SEND_TAG, "SendShiftReportUseCase: currentStatus=$status, hasReport=${!reportJson.isNullOrBlank()}, reportLen=${reportJson?.length ?: 0}")

        if (status == TripShiftStatusDomain.SENT) {
            Log.d(SEND_TAG, "SendShiftReportUseCase: already SENT, skipping upload, uuid=$uuid")
            return SendReportResult.AlreadySent
        }
        if (reportJson.isNullOrBlank()) {
            Log.w(SEND_TAG, "SendShiftReportUseCase: Missing report, uuid=$uuid")
            return SendReportResult.MissingReport
        }

        Log.d(SEND_TAG, "SendShiftReportUseCase: calling upload..., uuid=$uuid")
        val result = upload(reportJson)
        Log.d(SEND_TAG, "SendShiftReportUseCase: upload result=$result, uuid=$uuid")

        if (result is SendReportResult.Success) {
            Log.d(SEND_TAG, "SendShiftReportUseCase: marking SENT, uuid=$uuid")
            markSent(uuid)
            Log.d(SEND_TAG, "SendShiftReportUseCase: SENT marked, uuid=$uuid")
        }
        return result
    }

}