package com.example.chaika.data.data_source.apiService

import com.example.chaika.domain.models.ConductorDomain
import kotlin.Result

interface ApiServiceRepositoryInterface {
    suspend fun fetchUserInfo(accessToken: String): Result<ConductorDomain>
}