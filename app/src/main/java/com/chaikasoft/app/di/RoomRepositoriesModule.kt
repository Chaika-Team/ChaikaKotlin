package com.chaikasoft.app.di

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
import com.chaikasoft.app.data.room.repo.RoomShiftReportRepository
import com.chaikasoft.app.data.room.repo.RoomShiftReportRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomStationRepository
import com.chaikasoft.app.data.room.repo.RoomStationRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomSyncMetaRepository
import com.chaikasoft.app.data.room.repo.RoomSyncMetaRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("TooManyFunctions")
interface RoomRepositoriesModule {
    @Binds
    @Singleton
    fun bindRoomCartOperationRepository(
        repository: RoomCartOperationRepository
    ): RoomCartOperationRepositoryInterface

    @Binds
    @Singleton
    fun bindRoomCartItemRepository(
        repository: RoomCartItemRepository
    ): RoomCartItemRepositoryInterface

    @Binds
    @Singleton
    fun bindRoomCartRepository(repository: RoomCartRepository): RoomCartRepositoryInterface

    @Binds
    @Singleton
    fun bindRoomConductorRepository(
        repository: RoomConductorRepository
    ): RoomConductorRepositoryInterface

    @Binds
    @Singleton
    fun bindRoomProductInfoRepository(
        repository: RoomProductInfoRepository
    ): RoomProductInfoRepositoryInterface

    @Binds
    @Singleton
    fun bindRoomConductorTripShiftRepository(
        repository: RoomConductorTripShiftRepository
    ): RoomConductorTripShiftRepositoryInterface

    @Binds
    @Singleton
    fun bindRoomShiftReportRepository(
        repository: RoomShiftReportRepository
    ): RoomShiftReportRepositoryInterface

    @Binds
    @Singleton
    fun bindRoomPackageItemRepository(
        repository: RoomPackageItemRepository
    ): RoomPackageItemRepositoryInterface

    @Binds
    @Singleton
    fun bindRoomStationRepository(repository: RoomStationRepository): RoomStationRepositoryInterface

    @Binds
    @Singleton
    fun bindRoomSyncMetaRepository(
        repository: RoomSyncMetaRepository
    ): RoomSyncMetaRepositoryInterface

    @Binds
    @Singleton
    fun bindRoomReportRepository(repository: RoomReportRepository): RoomReportRepositoryInterface
}
