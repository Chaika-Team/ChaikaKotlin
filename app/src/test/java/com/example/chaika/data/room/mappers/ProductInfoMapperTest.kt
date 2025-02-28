package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.ProductInfo
import com.example.chaika.domain.models.ProductInfoDomain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Тесты для мапперов ProductInfoEntity.toDomain и ProductInfoDomain.toEntity.
 */
class ProductInfoMapperTest {
    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для маппера ProductInfoEntity.toDomain.
     *   - Классы эквивалентности: корректные данные для всех полей продукта.
     */
    @Test
    fun testProductInfoEntityToDomain() {
        // Arrange: создаём сущность продукта
        val productInfoEntity =
            ProductInfo(
                id = 1,
                name = "Entity Product",
                description = "Entity Description",
                image = "entity.png",
                price = 150.0,
            )
        // Act: преобразуем сущность в доменную модель
        val result: ProductInfoDomain = productInfoEntity.toDomain()
        // Assert: проверяем, что все поля корректно скопированы
        assertEquals(1, result.id)
        assertEquals("Entity Product", result.name)
        assertEquals("Entity Description", result.description)
        assertEquals("entity.png", result.image)
        assertEquals(150.0, result.price)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для маппера ProductInfoDomain.toEntity.
     *   - Граничные значения: проверка преобразования для продукта с граничным значением цены (0.0).
     */
    @Test
    fun testProductInfoDomainToEntity() {
        // Arrange: создаём доменную модель с граничным значением цены
        val productInfoDomain =
            ProductInfoDomain(
                id = 2,
                name = "Domain Product",
                description = "Domain Description",
                image = "domain.png",
                price = 0.0,
            )
        // Act: преобразуем доменную модель в сущность
        val result: ProductInfo = productInfoDomain.toEntity()
        // Assert: проверяем, что все поля корректно преобразованы
        assertEquals(2, result.id)
        assertEquals("Domain Product", result.name)
        assertEquals("Domain Description", result.description)
        assertEquals("domain.png", result.image)
        assertEquals(0.0, result.price)
    }
}
