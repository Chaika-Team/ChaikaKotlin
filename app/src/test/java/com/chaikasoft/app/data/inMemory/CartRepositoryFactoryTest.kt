package com.chaikasoft.app.data.inMemory

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

class CartRepositoryFactoryTest : FunSpec({

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - Фабрика должна возвращать новый экземпляр корзины на каждый create().
     *   - Это важно для изоляции состояния между независимыми сценариями.
     */
    test("create returns new in-memory repository instance each time") {
        val factory = CartRepositoryFactory()

        val first = factory.create()
        val second = factory.create()

        first.shouldBeInstanceOf<InMemoryCartRepository>()
        second.shouldBeInstanceOf<InMemoryCartRepository>()
        first shouldNotBe second
    }
})

