package com.example.chaika.data.inMemory

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryFactory @Inject constructor() : CartRepositoryFactoryInterface {
    override fun create(): InMemoryCartRepositoryInterface = InMemoryCartRepository()
}