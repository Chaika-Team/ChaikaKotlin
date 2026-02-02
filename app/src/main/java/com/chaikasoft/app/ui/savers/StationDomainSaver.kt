package com.chaikasoft.app.ui.savers

import android.os.Bundle
import androidx.compose.runtime.saveable.Saver
import com.chaikasoft.app.domain.models.trip.StationDomain

fun stationDomainSaver() = Saver<StationDomain?, Bundle>(
    save = { station ->
        Bundle().apply {
            if (station != null) {
                putString("code", station.code)
                putString("name", station.name)
                putString("city", station.city)
            } else {
                putInt("code", -1)
            }
        }
    },
    restore = { bundle ->
        if (bundle.getInt("code") == -1) null
        else StationDomain(
            code = bundle.getString("code") ?: "",
            name = bundle.getString("name") ?: "",
            city = bundle.getString("city") ?: ""
        )
    }
)