package com.chaikasoft.app.data.dataSource.mappers

import com.chaikasoft.app.data.dataSource.dto.CarDto
import com.chaikasoft.app.data.dataSource.dto.StationDto
import com.chaikasoft.app.data.dataSource.dto.TripDto
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TripMapperTest : FunSpec({

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Вход: валидный StationDto.
     *   - Ожидаемое поведение: поля code/name/city переносятся 1:1.
     *   - Цель: зафиксировать базовый контракт station-маппера.
     */
    test("maps StationDto to StationDomain") {
        val dto = StationDto(code = "MOW", name = "Moscow", city = "Moscow")

        dto.toDomain() shouldBe StationDomain(code = "MOW", name = "Moscow", city = "Moscow")
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Описание:
     *   - Вход: валидный TripDto с вложенными станциями.
     *   - Ожидаемое поведение: все плоские и вложенные поля маппятся корректно.
     *   - Цель: защитить комплексный маппинг TripDto -> TripDomain.
     */
    test("maps TripDto to TripDomain") {
        val dto = TripDto(
            uuid = "uuid-1",
            trainNumber = "001A",
            departure = "2026-01-01T10:00:00Z",
            arrival = "2026-01-01T12:00:00Z",
            duration = "PT2H",
            from = StationDto(code = "MOW", name = "Moscow", city = "Moscow"),
            to = StationDto(code = "SPB", name = "Saint-Petersburg", city = "Saint-Petersburg"),
        )

        dto.toDomain() shouldBe TripDomain(
            uuid = "uuid-1",
            trainNumber = "001A",
            departure = "2026-01-01T10:00:00Z",
            arrival = "2026-01-01T12:00:00Z",
            duration = "PT2H",
            from = StationDomain(code = "MOW", name = "Moscow", city = "Moscow"),
            to = StationDomain(code = "SPB", name = "Saint-Petersburg", city = "Saint-Petersburg"),
        )
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Вход: валидный CarDto.
     *   - Ожидаемое поведение: carNumber/classType переносятся без изменений.
     *   - Цель: зафиксировать контракт маппинга вагона.
     */
    test("maps CarDto to CarriageDomain") {
        val dto = CarDto(carNumber = "05", classType = "2nd")

        dto.toDomain() shouldBe CarriageDomain(carNumber = "05", classType = "2nd")
    }
})
