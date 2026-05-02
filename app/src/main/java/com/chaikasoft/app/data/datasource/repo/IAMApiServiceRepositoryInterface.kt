package com.chaikasoft.app.data.datasource.repo

import com.chaikasoft.app.domain.models.ConductorDomain

interface IAMApiServiceRepositoryInterface {
    suspend fun fetchUserInfo(accessToken: String): Result<ConductorDomain>
}
