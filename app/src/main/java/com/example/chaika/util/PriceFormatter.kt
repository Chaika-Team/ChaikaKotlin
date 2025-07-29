package com.example.chaika.util

/**
 * Утилитарная функция для форматирования цены в рублях
 * 
 * @param price Цена в рублях
 * @param quantity Количество товара (по умолчанию 1)
 * @return Отформатированная строка с ценой
 */
fun formatPrice(price: Double, quantity: Int = 1): String {
    return "%.2f ₽".format(price * quantity)
}

/**
 * Утилитарная функция для форматирования цены без количества
 * 
 * @param price Цена в рублях
 * @return Отформатированная строка с ценой
 */
fun formatPriceOnly(price: Double): String {
    return "%.2f ₽".format(price)
} 