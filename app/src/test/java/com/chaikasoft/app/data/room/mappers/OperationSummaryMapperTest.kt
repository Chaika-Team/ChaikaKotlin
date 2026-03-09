package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.OperationInfoView
import com.chaikasoft.app.domain.models.OperationTypeDomain
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class OperationSummaryMapperTest : FunSpec({

    /**
     * Test design: equivalence classes.
     * Mapper must keep operation payload and build conductor object with expected defaults.
     */
    test("toDomain maps OperationInfoView to OperationSummaryDomain") {
        val view = OperationInfoView(
            operationId = 10,
            operationType = OperationTypeDomain.SOLD_CASH.ordinal,
            operationTime = "2026-03-09T12:00:00Z",
            conductorId = 33,
            conductorName = "Pavel",
            conductorFamilyName = "Sidorov",
            conductorGivenName = "Sergeevich",
            productLineQuantity = 2,
            totalPrice = 900,
        )

        val domain = view.toDomain()

        domain.id shouldBe 10
        domain.type shouldBe OperationTypeDomain.SOLD_CASH
        domain.timeIso shouldBe "2026-03-09T12:00:00Z"
        domain.conductor.id shouldBe 33
        domain.conductor.name shouldBe "Pavel"
        domain.conductor.employeeID shouldBe ""
        domain.conductor.image shouldBe ""
        domain.productLineQuantity shouldBe 2
        domain.totalPrice shouldBe 900
    }

    /**
     * Test design: boundary values.
     * Unknown operation type from DB should not be silently accepted.
     */
    test("toDomain throws when operation type is unknown") {
        val view = OperationInfoView(
            operationId = 1,
            operationType = 99,
            operationTime = "2026-03-09T12:00:00Z",
            conductorId = 1,
            conductorName = "A",
            conductorFamilyName = "B",
            conductorGivenName = "C",
            productLineQuantity = 0,
            totalPrice = 0,
        )

        shouldThrow<IllegalArgumentException> {
            view.toDomain()
        }
    }
})

