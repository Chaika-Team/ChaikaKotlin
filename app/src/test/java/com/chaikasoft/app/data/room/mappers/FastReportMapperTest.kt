package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.FastReportView
import com.chaikasoft.app.domain.models.FastReportDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FastReportMapperTest : FunSpec({

    /**
     * Test design: equivalence classes.
     * Fast report mapper is 1:1; it should preserve every aggregated field.
     */
    test("toDomain maps all report fields") {
        val view = FastReportView(
            productName = "Tea",
            productPrice = 150,
            addedQuantity = 10,
            replenishedQuantity = 3,
            soldCashQuantity = 5,
            soldCartQuantity = 2,
            revenue = 750,
        )

        view.toDomain() shouldBe FastReportDomain(
            productName = "Tea",
            productPrice = 150,
            addedQuantity = 10,
            replenishedQuantity = 3,
            soldCashQuantity = 5,
            soldCartQuantity = 2,
            revenue = 750,
        )
    }
})

