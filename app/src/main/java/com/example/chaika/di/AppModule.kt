package com.example.chaika.di

import android.content.Context
import androidx.room.Room
import com.example.chaika.auth.OAuthManager
import com.example.chaika.data.crypto.EncryptedTokenManager
import com.example.chaika.data.crypto.EncryptedTokenManagerInterface
import com.example.chaika.data.dataSource.FakeProductInfoDataSource
import com.example.chaika.data.dataSource.ProductInfoDataSourceInterface
import com.example.chaika.data.dataSource.apiService.ChaikaSoftApiService
import com.example.chaika.data.dataSource.apiService.RemyApiService
import com.example.chaika.data.dataSource.repo.ChaikaSoftApiServiceRepository
import com.example.chaika.data.dataSource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.example.chaika.data.dataSource.repo.RemyApiServiceRepository
import com.example.chaika.data.dataSource.repo.RemyApiServiceRepositoryInterface
import com.example.chaika.data.inMemory.InMemoryCartRepository
import com.example.chaika.data.inMemory.InMemoryCartRepositoryInterface
import com.example.chaika.data.local.LocalImageRepository
import com.example.chaika.data.local.LocalImageRepositoryInterface
import com.example.chaika.data.local.LocalTripReportRepository
import com.example.chaika.data.room.AppDatabase
import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.dao.ConductorDao
import com.example.chaika.data.room.dao.ProductInfoDao
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
import com.example.chaika.domain.usecases.AuthorizeAndSaveConductorUseCase
import com.example.chaika.domain.usecases.DeleteAllConductorsUseCase
import com.example.chaika.domain.usecases.DeleteProductUseCase
import com.example.chaika.domain.usecases.FetchAndSaveProductsUseCase
import com.example.chaika.domain.usecases.FetchConductorByTokenUseCase
import com.example.chaika.domain.usecases.FetchProductsFromServerUseCase
import com.example.chaika.domain.usecases.GenerateTripReportUseCase
import com.example.chaika.domain.usecases.GetAccessTokenUseCase
import com.example.chaika.domain.usecases.GetAllProductsUseCase
import com.example.chaika.domain.usecases.GetPagedProductsUseCase
import com.example.chaika.domain.usecases.HandleAuthorizationResponseUseCase
import com.example.chaika.domain.usecases.LogoutUseCase
import com.example.chaika.domain.usecases.SaveCartWithItemsAndOperationUseCase
import com.example.chaika.domain.usecases.SaveConductorLocallyUseCase
import com.example.chaika.domain.usecases.SaveProductsLocallyUseCase
import com.example.chaika.domain.usecases.StartAuthorizationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // ================== DATABASE & DAOs ==================
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()

    @Provides
    fun provideCartItemDao(appDatabase: AppDatabase): CartItemDao = appDatabase.cartItemDao()

    @Provides
    fun provideCartOperationDao(appDatabase: AppDatabase): CartOperationDao = appDatabase.cartOperationDao()

    @Provides
    fun provideConductorDao(appDatabase: AppDatabase): ConductorDao = appDatabase.conductorDao()

    @Provides
    fun provideProductInfoDao(appDatabase: AppDatabase): ProductInfoDao = appDatabase.productInfoDao()

    // ================== ROOM REPOSITORIES ==================
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

    // ================== OTHER REPOSITORIES ==================
    @Provides
    @Singleton
    fun provideInMemoryCartRepository(): InMemoryCartRepositoryInterface = InMemoryCartRepository()

    @Provides
    @Singleton
    fun provideLocalImageRepository(
        @ApplicationContext context: Context,
    ): LocalImageRepositoryInterface = LocalImageRepository(context)

    @Provides
    @Singleton
    fun provideLocalTripReportRepository(
        @ApplicationContext context: Context,
    ): LocalTripReportRepository = LocalTripReportRepository(context)

    // ================== DATA SOURCES ==================
    @Provides
    @Singleton
    fun provideProductInfoDataSource(): ProductInfoDataSourceInterface = FakeProductInfoDataSource()

    // ================== RETROFIT & NETWORK ==================
    // Retrofit для Remy API
    @Provides
    @Singleton
    @Named("RemyRetrofit")
    fun provideRemyRetrofitInstance(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://iam.remystorage.ru/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // Remy API-сервис
    @Provides
    @Singleton
    fun provideRemyApiService(@Named("RemyRetrofit") retrofit: Retrofit): RemyApiService =
        retrofit.create(RemyApiService::class.java)

    // Remy репозиторий
    @Provides
    @Singleton
    fun provideRemyApiRepository(remyApiService: RemyApiService): RemyApiServiceRepositoryInterface =
        RemyApiServiceRepository(remyApiService)

    // Retrofit для ChaikaSoft API
    @Provides
    @Singleton
    @Named("ChaikaSoftRetrofit")
    fun provideChaikaSoftRetrofitInstance(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://chaika-soft.ru/") // URL для ChaikaSoft API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // Провайдер для ChaikaSoftApiService (интерфейс ChaikaSoft API)
    @Provides
    @Singleton
    @Named("ChaikaSoftApiService")
    fun provideChaikaSoftApiService(
        @Named("ChaikaSoftRetrofit") retrofit: Retrofit
    ): ChaikaSoftApiService =
        retrofit.create(ChaikaSoftApiService::class.java)

    // Провайдер для репозитория, реализующего ChaikaSoftApiServiceRepositoryInterface
    @Provides
    @Singleton
    fun provideChaikaSoftApiServiceRepository(
        @Named("ChaikaSoftApiService") apiService: ChaikaSoftApiService
    ): ChaikaSoftApiServiceRepositoryInterface =
        ChaikaSoftApiServiceRepository(apiService)

    // ================== ENCRYPTED TOKEN MANAGER ==================
    @Provides
    @Singleton
    fun provideEncryptedTokenManager(
        @ApplicationContext context: Context,
    ): EncryptedTokenManagerInterface = EncryptedTokenManager(context)

    // ================== AUTHORIZATION (OAUTH) ==================
    @Provides
    @Singleton
    fun provideAuthorizationService(
        @ApplicationContext context: Context,
    ): AuthorizationService = AuthorizationService(context)

    @Provides
    @Singleton
    fun provideOAuthManager(authorizationService: AuthorizationService): OAuthManager = OAuthManager(authorizationService)

    // ================== USE CASES: AUTHORIZATION ==================
    @Provides
    @Singleton
    fun provideStartAuthorizationUseCase(oAuthManager: OAuthManager): StartAuthorizationUseCase = StartAuthorizationUseCase(oAuthManager)

    @Provides
    @Singleton
    fun provideHandleAuthorizationResponseUseCase(
        oAuthManager: OAuthManager,
        tokenManager: EncryptedTokenManagerInterface,
    ): HandleAuthorizationResponseUseCase = HandleAuthorizationResponseUseCase(oAuthManager, tokenManager)

    @Provides
    @Singleton
    fun provideGetAccessTokenUseCase(tokenManager: EncryptedTokenManagerInterface): GetAccessTokenUseCase =
        GetAccessTokenUseCase(tokenManager)

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        tokenManager: EncryptedTokenManagerInterface,
        deleteAllConductorsUseCase: DeleteAllConductorsUseCase,
        imageRepository: LocalImageRepositoryInterface,
    ): LogoutUseCase = LogoutUseCase(tokenManager, deleteAllConductorsUseCase, imageRepository)

    // ================== USE CASES: OTHER ==================
    @Provides
    @Singleton
    fun provideSaveCartWithItemsAndOperationUseCase(
        roomCartRepositoryInterface: RoomCartRepositoryInterface,
        inMemoryCartRepositoryInterface: InMemoryCartRepositoryInterface,
    ): SaveCartWithItemsAndOperationUseCase =
        SaveCartWithItemsAndOperationUseCase(
            roomCartRepositoryInterface,
            inMemoryCartRepositoryInterface,
        )

    @Provides
    @Singleton
    fun provideSaveConductorLocallyUseCase(
        conductorRepository: RoomConductorRepositoryInterface,
        imageRepository: LocalImageRepositoryInterface,
    ): SaveConductorLocallyUseCase = SaveConductorLocallyUseCase(conductorRepository, imageRepository)

    @Provides
    @Singleton
    fun provideFetchConductorByTokenUseCase(remyApiServiceRepository: RemyApiServiceRepositoryInterface): FetchConductorByTokenUseCase =
        FetchConductorByTokenUseCase(remyApiServiceRepository)

    @Provides
    @Singleton
    fun provideAuthorizeAndSaveConductorUseCase(
        fetchConductorByTokenUseCase: FetchConductorByTokenUseCase,
        saveConductorLocallyUseCase: SaveConductorLocallyUseCase,
    ): AuthorizeAndSaveConductorUseCase = AuthorizeAndSaveConductorUseCase(fetchConductorByTokenUseCase, saveConductorLocallyUseCase)

    @Provides
    @Singleton
    fun provideGetAllProductsUseCase(repository: RoomProductInfoRepositoryInterface): GetAllProductsUseCase =
        GetAllProductsUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteAllConductorsUseCase(conductorRepository: RoomConductorRepositoryInterface): DeleteAllConductorsUseCase =
        DeleteAllConductorsUseCase(conductorRepository)

    @Provides
    @Singleton
    fun provideFetchProductsFromServerUseCase(
        repository: ChaikaSoftApiServiceRepositoryInterface
    ): FetchProductsFromServerUseCase =
        FetchProductsFromServerUseCase(repository)

    @Provides
    @Singleton
    fun provideSaveProductsLocallyUseCase(
        productInfoRepository: RoomProductInfoRepositoryInterface,
        localImageRepository: LocalImageRepositoryInterface
    ): SaveProductsLocallyUseCase =
        SaveProductsLocallyUseCase(productInfoRepository, localImageRepository)

    @Provides
    @Singleton
    fun provideFetchAndSaveProductsUseCase(
        fetchProductsFromServerUseCase: FetchProductsFromServerUseCase,
        saveProductsLocallyUseCase: SaveProductsLocallyUseCase
    ): FetchAndSaveProductsUseCase =
        FetchAndSaveProductsUseCase(fetchProductsFromServerUseCase, saveProductsLocallyUseCase)

    @Provides
    @Singleton
    fun provideGetPagedProductsUseCase(
        productInfoRepository: RoomProductInfoRepositoryInterface
    ): GetPagedProductsUseCase =
        GetPagedProductsUseCase(productInfoRepository)

    @Provides
    @Singleton
    fun provideDeleteProductUseCase(repository: RoomProductInfoRepositoryInterface): DeleteProductUseCase =
        DeleteProductUseCase(repository)

    @Provides
    @Singleton
    fun provideGenerateTripReportUseCase(
        cartOperationRepository: RoomCartOperationRepositoryInterface,
        cartItemRepository: RoomCartItemRepositoryInterface,
        conductorRepository: RoomConductorRepositoryInterface,
        tripReportRepository: LocalTripReportRepository,
    ): GenerateTripReportUseCase =
        GenerateTripReportUseCase(
            cartOperationRepository,
            cartItemRepository,
            conductorRepository,
            tripReportRepository,
        )
}
