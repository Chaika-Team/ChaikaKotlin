package com.chaikasoft.app.data.datasource.repo

import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.models.TemplateDomain

interface ChaikaSoftApiServiceRepositoryInterface {
    suspend fun fetchProducts(limit: Int, offset: Int): RemoteResult<List<ProductInfoDomain>>
    suspend fun fetchTemplates(
        query: String = "",
        limit: Int = 100,
        offset: Int = 0
    ): RemoteResult<List<TemplateDomain>>

    suspend fun fetchTemplateDetail(templateId: Int): RemoteResult<TemplateDomain>
}
