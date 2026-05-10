package com.chaikasoft.app.e2e.fixtures

import com.chaikasoft.app.domain.models.ConductorDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.models.TemplateContentDomain
import com.chaikasoft.app.domain.models.TemplateDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain

object E2EFixtures {
    const val AUTH_TOKEN = "e2e-auth-token"

    val conductor = ConductorDomain(
        id = null,
        name = "Екатерина",
        familyName = "Смирнова",
        givenName = "Андреевна",
        employeeID = "E2E-1001",
        image = "",
    )

    val stations = listOf(
        StationDomain(code = "2001001", name = "СУМСКИЙ ПОСАД", city = "Сумский Посад"),
        StationDomain(code = "2001002", name = "САВЕЛОВСКАЯ", city = "Москва"),
        StationDomain(code = "2001003", name = "САНКТ-ПЕТЕРБУРГ-ГЛАВНЫЙ", city = "Санкт-Петербург"),
        StationDomain(code = "2001004", name = "МОСКВА ОКТЯБРЬСКАЯ", city = "Москва"),
    )

    val products = listOf(
        ProductInfoDomain(
            id = 1,
            name = "Чай черный",
            description = "Пакетированный чай",
            image = "",
            price = 15000,
        ),
        ProductInfoDomain(
            id = 2,
            name = "Вода негазированная",
            description = "Бутылка 0.5",
            image = "",
            price = 12000,
        ),
    )

    val templates = listOf(
        TemplateDomain(
            id = 1,
            templateName = "Базовый",
            description = "Базовый шаблон",
            content = listOf(
                TemplateContentDomain(productId = 1, quantity = 2),
                TemplateContentDomain(productId = 2, quantity = 3),
            ),
        ),
    )

    val trips = listOf(
        TripDomain(
            uuid = "trip-e2e-1",
            trainNumber = "012А",
            departure = "2026-04-11T08:00:00+03:00",
            arrival = "2026-04-11T12:45:00+03:00",
            duration = "PT4H45M",
            from = stations.first(),
            to = stations[2],
        ),
    )
}
