package com.example.chaika.domain.models

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
    val id: Int? = null,
    val name: String,
    val familyName: String,
    val givenName: String,
    val employeeID: String,
    val image: String,
)
