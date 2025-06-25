package com.example.chaika.ui.viewModels.tripMocks

import com.example.chaika.domain.models.trip.StationDomain
import com.example.chaika.domain.models.trip.TripDomain

private const val MOCK_UUID = "12"
private const val MOCK_TRAIN_NUMBER = "120A"
private const val MOCK_DEPARTURE = "2025-03-29T23:55:00+03:00"
private const val MOCK_ARRIVAL = "2025-03-30T09:47:00+03:00"
private const val MOCK_DURATION = "PT9H52M"
private const val MOCK_FROM_NAME = "Московский вокзал"
private const val MOCK_FROM_CITY = "Санкт-Петербург-Главный"
private const val MOCK_TO_NAME = "ТПУ Черкизово"
private const val MOCK_TO_CITY = "Москва ВК Восточный"
private const val MOCK_FROM_CODE = 1
private const val MOCK_TO_CODE = 2

fun fetchAndSaveHistoryUseCase(): List<TripDomain> {
    return listOf(
        TripDomain(
            uuid = MOCK_UUID,
            trainNumber = MOCK_TRAIN_NUMBER,
            departure = MOCK_DEPARTURE,
            arrival = MOCK_ARRIVAL,
            duration = MOCK_DURATION,
            from = StationDomain(
                code = MOCK_FROM_CODE,
                name = MOCK_FROM_NAME,
                city = MOCK_FROM_CITY
            ),
            to = StationDomain(
                code = MOCK_TO_CODE,
                name = MOCK_TO_NAME,
                city = MOCK_TO_CITY
            )
        ),
        TripDomain(
            uuid = MOCK_UUID,
            trainNumber = MOCK_TRAIN_NUMBER,
            departure = MOCK_DEPARTURE,
            arrival = MOCK_ARRIVAL,
            duration = MOCK_DURATION,
            from = StationDomain(
                code = MOCK_FROM_CODE,
                name = MOCK_FROM_NAME,
                city = MOCK_FROM_CITY
            ),
            to = StationDomain(
                code = MOCK_TO_CODE,
                name = MOCK_TO_NAME,
                city = MOCK_TO_CITY
            )
        ),
        TripDomain(
            uuid = MOCK_UUID,
            trainNumber = MOCK_TRAIN_NUMBER,
            departure = MOCK_DEPARTURE,
            arrival = MOCK_ARRIVAL,
            duration = MOCK_DURATION,
            from = StationDomain(
                code = MOCK_FROM_CODE,
                name = MOCK_FROM_NAME,
                city = MOCK_FROM_CITY
            ),
            to = StationDomain(
                code = MOCK_TO_CODE,
                name = MOCK_TO_NAME,
                city = MOCK_TO_CITY
            )
        ),
        TripDomain(
            uuid = MOCK_UUID,
            trainNumber = MOCK_TRAIN_NUMBER,
            departure = MOCK_DEPARTURE,
            arrival = MOCK_ARRIVAL,
            duration = MOCK_DURATION,
            from = StationDomain(
                code = MOCK_FROM_CODE,
                name = MOCK_FROM_NAME,
                city = MOCK_FROM_CITY
            ),
            to = StationDomain(
                code = MOCK_TO_CODE,
                name = MOCK_TO_NAME,
                city = MOCK_TO_CITY
            )
        ),
        TripDomain(
            uuid = MOCK_UUID,
            trainNumber = MOCK_TRAIN_NUMBER,
            departure = MOCK_DEPARTURE,
            arrival = MOCK_ARRIVAL,
            duration = MOCK_DURATION,
            from = StationDomain(
                code = MOCK_FROM_CODE,
                name = MOCK_FROM_NAME,
                city = MOCK_FROM_CITY
            ),
            to = StationDomain(
                code = MOCK_TO_CODE,
                name = MOCK_TO_NAME,
                city = MOCK_TO_CITY
            )
        ),
        TripDomain(
            uuid = MOCK_UUID,
            trainNumber = MOCK_TRAIN_NUMBER,
            departure = MOCK_DEPARTURE,
            arrival = MOCK_ARRIVAL,
            duration = MOCK_DURATION,
            from = StationDomain(
                code = MOCK_FROM_CODE,
                name = MOCK_FROM_NAME,
                city = MOCK_FROM_CITY
            ),
            to = StationDomain(
                code = MOCK_TO_CODE,
                name = MOCK_TO_NAME,
                city = MOCK_TO_CITY
            )
        )
    )
}
