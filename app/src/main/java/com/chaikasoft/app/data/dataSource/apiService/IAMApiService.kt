package com.chaikasoft.app.data.dataSource.apiService

import com.chaikasoft.app.data.dataSource.dto.ConductorDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

/**
 * API-сервис.
 */
interface IAMApiService {
    @GET("/oidc/v1/userinfo")
    suspend fun getUserInfo(
        @Header("Authorization") token: String,
    ): Response<ConductorDto>
}
