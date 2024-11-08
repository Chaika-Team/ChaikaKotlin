package com.example.chaika.domain.models

data class PackageItemDomain(
    val productInfoDomain: ProductInfoDomain,  // Информация о продукте
    val currentQuantity: Int       // Текущее количество у проводника
)
