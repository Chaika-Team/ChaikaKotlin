package com.chaikasoft.app.data.datasource.apiservice

import com.chaikasoft.app.data.datasource.dto.ProductInfoListResponseDto
import com.chaikasoft.app.data.datasource.dto.StationsResponseDto
import com.chaikasoft.app.data.datasource.dto.TemplateDetailResponseDto
import com.chaikasoft.app.data.datasource.dto.TemplateListResponseDto
import com.chaikasoft.app.data.datasource.dto.TripsResponseDto
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChaikaSoftApiService {
    @GET("/api/v1/product")
    suspend fun getProducts(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): ProductInfoListResponseDto

    // Метод для получения списка шаблонов (без content)
    @GET("/api/v1/product/template/search")
    suspend fun getTemplates(
        @Query("query") query: String = "",
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): TemplateListResponseDto

    // Метод для получения деталей конкретного шаблона (с content)
    @GET("/api/v1/product/template/{id}")
    suspend fun getTemplateDetail(@Path("id") templateId: Int): TemplateDetailResponseDto

// ==== эндпоинты для маршрутов ====

    /**
     * Поиск подсказок по станциям.
     */
    @GET("/api/v1/route/station")
    suspend fun findStations(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10
    ): StationsResponseDto

    /**
     * Поиск поездок с фильтрами.
     */
    @GET("/api/v1/route/yandex/search")
    suspend fun findTrips(
        @Query("from") fromCode: String? = null,
        @Query("to") toCode: String? = null,
        @Query("date") date: String
    ): TripsResponseDto

    // ==== эндпоинты ChaikaReports ====

    @POST("/api/v1/report/sale")
    suspend fun sendShiftReport(@Body body: RequestBody)
}
