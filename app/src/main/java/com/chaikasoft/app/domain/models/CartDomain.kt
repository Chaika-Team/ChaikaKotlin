package com.chaikasoft.app.domain.models

/**
 * Доменная модель корзины.
 *
 * @param items элементы корзины, представлены ДМ CartItemDomain.
 */
data class CartDomain(val items: List<CartItemDomain>)
