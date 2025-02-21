package com.example.chaika.domain.models

/**
 * Доменная модель корзины.
 *
 * @param items элементы корзины, представлены ДМ CartItemDomain.
 */
data class CartDomain(
    val items: MutableList<CartItemDomain>,
)
