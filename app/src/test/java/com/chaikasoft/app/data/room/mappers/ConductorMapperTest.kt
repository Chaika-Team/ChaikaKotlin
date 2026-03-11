package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.Conductor
import com.chaikasoft.app.domain.models.ConductorDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ConductorMapperTest : FunSpec({

    /**
     * Test design: boundary values.
     * toEntity must fallback id=null to 0 for Room auto-generation.
     */
    test("conductor mapper keeps fields and uses id fallback") {
        val withNullId = ConductorDomain(
            id = null,
            name = "Ivan",
            familyName = "Petrov",
            givenName = "Ivanovich",
            employeeID = "EMP-1",
            image = "img",
        )

        val entity = withNullId.toEntity()
        entity.id shouldBe 0
        entity.toDomain() shouldBe withNullId.copy(id = 0)
    }

    /**
     * Test design: equivalence classes.
     * Existing id should stay unchanged through mapping.
     */
    test("conductor mapper keeps explicit id") {
        val domain = ConductorDomain(
            id = 9,
            name = "Petr",
            familyName = "Sidorov",
            givenName = "Petrovich",
            employeeID = "EMP-9",
            image = "img2",
        )

        domain.toEntity().id shouldBe 9
        Conductor(
            id = 9,
            name = "Petr",
            familyName = "Sidorov",
            givenName = "Petrovich",
            employeeID = "EMP-9",
            image = "img2",
        ).toDomain() shouldBe domain
    }
})

