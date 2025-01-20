package com.example.chaika.di

import android.content.Context
import androidx.room.Room
import com.example.chaika.data.data_source.apiService.AuthApiService
import com.example.chaika.data.data_source.ProductInfoDataSourceInterface
import com.example.chaika.data.inMemory.InMemoryCartRepository
import com.example.chaika.data.inMemory.InMemoryCartRepositoryInterface
import com.example.chaika.data.local.LocalImageRepository
import com.example.chaika.data.local.LocalTripReportRepository
import com.example.chaika.data.room.AppDatabase
import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.dao.ConductorDao
import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.repo.*
import com.example.chaika.data.crypto.KeyStoreCryptoManager
import com.example.chaika.data.crypto.KeyStoreCryptoManagerInterface
import com.example.chaika.data.data_source.FakeProductInfoDataSource
import com.example.chaika.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ================== Database ==================
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

    // ================== DAOs ==================
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

    // ================== Repositories ==================
    @Provides
    @Singleton
    fun provideRoomCartOperationRepository(
        cartOperationDao: CartOperationDao
    ): RoomCartOperationRepositoryInterface {
        return RoomCartOperationRepository(cartOperationDao)
    }

    @Provides
    @Singleton
    fun provideRoomCartItemRepository(
        cartItemDao: CartItemDao,
        productInfoDao: ProductInfoDao
    ): RoomCartItemRepositoryInterface {
        return RoomCartItemRepository(cartItemDao, productInfoDao)
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

    @Provides
    @Singleton
    fun provideInMemoryCartRepository(): InMemoryCartRepositoryInterface {
        return InMemoryCartRepository()
    }

    @Provides
    @Singleton
    fun provideLocalImageRepository(
        @ApplicationContext context: Context
    ): LocalImageRepository {
        return LocalImageRepository(context)
    }

    @Provides
    @Singleton
    fun provideLocalTripReportRepository(
        @ApplicationContext context: Context
    ): LocalTripReportRepository {
        return LocalTripReportRepository(context)
    }

    // ================== Data Sources ==================
    @Provides
    @Singleton
    fun provideProductInfoDataSource(): ProductInfoDataSourceInterface {
        return FakeProductInfoDataSource()
    }

    // Retrofit: AuthApiService
    @Provides
    @Singleton
    fun provideAuthApiService(): AuthApiService {
        return Retrofit.Builder()
            .baseUrl("https://example.com/api/") // Replace with actual base URL
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    // ================== Use Cases ==================
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
    fun provideFetchConductorFromServerUseCase(
        authApiService: AuthApiService
    ): FetchConductorFromServerUseCase {
        return FetchConductorFromServerUseCase(authApiService)
    }

    @Provides
    @Singleton
    fun provideEncryptConductorTokenUseCase(
        cryptoManager: KeyStoreCryptoManagerInterface
    ): EncryptConductorTokenUseCase {
        return EncryptConductorTokenUseCase(cryptoManager)
    }

    @Provides
    @Singleton
    fun provideSaveConductorLocallyUseCase(
        conductorRepository: RoomConductorRepositoryInterface,
        imageRepository: LocalImageRepository
    ): SaveConductorLocallyUseCase {
        return SaveConductorLocallyUseCase(conductorRepository, imageRepository)
    }

    @Provides
    @Singleton
    fun provideAuthorizeConductorUseCase(
        fetchConductorFromServerUseCase: FetchConductorFromServerUseCase,
        encryptConductorTokenUseCase: EncryptConductorTokenUseCase,
        saveConductorLocallyUseCase: SaveConductorLocallyUseCase
    ): AuthorizeConductorUseCase {
        return AuthorizeConductorUseCase(
            fetchConductorFromServerUseCase,
            encryptConductorTokenUseCase,
            saveConductorLocallyUseCase
        )
    }

    @Provides
    @Singleton
    fun provideGetAllProductsUseCase(
        roomProductInfoRepositoryInterface: RoomProductInfoRepositoryInterface
    ): GetAllProductsUseCase {
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
    fun provideDeleteProductUseCase(
        roomProductInfoRepositoryInterface: RoomProductInfoRepositoryInterface
    ): DeleteProductUseCase {
        return DeleteProductUseCase(roomProductInfoRepositoryInterface)
    }

    @Provides
    @Singleton
    fun provideGenerateTripReportUseCase(
        cartOperationRepository: RoomCartOperationRepositoryInterface,
        cartItemRepository: RoomCartItemRepositoryInterface,
        conductorRepository: RoomConductorRepositoryInterface,
        tripReportRepository: LocalTripReportRepository
    ): GenerateTripReportUseCase {
        return GenerateTripReportUseCase(
            cartOperationRepository,
            cartItemRepository,
            conductorRepository,
            tripReportRepository
        )
    }
}
