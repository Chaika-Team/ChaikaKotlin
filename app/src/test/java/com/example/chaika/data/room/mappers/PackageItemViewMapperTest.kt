package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.PackageItemView
import com.example.chaika.domain.models.PackageItemDomain
import com.example.chaika.domain.models.ProductInfoDomain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Тесты для маппера PackageItemView.toDomain.
 */
class PackageItemViewMapperTest {
    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для маппера PackageItemView.toDomain.
     *   - Классы эквивалентности: корректные данные для currentQuantity и productInfoDomain.
     */
    @Test
    fun testPackageItemViewToDomain() {
        // Arrange: создаём представление и доменную модель продукта
        val packageItemView =
            PackageItemView(
                productId = 10,
                currentQuantity = 20,
            )
        val productInfoDomain =
            ProductInfoDomain(
                id = 10,
                name = "Test Product",
                description = "Test Description",
                image = "test.png",
                price = 99.99,
            )
        // Act: преобразуем PackageItemView в доменную модель
        val result: PackageItemDomain = packageItemView.toDomain(productInfoDomain)
        // Assert: проверяем, что полученные данные совпадают с ожидаемыми
        assertEquals(productInfoDomain, result.productInfoDomain)
        assertEquals(20, result.currentQuantity)
    }
}
