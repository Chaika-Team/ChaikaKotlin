package com.example.chaika.data.dataSource.apiService

import com.example.chaika.data.dataSource.dto.CarsResponseDto
import com.example.chaika.data.dataSource.dto.ProductInfoListResponseDto
import com.example.chaika.data.dataSource.dto.StationsResponseDto
import com.example.chaika.data.dataSource.dto.TemplateDetailResponseDto
import com.example.chaika.data.dataSource.dto.TemplateListResponseDto
import com.example.chaika.data.dataSource.dto.TripDetailResponseDto
import com.example.chaika.data.dataSource.dto.TripsResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ChaikaSoftApiService {
    @GET("api/v1/product")
    suspend fun getProducts(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): Response<ProductInfoListResponseDto>

    // Метод для получения списка шаблонов (без content)
    @GET("api/v1/templates/search")
    suspend fun getTemplates(
        @Query("query") query: String = "",
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): Response<TemplateListResponseDto>


    // Метод для получения деталей конкретного шаблона (с content)
    @GET("api/v1/templates/{id}")
    suspend fun getTemplateDetail(
        @Path("id") templateId: Int
    ): Response<TemplateDetailResponseDto>

// ==== Новые эндпоинты для маршрутов ====

    /**
     * Поиск подсказок по станциям.
     */
    @GET("api/v1/route/station")
    suspend fun findStations(
        @Query("query") query: String,
        @Query("limit") limit: Int = 10
    ): Response<StationsResponseDto>

    /**
     * Поиск поездок с фильтрами.
     */
    @GET("api/v1/route/trip")
    suspend fun findTrips(
        @Query("date") date: String,
        @Query("number") trainNumber: String? = null,
        @Query("from_code") fromCode: Int? = null,
        @Query("to_code") toCode: Int? = null
    ): Response<TripsResponseDto>

    /**
     * Получить детали поездки по UUID.
     */
    @GET("api/v1/route/trip/{trip_uuid}")
    suspend fun getTripByUuid(
        @Path("trip_uuid") uuid: String
    ): Response<TripDetailResponseDto>

    /**
     * Получить список вагонов по UUID поездки.
     */
    @GET("api/v1/route/trip/{trip_uuid}/car")
    suspend fun getCarsForTrip(
        @Path("trip_uuid") uuid: String
    ): Response<CarsResponseDto>
}
