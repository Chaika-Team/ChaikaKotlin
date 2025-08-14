package com.example.chaika.data.inMemory

/** Фабрика, создающая новую корзину для каждого запроса */
interface CartRepositoryFactoryInterface {
    fun create(): InMemoryCartRepositoryInterface
}