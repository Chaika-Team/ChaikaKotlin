package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.PackageItemView
import com.example.chaika.domain.models.PackageItemDomain
import com.example.chaika.domain.models.ProductInfoDomain

// Маппер для преобразования из PackageItemView в PackageItem
fun PackageItemView.toDomain(productInfoDomain: ProductInfoDomain): PackageItemDomain {
    return PackageItemDomain(
        productInfoDomain = productInfoDomain,
        currentQuantity = this.currentQuantity,
    )
}
