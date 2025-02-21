package com.example.chaika.data.dataSource.repo

import com.example.chaika.domain.models.ConductorDomain
import kotlin.Result

interface ApiServiceRepositoryInterface {
    suspend fun fetchUserInfo(accessToken: String): Result<ConductorDomain>
}
