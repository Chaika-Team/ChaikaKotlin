package com.chaikasoft.app.di

import com.chaikasoft.app.data.room.dao.CartItemDao
import com.chaikasoft.app.data.room.dao.CartOperationDao
import com.chaikasoft.app.data.room.dao.ConductorDao
import com.chaikasoft.app.data.room.dao.ConductorTripShiftDao
import com.chaikasoft.app.data.room.dao.FastReportViewDao
import com.chaikasoft.app.data.room.dao.PackageItemViewDao
import com.chaikasoft.app.data.room.dao.ProductInfoDao
import com.chaikasoft.app.data.room.dao.StationDao
import com.chaikasoft.app.data.room.dao.SyncMetaDao
import com.chaikasoft.app.data.room.repo.RoomCartItemRepository
import com.chaikasoft.app.data.room.repo.RoomCartItemRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomCartOperationRepository
import com.chaikasoft.app.data.room.repo.RoomCartOperationRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomCartRepository
import com.chaikasoft.app.data.room.repo.RoomCartRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomConductorRepository
import com.chaikasoft.app.data.room.repo.RoomConductorRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomConductorTripShiftRepository
import com.chaikasoft.app.data.room.repo.RoomConductorTripShiftRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomPackageItemRepository
import com.chaikasoft.app.data.room.repo.RoomPackageItemRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomProductInfoRepository
import com.chaikasoft.app.data.room.repo.RoomProductInfoRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomReportRepository
import com.chaikasoft.app.data.room.repo.RoomReportRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomStationRepository
import com.chaikasoft.app.data.room.repo.RoomStationRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomSyncMetaRepository
import com.chaikasoft.app.data.room.repo.RoomSyncMetaRepositoryInterface
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
    fun provideRoomCartOperationRepository(
        cartOperationDao: CartOperationDao
    ): RoomCartOperationRepositoryInterface = RoomCartOperationRepository(cartOperationDao)

    @Provides
    @Singleton
    fun provideRoomCartItemRepository(
        cartItemDao: CartItemDao,
        productInfoDao: ProductInfoDao
    ): RoomCartItemRepositoryInterface = RoomCartItemRepository(cartItemDao, productInfoDao)

    @Provides
    @Singleton
    fun provideRoomCartRepository(
        cartItemDao: CartItemDao,
        cartOperationDao: CartOperationDao
    ): RoomCartRepositoryInterface = RoomCartRepository(cartItemDao, cartOperationDao)

    @Provides
    @Singleton
    fun provideRoomConductorRepository(
        conductorDao: ConductorDao
    ): RoomConductorRepositoryInterface = RoomConductorRepository(conductorDao)

    @Provides
    @Singleton
    fun provideRoomProductInfoRepository(
        productInfoDao: ProductInfoDao
    ): RoomProductInfoRepositoryInterface = RoomProductInfoRepository(productInfoDao)

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
    fun provideRoomStationRepository(dao: StationDao): RoomStationRepositoryInterface =
        RoomStationRepository(dao)

    @Provides
    @Singleton
    fun provideRoomSyncMetaRepository(dao: SyncMetaDao): RoomSyncMetaRepositoryInterface =
        RoomSyncMetaRepository(dao)

    @Provides
    @Singleton
    fun provideRoomReportRepository(
        fastReportViewDao: FastReportViewDao
    ): RoomReportRepositoryInterface = RoomReportRepository(fastReportViewDao)
}
