package com.example.chaika.data.dataSource.repo

import com.example.chaika.data.dataSource.apiService.RemyApiService
import com.example.chaika.data.dataSource.mappers.toDomain
import com.example.chaika.domain.models.ConductorDomain
import javax.inject.Inject

class RemyApiServiceRepository @Inject constructor(
    private val remyApiService: RemyApiService,
) : RemyApiServiceRepositoryInterface {

    override suspend fun fetchUserInfo(accessToken: String): Result<ConductorDomain> {
        return try {
            val response = remyApiService.getUserInfo("Bearer $accessToken")
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    // Преобразуем DTO в доменную модель
                    Result.success(dto.toDomain())
                } ?: Result.failure(Exception("User info is empty"))
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
