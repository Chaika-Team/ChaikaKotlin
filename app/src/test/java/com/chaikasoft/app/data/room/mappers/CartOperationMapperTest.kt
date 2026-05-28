package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.CartOperation
import com.chaikasoft.app.data.room.entities.Conductor
import com.chaikasoft.app.data.room.relations.CartOperationWithConductor
import com.chaikasoft.app.domain.models.CartOperationDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CartOperationMapperTest : FunSpec({

    /**
     * Test design: boundary values.
     * Unknown operation index must fail loudly, otherwise we hide invalid DB state.
     */
    test("toOperationType throws for unknown value") {
        shouldThrow<IllegalArgumentException> {
            999.toOperationType()
        }
    }

    /**
     * Test design: equivalence classes.
     * Enum/int conversion must be stable for all operation types.
     */
    test("operation type converts both ways") {
        OperationTypeDomain.entries.forEachIndexed { index, type ->
            index.toOperationType() shouldBe type
            type.toInt() shouldBe index
        }
    }

    /**
     * Test design: error guessing.
     * Report header must use external employeeID, not internal Room conductor id.
     */
    test("toReportHeader uses relation employeeId") {
        val operation = CartOperation(
            id = 15,
            operationType = OperationTypeDomain.SOLD_CASH.ordinal,
            operationTime = "2026-03-09T10:00:00Z",
            conductorId = 321,
        )

        val withConductor = CartOperationWithConductor(
            operation = operation,
            conductor = Conductor(
                id = 321,
                name = "Ivan",
                familyName = "Petrov",
                givenName = "Ivanovich",
                employeeID = "EMP-777",
                image = "img",
            ),
        )
        withConductor.toReportHeader().cartId.employeeId shouldBe "EMP-777"
    }

    /**
     * Test design: error guessing.
     * Missing conductor relation is invalid report state and must not leak internal PK as employeeID.
     */
    test("toReportHeader throws when conductor relation is missing") {
        val operation = CartOperation(
            id = 15,
            operationType = OperationTypeDomain.SOLD_CASH.ordinal,
            operationTime = "2026-03-09T10:00:00Z",
            conductorId = 321,
        )

        val withoutConductor = CartOperationWithConductor(operation = operation, conductor = null)
        shouldThrow<IllegalStateException> {
            withoutConductor.toReportHeader()
        }.message shouldBe "Conductor not found for operationId=15, conductorId=321"
    }

    /**
     * Test design: equivalence classes.
     * Pair contract must keep operation id and the mapped header.
     */
    test("toReportPair keeps operation id and report header") {
        val relation = CartOperationWithConductor(
            operation = CartOperation(
                id = 42,
                operationType = OperationTypeDomain.REPLENISH.ordinal,
                operationTime = "2026-03-09T10:30:00Z",
                conductorId = 11,
            ),
            conductor = Conductor(
                id = 11,
                name = "Ivan",
                familyName = "Petrov",
                givenName = "Ivanovich",
                employeeID = "EMP-11",
                image = "img",
            ),
        )

        val pair = relation.toReportPair()

        pair.first shouldBe 42
        pair.second.operationType shouldBe OperationTypeDomain.REPLENISH.ordinal
        pair.second.cartId.employeeId shouldBe "EMP-11"
        pair.second.cartId.operationTime shouldBe "2026-03-09T10:30:00Z"
    }

    /**
     * Test design: boundary values.
     * toEntity must set generated id and preserve business fields.
     */
    test("toEntity maps domain fields and sets id to zero") {
        val domain = CartOperationDomain(
            operationTypeDomain = OperationTypeDomain.ADD,
            conductorId = 9,
        )

        val entity = domain.toEntity()

        entity.id shouldBe 0
        entity.operationType shouldBe OperationTypeDomain.ADD.ordinal
        entity.conductorId shouldBe 9
    }
})

