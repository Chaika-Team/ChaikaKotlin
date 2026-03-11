package com.chaikasoft.app.domain.usecases.conductorTripShiftUseCases

import com.chaikasoft.app.data.room.repo.RoomConductorTripShiftRepositoryInterface
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.chaikasoft.app.domain.usecases.HasActiveShiftUseCase
import com.chaikasoft.app.domain.usecases.StartShiftUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest


class StartShiftUseCaseTest : FunSpec({

    lateinit var repository: RoomConductorTripShiftRepositoryInterface
    lateinit var hasActiveShift: HasActiveShiftUseCase
    lateinit var useCase: StartShiftUseCase

    val trip = TripDomain(
        uuid = "trip-uuid-123",
        trainNumber = "042M",
        departure = "2025-05-01T08:00:00Z",
        arrival = "2025-05-01T12:00:00Z",
        duration = "PT4H",
        from = StationDomain(code = "MSK", name = "Moscow", city = "Moscow"),
        to = StationDomain(code = "SPB", name = "Saint-Petersburg", city = "Saint-Petersburg")
    )
    val carriage = CarriageDomain(carNumber = "05", classType = "first")

    // Аналог @BeforeEach: выполняется перед каждым test(...)
    beforeTest {
        repository = mockk()
        hasActiveShift = mockk()
        useCase = StartShiftUseCase(repository, hasActiveShift)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Эквивалентный класс входных данных: активная смена уже существует.
     *   - Ожидаемое поведение:
     *       1) юзкейс возвращает false,
     *       2) не пытается создать новую смену (tryStartNewShift не вызывается).
     *   - Цель: зафиксировать инвариант "не более одной ACTIVE-смены".
     */
    test("when active shift already exists - returns false and does not start new shift") {
        runTest {
            coEvery { hasActiveShift() } returns true

            val result = useCase(trip, carriage)

            result shouldBe false

            coVerify(exactly = 1) { hasActiveShift() }
            coVerify(exactly = 0) { repository.tryStartNewShift(any()) }
            confirmVerified(hasActiveShift, repository)
        }
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений (Decision Table)
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Комбинация условий:
     *       activeShift = false, activeCarriage = present, repoResult = true
     *   - Ожидаемое поведение:
     *       1) создается новая смена со статусом ACTIVE,
     *       2) в смену прокидываются trip и activeCarriage,
     *       3) результат repo пробрасывается наружу.
     *   - Цель: зафиксировать корректное формирование новой ACTIVE-смены.
     */
    test("when no active shift and repo succeeds - creates ACTIVE shift with carriage") {
        runTest {
            val shiftSlot = slot<ConductorTripShiftDomain>()
            coEvery { hasActiveShift() } returns false
            coEvery { repository.tryStartNewShift(capture(shiftSlot)) } returns true

            val result = useCase(trip, carriage)

            result shouldBe true
            shiftSlot.captured.trip shouldBe trip
            shiftSlot.captured.activeCarriage shouldBe carriage
            shiftSlot.captured.status shouldBe TripShiftStatusDomain.ACTIVE

            coVerify(exactly = 1) { hasActiveShift() }
            coVerify(exactly = 1) { repository.tryStartNewShift(any()) }
            confirmVerified(hasActiveShift, repository)
        }
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Граница по данным вагона: activeCarriage = null.
     *   - Ожидаемое поведение:
     *       1) юзкейс все равно пытается создать ACTIVE-смену,
     *       2) null переносится в shift.activeCarriage,
     *       3) результат repo возвращается без модификации.
     *   - Цель: зафиксировать корректную работу без выбранного вагона.
     */
    test("when no active shift and carriage is null - still starts with null carriage") {
        runTest {
            val shiftSlot = slot<ConductorTripShiftDomain>()
            coEvery { hasActiveShift() } returns false
            coEvery { repository.tryStartNewShift(capture(shiftSlot)) } returns false

            val result = useCase(trip, null)

            result shouldBe false
            shiftSlot.captured.trip shouldBe trip
            shiftSlot.captured.activeCarriage shouldBe null
            shiftSlot.captured.status shouldBe TripShiftStatusDomain.ACTIVE

            coVerify(exactly = 1) { hasActiveShift() }
            coVerify(exactly = 1) { repository.tryStartNewShift(any()) }
            confirmVerified(hasActiveShift, repository)
        }
    }
})
