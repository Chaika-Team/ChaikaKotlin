package com.chaikasoft.app.data.datasource.repo

import android.util.Log
import com.chaikasoft.app.data.datasource.apiservice.ChaikaSoftApiService
import com.chaikasoft.app.data.datasource.common.remoteCall
import com.chaikasoft.app.data.datasource.mappers.toDomain
import com.chaikasoft.app.data.datasource.mappers.toDomainList
import com.chaikasoft.app.domain.common.ErrorReporter
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.models.TemplateDomain
import javax.inject.Inject

class ChaikaSoftApiServiceRepository @Inject constructor(
    private val apiService: ChaikaSoftApiService,
    private val errorReporter: ErrorReporter = ErrorReporter.NoOp
) : ChaikaSoftApiServiceRepositoryInterface {

    override suspend fun fetchProducts(
        limit: Int,
        offset: Int
    ): RemoteResult<List<ProductInfoDomain>> = remoteCall(errorReporter) {
        val dto = apiService.getProducts(limit, offset)
        dto.products.map { it.toDomain() }
    }

    override suspend fun fetchTemplates(
        query: String,
        limit: Int,
        offset: Int
    ): RemoteResult<List<TemplateDomain>> = remoteCall(errorReporter) {
        val body = apiService.getTemplates(query, limit, offset)
        Log.d("ChaikaSoftApiServiceRepo", "Templates count: ${body.templates.size}")
        body.templates.forEachIndexed { i, t ->
            Log.d(
                "ChaikaSoftApiServiceRepo",
                "Template[$i]: id=${t.id}, name=${t.templateName}, contentSize=${t.content.size}"
            )
        }
        body.templates.toDomainList()
    }

    override suspend fun fetchTemplateDetail(templateId: Int): RemoteResult<TemplateDomain> =
        remoteCall(errorReporter) {
            val body = apiService.getTemplateDetail(templateId)
            body.template.toDomain()
        }
}
