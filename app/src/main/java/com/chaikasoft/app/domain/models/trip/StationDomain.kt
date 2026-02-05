package com.chaikasoft.app.domain.models.trip

/**
 * Доменная модель станции.
 *
 * @param code Код станции.
 * @param name Название станции.
 * @param city Город или регион станции.
 */
data class StationDomain(
    val code: String,
    val name: String,
    val city: String
)
