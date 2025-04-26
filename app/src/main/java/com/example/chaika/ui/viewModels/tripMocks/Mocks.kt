package com.example.chaika.ui.viewModels.tripMocks


import com.example.chaika.domain.models.trip.StationDomain
import com.example.chaika.domain.models.trip.TripDomain

fun fetchAndSaveHistoryUseCase(): List<TripDomain> {
    return listOf(
        TripDomain(
            uuid = "12",
            trainNumber = "120A",
            departure = "2025-03-29T23:55:00+03:00",
            arrival = "2025-03-30T09:47:00+03:00",
            duration = "PT9H52M",
            from = StationDomain(
                code = 1,
                name = "Московский вокзал",
                city = "Санкт-Петербург-Главный"
            ),
            to = StationDomain(
                code = 2,
                name = "ТПУ Черкизово",
                city = "Москва ВК Восточный"
            )
        ),
        TripDomain(
            uuid = "12",
            trainNumber = "120A",
            departure = "2025-03-29T23:55:00+03:00",
            arrival = "2025-03-30T09:47:00+03:00",
            duration = "PT9H52M",
            from = StationDomain(
                code = 1,
                name = "Московский вокзал",
                city = "Санкт-Петербург-Главный"
            ),
            to = StationDomain(
                code = 2,
                name = "ТПУ Черкизово",
                city = "Москва ВК Восточный"
            )
        ),
        TripDomain(
            uuid = "12",
            trainNumber = "120A",
            departure = "2025-03-29T23:55:00+03:00",
            arrival = "2025-03-30T09:47:00+03:00",
            duration = "PT9H52M",
            from = StationDomain(
                code = 1,
                name = "Московский вокзал",
                city = "Санкт-Петербург-Главный"
            ),
            to = StationDomain(
                code = 2,
                name = "ТПУ Черкизово",
                city = "Москва ВК Восточный"
            )
        ),
        TripDomain(
            uuid = "12",
            trainNumber = "120A",
            departure = "2025-03-29T23:55:00+03:00",
            arrival = "2025-03-30T09:47:00+03:00",
            duration = "PT9H52M",
            from = StationDomain(
                code = 1,
                name = "Московский вокзал",
                city = "Санкт-Петербург-Главный"
            ),
            to = StationDomain(
                code = 2,
                name = "ТПУ Черкизово",
                city = "Москва ВК Восточный"
            )
        ),
        TripDomain(
            uuid = "12",
            trainNumber = "120A",
            departure = "2025-03-29T23:55:00+03:00",
            arrival = "2025-03-30T09:47:00+03:00",
            duration = "PT9H52M",
            from = StationDomain(
                code = 1,
                name = "Московский вокзал",
                city = "Санкт-Петербург-Главный"
            ),
            to = StationDomain(
                code = 2,
                name = "ТПУ Черкизово",
                city = "Москва ВК Восточный"
            )
        ),
        TripDomain(
            uuid = "12",
            trainNumber = "120A",
            departure = "2025-03-29T23:55:00+03:00",
            arrival = "2025-03-30T09:47:00+03:00",
            duration = "PT9H52M",
            from = StationDomain(
                code = 1,
                name = "Московский вокзал",
                city = "Санкт-Петербург-Главный"
            ),
            to = StationDomain(
                code = 2,
                name = "ТПУ Черкизово",
                city = "Москва ВК Восточный"
            )
        )
    )
}
