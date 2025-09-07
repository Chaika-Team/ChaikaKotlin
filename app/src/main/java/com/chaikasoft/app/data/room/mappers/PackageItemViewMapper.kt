package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.PackageItemView
import com.chaikasoft.app.domain.models.PackageItemDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain

// Маппер для преобразования из PackageItemView в PackageItem
fun PackageItemView.toDomain(productInfoDomain: ProductInfoDomain): PackageItemDomain =
    PackageItemDomain(
        productInfoDomain = productInfoDomain,
        currentQuantity = this.currentQuantity,
    )
