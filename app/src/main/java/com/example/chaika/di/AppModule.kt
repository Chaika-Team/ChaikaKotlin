package com.example.chaika.di

import android.content.Context
import com.example.chaika.dataBase.AppDatabase
import com.example.chaika.dataBase.dao.ActionDao
import com.example.chaika.dataBase.dao.ProductDao
import com.example.chaika.dataBase.dao.TripDao
import com.example.chaika.dataBase.models.ActionRepository
import com.example.chaika.dataBase.models.ProductRepository
import com.example.chaika.dataBase.models.TripRepository
import com.example.chaika.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ---------------------------------
    // Database and DAO Providers
    // ---------------------------------

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getInstance(context, CoroutineScope(SupervisorJob() + Dispatchers.IO))
    }

    @Provides
    fun provideTripDao(appDatabase: AppDatabase): TripDao {
        return appDatabase.tripDao()
    }

    @Provides
    fun provideActionDao(appDatabase: AppDatabase): ActionDao {
        return appDatabase.actionDao()
    }

    @Provides
    fun provideProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    // ---------------------------------
    // Repository Providers
    // ---------------------------------

    @Provides
    @Singleton
    fun provideTripRepository(tripDao: TripDao): TripRepository {
        return TripRepository(tripDao)
    }

    @Provides
    @Singleton
    fun provideActionRepository(actionDao: ActionDao): ActionRepository {
        return ActionRepository(actionDao)
    }

    @Provides
    @Singleton
    fun provideProductRepository(productDao: ProductDao): ProductRepository {
        return ProductRepository(productDao)
    }

    // ---------------------------------
    // Trip Use Cases Providers
    // ---------------------------------

    @Provides
    fun provideGetAllTripsUseCase(tripRepository: TripRepository): GetAllTripsUseCase {
        return GetAllTripsUseCase(tripRepository)
    }

    @Provides
    fun provideInsertTripUseCase(tripRepository: TripRepository): InsertTripUseCase {
        return InsertTripUseCase(tripRepository)
    }

    @Provides
    fun provideDeleteTripAndActionsUseCase(
        tripRepository: TripRepository,
        actionRepository: ActionRepository
    ): DeleteTripAndActionsUseCase {
        return DeleteTripAndActionsUseCase(tripRepository, actionRepository)
    }

    @Provides
    fun provideUpdateTripUseCase(tripRepository: TripRepository): UpdateTripUseCase {
        return UpdateTripUseCase(tripRepository)
    }

    @Provides
    fun provideFilterTripsUseCase(): FilterTripsUseCase {
        return FilterTripsUseCase()
    }

    // ---------------------------------
    // Product List Use Cases Providers
    // ---------------------------------

    @Provides
    fun provideGetAllProductsUseCase(productRepository: ProductRepository): GetAllProductsUseCase {
        return GetAllProductsUseCase(productRepository)
    }

    @Provides
    fun provideInsertProductsUseCase(productRepository: ProductRepository): InsertProductsUseCase {
        return InsertProductsUseCase(productRepository)
    }

    @Provides
    fun provideAddProductActionUseCase(actionRepository: ActionRepository): AddProductActionUseCase {
        return AddProductActionUseCase(actionRepository)
    }

    @Provides
    fun provideFilterProductsUseCase(): FilterProductsUseCase {
        return FilterProductsUseCase()
    }

    // ---------------------------------
    // Product Table Use Cases Providers
    // ---------------------------------

    @Provides
    fun provideGetProductsByTripUseCase(productRepository: ProductRepository): GetProductsByTripUseCase {
        return GetProductsByTripUseCase(productRepository)
    }

    @Provides
    fun provideDeleteActionsForProductInTripUseCase(actionRepository: ActionRepository): DeleteActionsForProductInTripUseCase {
        return DeleteActionsForProductInTripUseCase(actionRepository)
    }
}
