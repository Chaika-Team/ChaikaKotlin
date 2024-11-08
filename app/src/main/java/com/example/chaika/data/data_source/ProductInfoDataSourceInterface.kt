package com.example.chaika.data.data_source

import com.example.chaika.domain.models.ProductInfoDomain

// ProductInfoDataSourceInterface.kt
interface ProductInfoDataSourceInterface {
    suspend fun fetchProductInfoList(): List<ProductInfoDomain>
}
