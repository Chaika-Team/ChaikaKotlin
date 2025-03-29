package com.example.chaika.data.dataSource.repo

import com.example.chaika.domain.models.ConductorDomain
import kotlin.Result

interface IAMApiServiceRepositoryInterface {
    suspend fun fetchUserInfo(accessToken: String): Result<ConductorDomain>
}
