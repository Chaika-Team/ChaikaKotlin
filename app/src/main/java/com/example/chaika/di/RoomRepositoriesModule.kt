package com.example.chaika.di

import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.dao.ConductorDao
import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.dao.TripReportDao
import com.example.chaika.data.room.repo.RoomCartItemRepository
import com.example.chaika.data.room.repo.RoomCartItemRepositoryInterface
import com.example.chaika.data.room.repo.RoomCartOperationRepository
import com.example.chaika.data.room.repo.RoomCartOperationRepositoryInterface
import com.example.chaika.data.room.repo.RoomCartRepository
import com.example.chaika.data.room.repo.RoomCartRepositoryInterface
import com.example.chaika.data.room.repo.RoomConductorRepository
import com.example.chaika.data.room.repo.RoomConductorRepositoryInterface
import com.example.chaika.data.room.repo.RoomProductInfoRepository
import com.example.chaika.data.room.repo.RoomProductInfoRepositoryInterface
import com.example.chaika.data.room.repo.RoomTripReportRepository
import com.example.chaika.data.room.repo.RoomTripReportRepositoryInterface
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomRepositoriesModule {
    @Provides
    @Singleton
    fun provideRoomCartOperationRepository(cartOperationDao: CartOperationDao): RoomCartOperationRepositoryInterface =
        RoomCartOperationRepository(cartOperationDao)

    @Provides
    @Singleton
    fun provideRoomCartItemRepository(
        cartItemDao: CartItemDao,
        productInfoDao: ProductInfoDao,
    ): RoomCartItemRepositoryInterface = RoomCartItemRepository(cartItemDao, productInfoDao)

    @Provides
    @Singleton
    fun provideRoomCartRepository(
        cartItemDao: CartItemDao,
        cartOperationDao: CartOperationDao,
    ): RoomCartRepositoryInterface = RoomCartRepository(cartItemDao, cartOperationDao)

    @Provides
    @Singleton
    fun provideRoomConductorRepository(conductorDao: ConductorDao): RoomConductorRepositoryInterface = RoomConductorRepository(conductorDao)

    @Provides
    @Singleton
    fun provideRoomProductInfoRepository(productInfoDao: ProductInfoDao): RoomProductInfoRepositoryInterface =
        RoomProductInfoRepository(productInfoDao)

    @Provides
    @Singleton
    fun provideRoomTripReportRepositoryInterface(
        tripReportDao: TripReportDao,
        moshi: Moshi
    ): RoomTripReportRepositoryInterface {
        return RoomTripReportRepository(tripReportDao, moshi)
    }
}

