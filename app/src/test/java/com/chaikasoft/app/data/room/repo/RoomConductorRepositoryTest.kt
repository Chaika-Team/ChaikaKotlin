package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.data.room.dao.ConductorDao
import com.chaikasoft.app.data.room.entities.Conductor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class RoomConductorRepositoryTest : FunSpec({

    lateinit var dao: ConductorDao
    lateinit var repository: RoomConductorRepository

    beforeTest {
        dao = mockk(relaxed = true)
        repository = RoomConductorRepository(dao)
    }

    /**
     * Test design: equivalence classes.
     * Repository should return employee id for existing conductor id.
     */
    test("getEmployeeIDByConductorId returns value when conductor exists") {
        runTest {
            every { dao.getAllConductors() } returns flowOf(
                listOf(
                    Conductor(1, "A", "B", "C", "EMP-1", "img"),
                    Conductor(2, "D", "E", "F", "EMP-2", "img2"),
                ),
            )

            repository.getEmployeeIDByConductorId(2) shouldBe "EMP-2"
        }
    }

    /**
     * Test design: boundary values.
     * Missing conductor id should fail loudly to protect report generation.
     */
    test("getEmployeeIDByConductorId throws when conductor does not exist") {
        runTest {
            every { dao.getAllConductors() } returns flowOf(
                listOf(Conductor(1, "A", "B", "C", "EMP-1", "img")),
            )

            shouldThrow<IllegalArgumentException> {
                repository.getEmployeeIDByConductorId(999)
            }
        }
    }
})

