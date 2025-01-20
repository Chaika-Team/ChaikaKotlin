package com.example.chaika.data.data_source.dto

/**
 * DTO для отправки данных авторизации на сервер.
 *
 * @param employeeID Табельный номер проводника.
 * @param password Пароль проводника.
 */
data class AuthRequestDto(
    val employeeID: String,
    val password: String
)

/**
 * DTO для получения данных авторизации с сервера.
 *
 * @param token Токен авторизации.
 * @param name Имя проводника.
 * @param image Ссылка на изображение проводника.
 */
data class AuthResponseDto(
    val token: String,
    val name: String,
    val image: String
)
