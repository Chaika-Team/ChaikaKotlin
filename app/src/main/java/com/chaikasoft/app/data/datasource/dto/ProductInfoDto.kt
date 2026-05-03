package com.chaikasoft.app.data.datasource.dto

import com.google.gson.annotations.SerializedName

data class ProductInfoDto(
    val id: Int,
    val name: String,
    val description: String,
    @SerializedName("imageurl")
    val image: String? = null,
    val price: Double
)

data class ProductInfoListResponseDto(val products: List<ProductInfoDto>)
