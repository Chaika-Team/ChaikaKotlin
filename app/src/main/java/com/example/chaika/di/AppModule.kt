package com.example.chaika.di

import android.content.Context
import androidx.room.Room
import com.example.chaika.data.crypto.EncryptedTokenManagerInterface
import com.example.chaika.data.crypto.EncryptedTokenManager
import com.example.chaika.data.data_source.FakeProductInfoDataSource
import com.example.chaika.data.data_source.ProductInfoDataSourceInterface
import com.example.chaika.data.data_source.apiService.ApiService
import com.example.chaika.auth.OAuthManager
import com.example.chaika.data.data_source.repo.ApiServiceRepository
import com.example.chaika.data.data_source.repo.ApiServiceRepositoryInterface
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
import com.example.chaika.domain.usecases.AddProductInfoUseCase
import com.example.chaika.domain.usecases.AuthorizeAndSaveConductorUseCase
import com.example.chaika.domain.usecases.DeleteAllConductorsUseCase
import com.example.chaika.domain.usecases.DeleteProductUseCase
import com.example.chaika.domain.usecases.FetchConductorByTokenUseCase
import com.example.chaika.domain.usecases.GenerateTripReportUseCase
import com.example.chaika.domain.usecases.GetAllProductsUseCase
import com.example.chaika.domain.usecases.SaveCartWithItemsAndOperationUseCase
import com.example.chaika.domain.usecases.SaveConductorLocallyUseCase
import com.example.chaika.domain.usecases.HandleAuthorizationResponseUseCase
import com.example.chaika.domain.usecases.StartAuthorizationUseCase
import com.example.chaika.domain.usecases.GetAccessTokenUseCase
import com.example.chaika.domain.usecases.LogoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ================== DATABASE & DAOs ==================
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()

    @Provides
    fun provideCartItemDao(appDatabase: AppDatabase): CartItemDao = appDatabase.cartItemDao()

    @Provides
    fun provideCartOperationDao(appDatabase: AppDatabase): CartOperationDao =
        appDatabase.cartOperationDao()

    @Provides
    fun provideConductorDao(appDatabase: AppDatabase): ConductorDao = appDatabase.conductorDao()

    @Provides
    fun provideProductInfoDao(appDatabase: AppDatabase): ProductInfoDao =
        appDatabase.productInfoDao()

    // ================== ROOM REPOSITORIES ==================
    @Provides
    @Singleton
    fun provideRoomCartOperationRepository(cartOperationDao: CartOperationDao): RoomCartOperationRepositoryInterface =
        RoomCartOperationRepository(cartOperationDao)

    @Provides
    @Singleton
    fun provideRoomCartItemRepository(
        cartItemDao: CartItemDao,
        productInfoDao: ProductInfoDao
    ): RoomCartItemRepositoryInterface =
        RoomCartItemRepository(cartItemDao, productInfoDao)

    @Provides
    @Singleton
    fun provideRoomCartRepository(
        cartItemDao: CartItemDao,
        cartOperationDao: CartOperationDao
    ): RoomCartRepositoryInterface =
        RoomCartRepository(cartItemDao, cartOperationDao)

    @Provides
    @Singleton
    fun provideRoomConductorRepository(conductorDao: ConductorDao): RoomConductorRepositoryInterface =
        RoomConductorRepository(conductorDao)

    @Provides
    @Singleton
    fun provideRoomProductInfoRepository(productInfoDao: ProductInfoDao): RoomProductInfoRepositoryInterface =
        RoomProductInfoRepository(productInfoDao)

    // ================== OTHER REPOSITORIES ==================
    @Provides
    @Singleton
    fun provideInMemoryCartRepository(): InMemoryCartRepositoryInterface = InMemoryCartRepository()

    @Provides
    @Singleton
    fun provideLocalImageRepository(@ApplicationContext context: Context): LocalImageRepository =
        LocalImageRepository(context)

    @Provides
    @Singleton
    fun provideLocalTripReportRepository(@ApplicationContext context: Context): LocalTripReportRepository =
        LocalTripReportRepository(context)

    // ================== DATA SOURCES ==================
    @Provides
    @Singleton
    fun provideProductInfoDataSource(): ProductInfoDataSourceInterface = FakeProductInfoDataSource()

    // ================== RETROFIT & NETWORK ==================
    @Provides
    @Singleton
    fun provideRetrofitInstance(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://iam.remystorage.ru/") // или ваш базовый URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideConductorApiRepository(apiService: ApiService): ApiServiceRepositoryInterface =
        ApiServiceRepository(apiService)

    // ================== ENCRYPTED TOKEN MANAGER ==================
    @Provides
    @Singleton
    fun provideEncryptedTokenManager(@ApplicationContext context: Context): EncryptedTokenManagerInterface =
        EncryptedTokenManager(context)

    // ================== AUTHORIZATION (OAUTH) ==================
    @Provides
    @Singleton
    fun provideAuthorizationService(@ApplicationContext context: Context): AuthorizationService =
        AuthorizationService(context)

    @Provides
    @Singleton
    fun provideOAuthManager(authorizationService: AuthorizationService): OAuthManager =
        OAuthManager(authorizationService)

    // ================== USE CASES: AUTHORIZATION ==================
    @Provides
    @Singleton
    fun provideStartAuthorizationUseCase(oAuthManager: OAuthManager): StartAuthorizationUseCase =
        StartAuthorizationUseCase(oAuthManager)

    @Provides
    @Singleton
    fun provideHandleAuthorizationResponseUseCase(
        oAuthManager: OAuthManager,
        tokenManager: EncryptedTokenManagerInterface
    ): HandleAuthorizationResponseUseCase =
        HandleAuthorizationResponseUseCase(oAuthManager, tokenManager)

    @Provides
    @Singleton
    fun provideGetAccessTokenUseCase(tokenManager: EncryptedTokenManagerInterface): GetAccessTokenUseCase =
        GetAccessTokenUseCase(tokenManager)

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        tokenManager: EncryptedTokenManagerInterface,
        deleteAllConductorsUseCase: DeleteAllConductorsUseCase
    ): LogoutUseCase =
        LogoutUseCase(tokenManager, deleteAllConductorsUseCase)


    // ================== USE CASES: OTHER ==================
    @Provides
    @Singleton
    fun provideSaveCartWithItemsAndOperationUseCase(
        roomCartRepositoryInterface: RoomCartRepositoryInterface,
        inMemoryCartRepositoryInterface: InMemoryCartRepositoryInterface
    ): SaveCartWithItemsAndOperationUseCase =
        SaveCartWithItemsAndOperationUseCase(
            roomCartRepositoryInterface,
            inMemoryCartRepositoryInterface
        )

    @Provides
    @Singleton
    fun provideSaveConductorLocallyUseCase(
        conductorRepository: RoomConductorRepositoryInterface,
        imageRepository: LocalImageRepository
    ): SaveConductorLocallyUseCase =
        SaveConductorLocallyUseCase(conductorRepository, imageRepository)

    @Provides
    @Singleton
    fun provideFetchConductorByTokenUseCase(
        conductorApiRepository: ApiServiceRepositoryInterface
    ): FetchConductorByTokenUseCase =
        FetchConductorByTokenUseCase(conductorApiRepository)

    @Provides
    @Singleton
    fun provideAuthorizeAndSaveConductorUseCase(
        fetchConductorByTokenUseCase: FetchConductorByTokenUseCase,
        saveConductorLocallyUseCase: SaveConductorLocallyUseCase
    ): AuthorizeAndSaveConductorUseCase =
        AuthorizeAndSaveConductorUseCase(fetchConductorByTokenUseCase, saveConductorLocallyUseCase)

    @Provides
    @Singleton
    fun provideGetAllProductsUseCase(
        roomProductInfoRepositoryInterface: RoomProductInfoRepositoryInterface
    ): GetAllProductsUseCase =
        GetAllProductsUseCase(roomProductInfoRepositoryInterface)

    @Provides
    @Singleton
    fun provideDeleteAllConductorsUseCase(
        conductorRepository: RoomConductorRepositoryInterface
    ): DeleteAllConductorsUseCase =
        DeleteAllConductorsUseCase(conductorRepository)


    @Provides
    @Singleton
    fun provideAddProductInfoUseCase(
        productInfoRepository: RoomProductInfoRepositoryInterface,
        productInfoDataSource: ProductInfoDataSourceInterface,
        imageRepository: LocalImageRepository
    ): AddProductInfoUseCase =
        AddProductInfoUseCase(productInfoRepository, productInfoDataSource, imageRepository)

    @Provides
    @Singleton
    fun provideDeleteProductUseCase(
        roomProductInfoRepositoryInterface: RoomProductInfoRepositoryInterface
    ): DeleteProductUseCase =
        DeleteProductUseCase(roomProductInfoRepositoryInterface)

    @Provides
    @Singleton
    fun provideGenerateTripReportUseCase(
        cartOperationRepository: RoomCartOperationRepositoryInterface,
        cartItemRepository: RoomCartItemRepositoryInterface,
        conductorRepository: RoomConductorRepositoryInterface,
        tripReportRepository: LocalTripReportRepository
    ): GenerateTripReportUseCase =
        GenerateTripReportUseCase(
            cartOperationRepository,
            cartItemRepository,
            conductorRepository,
            tripReportRepository
        )
}
