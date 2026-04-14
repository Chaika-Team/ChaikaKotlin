package com.chaikasoft.app.di

import com.chaikasoft.app.data.inmemory.CartRepositoryFactory
import com.chaikasoft.app.data.inmemory.CartRepositoryFactoryInterface
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
