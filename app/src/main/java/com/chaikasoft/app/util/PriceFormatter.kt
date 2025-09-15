package com.chaikasoft.app.util

/**
 * Утилитарная функция для форматирования цены в рублях
 * 
 * @param price Цена в рублях
 * @param quantity Количество товара (по умолчанию 1)
 * @return Отформатированная строка с ценой
 */

    //TODO: Согласовать с фронтендером
fun formatPrice(priceKopecks: Int, quantity: Int = 1): String {
    val total = priceKopecks.toLong() * quantity.toLong()
    val rub = total / 100
    val kop = total % 100
    return "%d.%02d ₽".format(rub, kop)
}

/**
 * Утилитарная функция для форматирования цены без количества
 * 
 * @param price Цена в рублях
 * @return Отформатированная строка с ценой
 */
    //TODO: Согласовать с фронтендером
fun formatPriceOnly(priceKopecks: Int): String {
    val rub = priceKopecks / 100
    val kop = priceKopecks % 100
    return "%d.%02d ₽".format(rub, kop)
}