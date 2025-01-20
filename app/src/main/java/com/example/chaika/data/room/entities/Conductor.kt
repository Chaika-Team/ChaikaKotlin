package com.example.chaika.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность проводника для хранения в базе данных.
 *
 * @param id Уникальный идентификатор проводника.
 * @param name Имя проводника.
 * @param employeeID Табельный номер проводника.
 * @param image Ссылка или путь к изображению проводника.
 * @param token Зашифрованный токен авторизации.
 */
@Entity(tableName = "conductors")
data class Conductor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "employee_id") val employeeID: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "token") val token: String
)
