package com.chaikasoft.app.data.dataSource.repo

import com.chaikasoft.app.domain.models.ConductorDomain
import kotlin.Result

interface IAMApiServiceRepositoryInterface {
    suspend fun fetchUserInfo(accessToken: String): Result<ConductorDomain>
}
