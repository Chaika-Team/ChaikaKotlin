package com.example.chaika.data.dataSource.repo

import com.example.chaika.data.dataSource.apiService.ChaikaSoftApiService
import com.example.chaika.data.dataSource.mappers.toDomain
import com.example.chaika.data.dataSource.mappers.toDomainList
import com.example.chaika.domain.models.ProductInfoDomain
import com.example.chaika.domain.models.TemplateDomain
import javax.inject.Inject

class ChaikaSoftApiServiceRepository @Inject constructor(
    private val apiService: ChaikaSoftApiService
) : ChaikaSoftApiServiceRepositoryInterface {

    override suspend fun fetchProducts(limit: Int, offset: Int): List<ProductInfoDomain> {
        val response = apiService.getProducts(limit, offset)
        if (response.isSuccessful) {
            return response.body()?.products?.map { it.toDomain() } ?: emptyList()
        } else {
            throw Exception("Error ${response.code()} - ${response.message()}")
        }
    }

    // Новый метод для шаблонов
    override suspend fun fetchTemplates(limit: Int, offset: Int): List<TemplateDomain> {
        val response = apiService.getTemplates(limit = limit, offset = offset)
        if (response.isSuccessful) {
            return response.body()?.toDomainList() ?: emptyList()
        } else {
            throw Exception("Error ${response.code()} - ${response.message()}")
        }
    }
}
