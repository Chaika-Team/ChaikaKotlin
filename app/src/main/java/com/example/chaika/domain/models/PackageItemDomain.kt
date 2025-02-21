package com.example.chaika.domain.models

/**
 * Доменная модель элемента пакета (списка товаров у проводника).
 *
 * @param productInfoDomain Информация о продукте, представленная ProductInfoDomain.
 * @param currentQuantity Текущее количество товара у проводника.
 */
data class PackageItemDomain(
    val productInfoDomain: ProductInfoDomain,
    val currentQuantity: Int,
)
