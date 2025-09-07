package com.chaikasoft.app.data.inMemory

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryFactory @Inject constructor() : CartRepositoryFactoryInterface {
    override fun create(): InMemoryCartRepositoryInterface = InMemoryCartRepository()
}