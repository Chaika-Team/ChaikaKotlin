package com.chaikasoft.app.data.dataSource.repo

import com.chaikasoft.app.data.dataSource.apiService.IAMApiService
import com.chaikasoft.app.data.dataSource.mappers.toDomain
import com.chaikasoft.app.domain.models.ConductorDomain
import javax.inject.Inject

class IAMApiServiceRepository @Inject constructor(
    private val iamApiService: IAMApiService,
) : IAMApiServiceRepositoryInterface {

    override suspend fun fetchUserInfo(accessToken: String): Result<ConductorDomain> {
        return try {
            val dto = iamApiService.getUserInfo("Bearer $accessToken")
            Result.success(dto.toDomain())
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception("HTTP ${e.code()}: ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
