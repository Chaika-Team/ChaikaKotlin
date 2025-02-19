package com.example.chaika.data.data_source.repo

import com.example.chaika.domain.models.ConductorDomain
import kotlin.Result

interface ApiServiceRepositoryInterface {
    suspend fun fetchUserInfo(accessToken: String): Result<ConductorDomain>
}