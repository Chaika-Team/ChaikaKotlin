package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.ProductInfo as ProductInfoEntity
import com.chaikasoft.app.domain.models.ProductInfoDomain

fun ProductInfoEntity.toDomain(): ProductInfoDomain = ProductInfoDomain(
    id = this.id,
    name = this.name,
    description = this.description,
    image = this.image,
    price = this.price
)

fun ProductInfoDomain.toEntity(): ProductInfoEntity = ProductInfoEntity(
    id = this.id,
    name = this.name,
    description = this.description,
    image = this.image,
    price = this.price
)
