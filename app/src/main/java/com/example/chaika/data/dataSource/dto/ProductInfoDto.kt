package com.example.chaika.data.dataSource.dto

import com.google.gson.annotations.SerializedName

data class ProductInfoDto(
    val name: String,
    val description: String,
    @SerializedName("imageurl")
    val image: String,
    val price: Double
)

data class ProductInfoListResponseDto(
    val products: List<ProductInfoDto>
)