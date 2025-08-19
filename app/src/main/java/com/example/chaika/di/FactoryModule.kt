package com.example.chaika.di

import com.example.chaika.data.inMemory.CartRepositoryFactory
import com.example.chaika.data.inMemory.CartRepositoryFactoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FactoryModule {

    @Binds
    @Singleton
    abstract fun bindCartRepositoryFactory(
        impl: CartRepositoryFactory
    ): CartRepositoryFactoryInterface
}
