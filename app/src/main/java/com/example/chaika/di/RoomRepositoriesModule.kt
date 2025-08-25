package com.example.chaika.di

import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.dao.ConductorDao
import com.example.chaika.data.room.dao.ConductorTripShiftDao
import com.example.chaika.data.room.dao.FastReportViewDao
import com.example.chaika.data.room.dao.PackageItemViewDao
import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.repo.RoomCartItemRepository
import com.example.chaika.data.room.repo.RoomCartItemRepositoryInterface
import com.example.chaika.data.room.repo.RoomCartOperationRepository
import com.example.chaika.data.room.repo.RoomCartOperationRepositoryInterface
import com.example.chaika.data.room.repo.RoomCartRepository
import com.example.chaika.data.room.repo.RoomCartRepositoryInterface
import com.example.chaika.data.room.repo.RoomConductorRepository
import com.example.chaika.data.room.repo.RoomConductorRepositoryInterface
import com.example.chaika.data.room.repo.RoomConductorTripShiftRepository
import com.example.chaika.data.room.repo.RoomConductorTripShiftRepositoryInterface
import com.example.chaika.data.room.repo.RoomProductInfoRepository
import com.example.chaika.data.room.repo.RoomProductInfoRepositoryInterface
import com.example.chaika.data.room.repo.RoomPackageItemRepository
import com.example.chaika.data.room.repo.RoomPackageItemRepositoryInterface
import com.example.chaika.data.room.repo.RoomReportRepository
import com.example.chaika.data.room.repo.RoomReportRepositoryInterface
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
    fun provideRoomConductorTripShiftRepository(
        conductorTripShiftDao: ConductorTripShiftDao
    ): RoomConductorTripShiftRepositoryInterface =
        RoomConductorTripShiftRepository(conductorTripShiftDao)

    @Provides
    @Singleton
    fun provideRoomPackageItemRepository(
        packageItemViewDao: PackageItemViewDao,
        productInfoDao: ProductInfoDao
    ): RoomPackageItemRepositoryInterface =
        RoomPackageItemRepository(packageItemViewDao, productInfoDao)

    @Provides
    @Singleton
    fun provideRoomReportRepository(
        fastReportViewDao: FastReportViewDao
    ): RoomReportRepositoryInterface =
        RoomReportRepository(fastReportViewDao)
}
