// ApiServiceRepository.kt
package com.example.chaika.data.data_source.apiService

import com.example.chaika.domain.models.ConductorDomain
import javax.inject.Inject

class ApiServiceRepository @Inject constructor(
    private val apiService: ApiService
) : ApiServiceRepositoryInterface {

    override suspend fun fetchUserInfo(accessToken: String): Result<ConductorDomain> {
        return try {
            val response = apiService.getUserInfo("Bearer $accessToken")
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("User info is empty"))
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
