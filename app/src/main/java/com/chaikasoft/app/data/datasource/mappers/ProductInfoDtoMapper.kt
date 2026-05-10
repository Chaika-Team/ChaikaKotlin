package com.chaikasoft.app.data.datasource.mappers

import com.chaikasoft.app.data.datasource.dto.ProductInfoDto
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.util.rubToKopecks

fun ProductInfoDto.toDomain(): ProductInfoDomain = ProductInfoDomain(
    id = this.id,
    name = this.name,
    description = this.description,
    image = this.image?.trim().orEmpty(),
    price = rubToKopecks(this.price)
)
