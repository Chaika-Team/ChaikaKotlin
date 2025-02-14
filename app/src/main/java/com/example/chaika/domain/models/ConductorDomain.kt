package com.example.chaika.domain.models

import com.google.gson.annotations.SerializedName

/**
 * Доменная модель проводника.
 *
 * @param id Уникальный идентификатор проводника.
 * @param name Имя проводника.
 * @param familyName Фамилия проводника.
 * @param givenName Отчество проводника.
 * @param employeeID Табельный номер проводника.
 * @param image Ссылка или путь к изображению проводника.
 */
data class ConductorDomain(
    val id: Int,
    val name: String,
    @SerializedName("family_name")
    val familyName: String,
    @SerializedName("given_name")
    val givenName: String,
    @SerializedName("nickname")
    val employeeID: String,
    val image: String
)