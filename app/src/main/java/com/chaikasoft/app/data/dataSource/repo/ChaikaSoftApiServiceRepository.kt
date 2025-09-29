package com.chaikasoft.app.data.dataSource.repo

import android.util.Log
import com.chaikasoft.app.data.dataSource.apiService.ChaikaSoftApiService
import com.chaikasoft.app.data.dataSource.mappers.toDomain
import com.chaikasoft.app.data.dataSource.mappers.toDomainList
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.models.TemplateDomain
import javax.inject.Inject

class ChaikaSoftApiServiceRepository @Inject constructor(
    private val apiService: ChaikaSoftApiService,
) : ChaikaSoftApiServiceRepositoryInterface {

    override suspend fun fetchProducts(limit: Int, offset: Int): List<ProductInfoDomain> {
        val dto = apiService.getProducts(limit, offset)
        return dto.products.map { it.toDomain() }
    }

    override suspend fun fetchTemplates(
        query: String,
        limit: Int,
        offset: Int
    ): List<TemplateDomain> {
        val body = apiService.getTemplates(query, limit, offset)
        Log.d("ChaikaSoftApiServiceRepo", "Templates count: ${body.templates.size}")
        body.templates.forEachIndexed { i, t ->
            Log.d(
                "ChaikaSoftApiServiceRepo",
                "Template[$i]: id=${t.id}, name=${t.templateName}, contentSize=${t.content.size}"
            )
        }
        return body.templates.toDomainList()
    }


    override suspend fun fetchTemplateDetail(templateId: Int): TemplateDomain {
        val body = apiService.getTemplateDetail(templateId)
        return body.template.toDomain()
    }
}
