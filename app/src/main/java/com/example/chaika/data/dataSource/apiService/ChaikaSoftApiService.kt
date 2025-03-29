package com.example.chaika.data.dataSource.apiService

import com.example.chaika.data.dataSource.dto.ProductInfoListResponseDto
import com.example.chaika.data.dataSource.dto.TemplateDetailResponseDto
import com.example.chaika.data.dataSource.dto.TemplateListResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ChaikaSoftApiService {
    @GET("api/v1/products")
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
}
