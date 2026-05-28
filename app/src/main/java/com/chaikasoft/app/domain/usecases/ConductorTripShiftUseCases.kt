package com.chaikasoft.app.domain.usecases

import android.util.Log
import com.chaikasoft.app.data.datasource.repo.ChaikaSoftReportsRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomCartItemRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomCartOperationRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomConductorTripShiftRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomShiftReportRepositoryInterface
import com.chaikasoft.app.domain.models.report.CartIdReport
import com.chaikasoft.app.domain.models.report.CartReport
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.chaikasoft.app.domain.sealed.SendReportResult
import com.chaikasoft.app.domain.sealed.UploadResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

private const val SEND_TAG = "SHIFT-REPORT"

/** Возвращает поток завершённых смен для истории, исключая текущую ACTIVE-смену. */
class GetShiftHistoryUseCase @Inject constructor(
    private val repository: RoomConductorTripShiftRepositoryInterface
) {
    operator fun invoke(): Flow<List<ConductorTripShiftDomain>> = repository.observeShiftHistory()
}

/**
 * Возвращает поток с единственной активной сменой, либо null, когда нет активной.
 */
class GetActiveShiftUseCase @Inject constructor(
    private val repository: RoomConductorTripShiftRepositoryInterface
) {
    operator fun invoke(): Flow<ConductorTripShiftDomain?> = repository.observeActiveShift()
}

/**
 * Возвращает true, если есть активная смена в момент вызова.
 */
class HasActiveShiftUseCase @Inject constructor(
    private val repository: RoomConductorTripShiftRepositoryInterface
) {
    /** true, если в БД уже есть активная смена на момент вызова */
    suspend operator fun invoke(): Boolean = repository.getActiveShift() != null
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
    suspend operator fun invoke(trip: TripDomain, activeCarriage: CarriageDomain?): Boolean {
        // не допускаем более одной активной смены
        // Быстрый путь, чтобы не ловить exception в обычном сценарии
        if (hasActiveShift()) return false

        val newShift = ConductorTripShiftDomain(
            trip = trip,
            activeCarriage = activeCarriage,
            status = TripShiftStatusDomain.ACTIVE
        )
        return repository.tryStartNewShift(newShift)
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
        } catch (t: IllegalStateException) {
            Log.e(
                SEND_TAG,
                "CompleteShiftAndSendUseCase: generation FAILED for uuid=$uuid, ${t.message}",
                t
            )
            throw t
        }
        val res = send(uuid)
        Log.d(SEND_TAG, "CompleteShiftAndSendUseCase: send() returned $res for uuid=$uuid")
        return res
    }
}

/**
 * Атомарно генерирует JSON-отчёт по смене, сохраняет его в `conductor_trip_shifts.report`,
 * переводит смену в FINISHED и очищает операции через data layer.
 */
class GenerateShiftReportUseCase @Inject constructor(
    private val shiftReportRepo: RoomShiftReportRepositoryInterface
) {
    suspend operator fun invoke(uuid: String): String = shiftReportRepo.finishShiftWithReport(uuid)
}

/**
 * Собирает список операций корзины для JSON-отчёта смены.
 *
 * Алгоритм:
 * 1) берёт из CartOperationRepository пары (opId, CartOperationReportHeader);
 * 2) для каждого opId загружает товарные строки из CartItemRepository;
 * 3) собирает финальный List<CartReport>, который уже сериализуется внутри ShiftReportReport.
 */
class GetCartReportsUseCase @Inject constructor(
    private val cartOpRepo: RoomCartOperationRepositoryInterface,
    private val cartItemRepo: RoomCartItemRepositoryInterface
) {
    suspend operator fun invoke(): List<CartReport> {
        // 1) операции
        val ops = cartOpRepo
            .getCartOperationReportHeadersWithIds()
            .first()

        // 2) маппим каждую
        return ops.map { (opId, opHeader) ->
            // 2.1) товары
            val items = cartItemRepo
                .getCartItemReportsByOperationId(opId)
                .first()

            // 2.2) финальный CartReport
            CartReport(
                cartId = CartIdReport(
                    employeeId = opHeader.cartId.employeeId,
                    operationTime = opHeader.cartId.operationTime
                ),
                operationType = opHeader.operationType,
                items = items
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
        Log.d(
            SEND_TAG,
            "GetShiftReportJsonUseCase: uuid=$uuid, status=$status, reportPresent=${!json.isNullOrBlank()}, reportLen=${json?.length ?: 0}"
        )
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
                Log.w(
                    SEND_TAG,
                    "UploadShiftReportUseCase: HTTP ${repoRes.code}, errSnippet=$bodySnippet"
                )
                when (repoRes.code) {
                    409 -> SendReportResult.Success

                    // идемпотентность
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
            newStatus = TripShiftStatusDomain.SENT.code,
            updatedAt = System.currentTimeMillis()
        )
        Log.d(SEND_TAG, "MarkShiftSentUseCase: done, uuid=$uuid")
    }
}

/**
 * Отправляет отчёт по поездке (так же используется для ретрая отправки)
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
        Log.d(
            SEND_TAG,
            "SendShiftReportUseCase: currentStatus=$status, hasReport=${!reportJson.isNullOrBlank()}, reportLen=${reportJson?.length ?: 0}"
        )

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
