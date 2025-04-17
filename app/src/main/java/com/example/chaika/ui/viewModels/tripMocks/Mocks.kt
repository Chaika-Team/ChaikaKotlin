package com.example.chaika.ui.viewModels.tripMocks

import androidx.paging.PagingData
import com.example.chaika.ui.dto.Carriage
import com.example.chaika.ui.dto.TripRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

// Mock implementation of fetchAndSaveHistoryUseCase
fun fetchAndSaveHistoryUseCase(): List<TripRecord> {
    // Mock data - in real app this would fetch from API and save to DB
    return listOf(
        TripRecord(
            routeID = 0,
            trainId = "119A",
            startTime = LocalDateTime.parse("2024-03-30T00:12:00"),
            endTime = LocalDateTime.parse("2024-03-30T09:47:00"),
            carriageID = 33,
            startName1 = "Московский вокзал",
            startName2 = "Санкт-Петербург-Главный",
            endName1 = "ТПУ черкизово",
            endName2 = "Москва ВК Восточный"
        )
    )
}

// Mock implementation of getPagedHistoryUseCase
fun getPagedHistoryUseCase(pageSize: Int): Flow<PagingData<TripRecord>> {
    // Generate mock trip records
    val mockTrips = (1..100).map { id ->
        TripRecord(
            routeID = id,
            trainId = "TR${(100 + id)}",
            startTime = LocalDateTime.now().minusDays(id.toLong()),
            endTime = LocalDateTime.now().minusDays(id.toLong()).plusHours(5),
            carriageID = id % 10 + 1,
            startName1 = "Московский вокзал",
            startName2 = "Санкт-Петербург-Главный",
            endName1 = "ТПУ черкизово",
            endName2 = "Москва ВК Восточный"
        )
    }

    return flow {
        emit(PagingData.from(mockTrips))
    }
}

fun getPagedFutureTripsUseCase(pageSize: Int): Flow<PagingData<TripRecord>> {
    // Generate mock trip records
    val mockTrips = (1..100).map { id ->
        TripRecord(
            routeID = id,
            trainId = "TR${(100 + id)}",
            startTime = LocalDateTime.now().minusDays(id.toLong()),
            endTime = LocalDateTime.now().minusDays(id.toLong()).plusHours(5),
            carriageID = id % 10 + 1,
            startName1 = "Московский вокзал",
            startName2 = "Санкт-Петербург-Главный",
            endName1 = "ТПУ черкизово",
            endName2 = "Москва ВК Восточный"
        )
    }

    return flow {
        emit(PagingData.from(mockTrips))
    }
}

fun getPagedCarriagesUseCase(pageSize: Int, trainId: String): Flow<PagingData<Carriage>> {
    val mockCarriages = (1..30).map { id ->
        Carriage(
            id = id,
            classType = generateRandomClassType()
        )
    }

    return flow {
        emit(PagingData.from(mockCarriages))
    }
}

private fun generateRandomClassType(): String {
    val randomNumber = (1..3).random()
    val randomLetter = listOf("А", "Б", "В", "Г", "Д").random()
    return "$randomNumber$randomLetter"
}