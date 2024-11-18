package com.example.chaika.di

import android.content.Context
import androidx.room.Room
import com.example.chaika.data.data_source.FakeProductInfoDataSource
import com.example.chaika.data.data_source.ProductInfoDataSourceInterface
import com.example.chaika.data.inMemory.InMemoryCartRepository
import com.example.chaika.data.inMemory.InMemoryCartRepositoryInterface
import com.example.chaika.data.local.LocalImageRepository
import com.example.chaika.data.room.AppDatabase
import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.dao.ConductorDao
import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.dao.FastReportViewDao
import com.example.chaika.data.room.repo.*
import com.example.chaika.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    // Тестовое Предзаполнение
    @Provides
    @Singleton
    fun providePrepopulateConductorsUseCase(roomConductorRepositoryInterface: RoomConductorRepositoryInterface): PrepopulateConductorsUseCase {
        return PrepopulateConductorsUseCase(roomConductorRepositoryInterface)
    }

    @Provides
    @Singleton
    fun providePrepopulateProductsUseCase(productInfoRepositoryInterface: RoomProductInfoRepositoryInterface): PrepopulateProductsUseCase {
        return PrepopulateProductsUseCase(productInfoRepositoryInterface)
    }

    // Загрузка товаров с сервера
    @Provides
    @Singleton
    fun provideProductInfoDataSource(): ProductInfoDataSourceInterface {
        return FakeProductInfoDataSource() // Используем фейковую реализацию
    }

    // DAOs
    @Provides
    fun provideCartItemDao(appDatabase: AppDatabase): CartItemDao {
        return appDatabase.cartItemDao()
    }

    @Provides
    fun provideCartOperationDao(appDatabase: AppDatabase): CartOperationDao {
        return appDatabase.cartOperationDao()
    }

    @Provides
    fun provideConductorDao(appDatabase: AppDatabase): ConductorDao {
        return appDatabase.conductorDao()
    }

    @Provides
    fun provideProductInfoDao(appDatabase: AppDatabase): ProductInfoDao {
        return appDatabase.productInfoDao()
    }

    @Provides
    fun provideReportViewDao(appDatabase: AppDatabase): FastReportViewDao {
        return appDatabase.fastReportViewDao()
    }

    // Repositories

    @Provides
    @Singleton
    fun provideInMemoryCartRepository(): InMemoryCartRepositoryInterface {
        return InMemoryCartRepository()
    }


    // Image Repository
    @Provides
    @Singleton
    fun provideInMemoryImageRepository(
        @ApplicationContext context: Context
    ): LocalImageRepository {
        return LocalImageRepository(context)
    }

    @Provides
    @Singleton
    fun provideRoomCartRepository(
        cartItemDao: CartItemDao,
        cartOperationDao: CartOperationDao
    ): RoomCartRepositoryInterface {
        return RoomCartRepository(cartItemDao, cartOperationDao)
    }

    @Provides
    @Singleton
    fun provideRoomConductorRepository(conductorDao: ConductorDao): RoomConductorRepositoryInterface {
        return RoomConductorRepository(conductorDao)
    }

    @Provides
    @Singleton
    fun provideRoomProductInfoRepository(productInfoDao: ProductInfoDao): RoomProductInfoRepositoryInterface {
        return RoomProductInfoRepository(productInfoDao)
    }

    // Use Cases
    // Cart Use Case
    @Provides
    @Singleton
    fun provideSaveCartWithItemsAndOperationUseCase(
        roomCartRepositoryInterface: RoomCartRepositoryInterface,
        inMemoryCartRepositoryInterface: InMemoryCartRepositoryInterface
    ): SaveCartWithItemsAndOperationUseCase {
        return SaveCartWithItemsAndOperationUseCase(
            roomCartRepositoryInterface,
            inMemoryCartRepositoryInterface
        )
    }

    // Conductor Use Cases
    @Provides
    @Singleton
    fun provideAddConductorUseCase(roomConductorRepositoryInterface: RoomConductorRepositoryInterface): AddConductorUseCase {
        return AddConductorUseCase(roomConductorRepositoryInterface)
    }

    @Provides
    @Singleton
    fun provideDeleteConductorUseCase(roomConductorRepositoryInterface: RoomConductorRepositoryInterface): DeleteConductorUseCase {
        return DeleteConductorUseCase(roomConductorRepositoryInterface)
    }

    // ProductInfo Use Cases
    @Provides
    @Singleton
    fun provideGetAllProductsUseCase(roomProductInfoRepositoryInterface: RoomProductInfoRepositoryInterface): GetAllProductsUseCase {
        return GetAllProductsUseCase(roomProductInfoRepositoryInterface)
    }

    @Provides
    @Singleton
    fun provideAddProductInfoUseCase(
        productInfoRepository: RoomProductInfoRepositoryInterface,
        productInfoDataSource: ProductInfoDataSourceInterface,
        imageRepository: LocalImageRepository
    ): AddProductInfoUseCase {
        return AddProductInfoUseCase(productInfoRepository, productInfoDataSource, imageRepository)
    }


    @Provides
    @Singleton
    fun provideDeleteProductUseCase(roomProductInfoRepositoryInterface: RoomProductInfoRepositoryInterface): DeleteProductUseCase {
        return DeleteProductUseCase(roomProductInfoRepositoryInterface)
    }

    // Report Use Cases
    @Provides
    @Singleton
    fun provideGetReportDataUseCase(reportRepository: RoomReportRepositoryInterface): GetReportDataUseCase {
        return GetReportDataUseCase(reportRepository)
    }
}
