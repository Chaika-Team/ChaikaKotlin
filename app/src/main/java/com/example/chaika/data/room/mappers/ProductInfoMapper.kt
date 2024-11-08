package com.example.chaika.data.room.mappers

import com.example.chaika.domain.models.ProductInfoDomain
import com.example.chaika.data.room.entities.ProductInfo as ProductInfoEntity

fun ProductInfoEntity.toDomain(): ProductInfoDomain {
    return ProductInfoDomain(
        id = this.id,
        name = this.name,
        description = this.description,
        image = this.image,
        price = this.price
    )
}

fun ProductInfoDomain.toEntity(): ProductInfoEntity {
    return ProductInfoEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        image = this.image,
        price = this.price
    )
}
