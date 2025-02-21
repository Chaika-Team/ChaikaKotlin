package com.example.chaika.domain.models
/**
 * Доменная модель элемента корзины.
 *
 * @param product Продукт, представлен ДМ PackageItemDomain.
 * @param quantity Количество единиц продукта.
 * */
data class CartItemDomain(
    val product: ProductInfoDomain, // Теперь используем ProductInfo вместо productId
    var quantity: Int,
)
