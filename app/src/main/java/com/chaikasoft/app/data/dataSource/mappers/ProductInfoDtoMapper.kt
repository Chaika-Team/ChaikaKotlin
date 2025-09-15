package com.chaikasoft.app.data.dataSource.mappers


import com.chaikasoft.app.data.dataSource.dto.ProductInfoDto
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.util.rubToKopecks

fun ProductInfoDto.toDomain(): ProductInfoDomain =
    ProductInfoDomain(
        id = this.id,
        name = this.name,
        description = this.description,
        image = "https://i.pinimg.com/736x/5b/d3/e7/5bd3e779f192cb04cf35b859e0d50cbc.jpg",
        price = rubToKopecks(this.price)
    )
