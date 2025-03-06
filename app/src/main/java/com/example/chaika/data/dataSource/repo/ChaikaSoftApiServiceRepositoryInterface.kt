package com.example.chaika.data.dataSource.repo

import com.example.chaika.domain.models.ProductInfoDomain

interface ChaikaSoftApiServiceRepositoryInterface {
    suspend fun fetchProducts(limit: Int, offset: Int): List<ProductInfoDomain>
}