package com.example.chaika.data.dataSource.repo

import com.example.chaika.domain.models.ProductInfoDomain
import com.example.chaika.domain.models.TemplateDomain

interface ChaikaSoftApiServiceRepositoryInterface {
    suspend fun fetchProducts(limit: Int, offset: Int): List<ProductInfoDomain>
    suspend fun fetchTemplates(limit: Int = 100, offset: Int = 0): List<TemplateDomain>

}