package com.chaikasoft.app.data.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.chaikasoft.app.data.room.entities.ConductorTripShift
import com.chaikasoft.app.data.room.entities.Station

data class ConductorTripShiftWithStations(
    @Embedded val shift: ConductorTripShift,
    @Relation(parentColumn = "from_code", entityColumn = "code")
    val from: Station,
    @Relation(parentColumn = "to_code", entityColumn = "code")
    val to: Station
)
