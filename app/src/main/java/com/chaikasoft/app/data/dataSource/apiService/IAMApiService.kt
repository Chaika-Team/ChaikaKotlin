package com.chaikasoft.app.data.datasource.apiservice

import com.chaikasoft.app.data.datasource.dto.ConductorDto
import retrofit2.http.GET
import retrofit2.http.Header

/**
 * API-сервис.
 */
interface IAMApiService {
    @GET("/oidc/v1/userinfo")
    suspend fun getUserInfo(@Header("Authorization") token: String): ConductorDto
}
