package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.OperationInfoView
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.domain.models.OperationSummaryDomain

fun OperationInfoView.toDomain(): OperationSummaryDomain =
    OperationSummaryDomain(
        id = this.operationId,
        type = this.operationType.toOperationType(), // ext из CartOperationMapper.kt
        timeIso = this.operationTime,
        conductor = ConductorDomain(
            id = this.conductorId,
            name = this.conductorName,
            familyName = this.conductorFamilyName,
            givenName = this.conductorGivenName,
            employeeID = "",
            image = ""
        ),
        productLineQuantity = this.productLineQuantity,
        totalPrice = this.totalPrice
    )
