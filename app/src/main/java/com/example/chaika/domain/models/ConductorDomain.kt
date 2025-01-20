package com.example.chaika.domain.models

/**
 * Доменная модель проводника.
 *
 * @param id Уникальный идентификатор проводника.
 * @param name Имя проводника.
 * @param employeeID Табельный номер проводника.
 * @param image Ссылка или путь к изображению проводника.
 * @param token Зашифрованный токен авторизации.
 */
data class ConductorDomain(
    val id: Int,
    val name: String,
    val employeeID: String,
    val image: String,
    val token: String
)
