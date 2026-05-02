package com.chaikasoft.app.data.inmemory

/** Фабрика, создающая новую корзину для каждого запроса */
interface CartRepositoryFactoryInterface {
    fun create(): InMemoryCartRepositoryInterface
}
