package com.example.chaika.data.dataSource.repo

import android.util.Log
import com.example.chaika.data.dataSource.apiService.ChaikaSoftApiService
import com.example.chaika.data.dataSource.mappers.toDomain
import com.example.chaika.data.dataSource.mappers.toDomainList
import com.example.chaika.domain.models.ProductInfoDomain
import com.example.chaika.domain.models.TemplateDomain
import javax.inject.Inject

class ChaikaSoftApiServiceRepository @Inject constructor(
    private val apiService: ChaikaSoftApiService,
) : ChaikaSoftApiServiceRepositoryInterface {

    override suspend fun fetchProducts(limit: Int, offset: Int): List<ProductInfoDomain> {
        val response = apiService.getProducts(limit, offset)
        if (response.isSuccessful) {
            return response.body()?.products?.map { it.toDomain() } ?: emptyList()
        } else {
            throw Exception("Error ${response.code()} - ${response.message()}")
        }
    }

    override suspend fun fetchTemplates(
        query: String,
        limit: Int,
        offset: Int
    ): List<TemplateDomain> {
        val response = apiService.getTemplates(query, limit, offset)
        if (response.isSuccessful) {
            val body = response.body()
            Log.d("ChaikaSoftApiServiceRepo", "Templates response body: $body")
            return body?.templates?.toDomainList() ?: emptyList()
        } else {
            Log.e(
                "ChaikaSoftApiServiceRepo",
                "Error fetching templates: ${response.code()} - ${response.message()}"
            )
            throw Exception("Error fetching templates: ${response.code()} - ${response.message()}")
        }
    }


    override suspend fun fetchTemplateDetail(templateId: Int): TemplateDomain {
        val response = apiService.getTemplateDetail(templateId)
        if (response.isSuccessful) {
            return response.body()?.template?.toDomain()
                ?: throw Exception("Template detail is empty")
        } else {
            throw Exception("Error fetching template detail: ${response.code()} - ${response.message()}")
        }
    }
}
