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
     * Fallback employeeId in report header protects us when relation conductor is absent.
     */
    test("toReportHeader uses relation employeeId and falls back to conductor id") {
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
        withConductor.toReportHeader().employeeID shouldBe "EMP-777"

        val withoutConductor = CartOperationWithConductor(operation = operation, conductor = null)
        withoutConductor.toReportHeader().employeeID shouldBe "321"
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
            conductor = null,
        )

        val pair = relation.toReportPair()

        pair.first shouldBe 42
        pair.second.operationType shouldBe OperationTypeDomain.REPLENISH.ordinal
        pair.second.employeeID shouldBe "11"
        pair.second.items shouldBe emptyList()
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

