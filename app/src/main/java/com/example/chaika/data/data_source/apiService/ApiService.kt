package com.example.chaika.data.data_source.apiService

import com.example.chaika.domain.models.ConductorDomain
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

/**
 * API-сервис для авторизации пользователей.
 */
interface ApiService {
    @GET("oidc/v2/userinfo")
    suspend fun getUserInfo(
        @Header("Authorization") token: String
    ): Response<ConductorDomain>
}

