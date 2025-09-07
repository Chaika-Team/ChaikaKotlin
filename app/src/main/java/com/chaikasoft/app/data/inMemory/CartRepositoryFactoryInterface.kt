package com.chaikasoft.app.data.inMemory

/** Фабрика, создающая новую корзину для каждого запроса */
interface CartRepositoryFactoryInterface {
    fun create(): InMemoryCartRepositoryInterface
}