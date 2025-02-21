package com.example.chaika.data.dataSource

import com.example.chaika.domain.models.ProductInfoDomain

class FakeProductInfoDataSource : ProductInfoDataSourceInterface {
    override suspend fun fetchProductInfoList(): List<ProductInfoDomain> {
        return listOf(
            ProductInfoDomain(
                id = 0,
                name = "Product 1",
                description = "Description for Product 1",
                image = "https://i.pinimg.com/736x/5b/d3/e7/5bd3e779f192cb04cf35b859e0d50cbc.jpg",
                price = 10.0,
            ),
            ProductInfoDomain(
                id = 0,
                name = "Product 2",
                description = "Description for Product 2",
                image = "https://i.pinimg.com/736x/5b/d3/e7/5bd3e779f192cb04cf35b859e0d50cbc.jpg",
                price = 15.0,
            ),
        )
    }
}
