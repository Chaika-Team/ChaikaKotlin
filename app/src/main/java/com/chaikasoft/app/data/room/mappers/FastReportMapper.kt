package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.FastReportView
import com.chaikasoft.app.domain.models.FastReportDomain

fun FastReportView.toDomain(): FastReportDomain =
    FastReportDomain(
        productName = productName,
        productPrice = productPrice,
        addedQuantity = addedQuantity,
        replenishedQuantity = replenishedQuantity,
        soldCashQuantity = soldCashQuantity,
        soldCartQuantity = soldCartQuantity,
        revenue = revenue,
    )
