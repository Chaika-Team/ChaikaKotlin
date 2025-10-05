package com.chaikasoft.app.util

/**
 * Утилитарная функция для форматирования цены в рублях
 * 
 * @param priceKopecks Цена в копейках
 * @param quantity Количество товара (по умолчанию 1)
 * @return Отформатированная строка с ценой
 */

fun formatPrice(priceKopecks: Int, quantity: Int = 1): String {
    val total = priceKopecks.toLong() * quantity.toLong()
    val rub = total / 100
    val kop = total % 100
    return "%d.%02d ₽".format(rub, kop)
}

/**
 * Утилитарная функция для форматирования цены без количества
 * 
 * @param priceKopecks Цена в копейках
 * @return Отформатированная строка с ценой
 */
fun formatPriceOnly(priceKopecks: Int): String {
    val rub = priceKopecks / 100
    val kop = priceKopecks % 100
    return "%d.%02d ₽".format(rub, kop)
}