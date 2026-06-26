package com.chaikasoft.app.diagnostics

import com.chaikasoft.app.domain.common.AppError
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class DiagnosticPolicyTest : FunSpec({
    test("fromAppError reports unknown and serialization errors") {
        val unknown = AppError.Unknown(IllegalStateException("unexpected"))
        val serialization = AppError.Serialization(IllegalStateException("bad_json"))

        DiagnosticPolicy.fromAppError(
            unknown,
            DiagnosticArea.TEMPLATES,
            DiagnosticOperation.RESOLVE_TEMPLATE_DETAIL
        )?.type shouldBe DiagnosticEventType.APP_ERROR_UNKNOWN

        DiagnosticPolicy.fromAppError(
            serialization,
            DiagnosticArea.PRODUCTS,
            DiagnosticOperation.DTO_MAPPING
        )?.type shouldBe DiagnosticEventType.APP_ERROR_SERIALIZATION
    }

    test("fromAppError ignores expected remote failures") {
        listOf(
            AppError.Network,
            AppError.Timeout,
            AppError.Http(code = 500, body = "{\"token\":\"secret\"}"),
            AppError.Unauthorized(code = 401)
        ).forEach { error ->
            DiagnosticPolicy.fromAppError(
                error,
                DiagnosticArea.AUTH,
                DiagnosticOperation.AUTHORIZE
            ).shouldBeNull()
        }
    }
})
