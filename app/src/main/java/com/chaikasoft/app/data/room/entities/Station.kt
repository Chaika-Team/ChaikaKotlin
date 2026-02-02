// data/room/entities/StationEntity.kt
package com.chaikasoft.app.data.room.entities

import androidx.room.*

@Entity(
    tableName = "stations",
    indices = [Index(value = ["code"], unique = true), Index(value = ["name"]), Index(value = ["city"])]
)
data class Station(
    @PrimaryKey val code: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "city") val city: String
)
