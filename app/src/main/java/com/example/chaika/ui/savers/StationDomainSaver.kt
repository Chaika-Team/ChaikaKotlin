package com.example.chaika.ui.savers

import android.os.Bundle
import androidx.compose.runtime.saveable.Saver
import com.example.chaika.domain.models.trip.StationDomain

fun stationDomainSaver() = Saver<StationDomain?, Bundle>(
    save = { station ->
        Bundle().apply {
            if (station != null) {
                putInt("code", station.code)
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
            code = bundle.getInt("code"),
            name = bundle.getString("name") ?: "",
            city = bundle.getString("city") ?: ""
        )
    }
)