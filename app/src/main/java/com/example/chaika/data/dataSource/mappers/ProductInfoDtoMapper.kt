package com.example.chaika.data.dataSource.mappers


import com.example.chaika.data.dataSource.dto.ProductInfoDto
import com.example.chaika.domain.models.ProductInfoDomain

fun ProductInfoDto.toDomain(): ProductInfoDomain =
    ProductInfoDomain(
        id = 0,
        name = name,
        description = description,
        image = image,
        price = price
    )
