package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.FastReportView
import com.example.chaika.domain.models.FastReportDomain

fun FastReportView.toDomain(): FastReportDomain {
    return FastReportDomain(
        productName = productName,
        productPrice = productPrice,
        addedQuantity = addedQuantity,
        replenishedQuantity = replenishedQuantity,
        soldCashQuantity = soldCashQuantity,
        soldCartQuantity = soldCartQuantity,
        revenue = revenue
    )
}
