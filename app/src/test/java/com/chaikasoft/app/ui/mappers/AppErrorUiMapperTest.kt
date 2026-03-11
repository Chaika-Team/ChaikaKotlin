package com.chaikasoft.app.ui.mappers

import com.chaikasoft.app.R
import com.chaikasoft.app.domain.common.AppError
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AppErrorUiMapperTest : FunSpec({

    data class Case(
        val error: AppError,
        val messageRes: Int,
        val retryable: Boolean
    )

    val cases = listOf(
        Case(AppError.Network, R.string.error_no_connection, true),
        Case(AppError.Timeout, R.string.error_no_connection, true),
        Case(AppError.Unauthorized(401), R.string.error_unauthorized, false),
        Case(AppError.Http(500), R.string.error_try_later, true),
        Case(AppError.Http(400), R.string.error_try_later, false),
        Case(AppError.Serialization(IllegalStateException("x")), R.string.error_try_later, false),
        Case(AppError.Unknown(IllegalStateException("x")), R.string.error_try_later, false)
    )

    cases.forEach { c ->
        test("maps ${c.error::class.simpleName} to expected UiError") {
            val result = AppErrorUiMapper.map(c.error)

            result.messageRes shouldBe c.messageRes
            result.retryable shouldBe c.retryable
        }
    }
})

