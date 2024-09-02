package com.example.chaika.di

import android.content.Context
import androidx.room.Room
import com.example.chaika.data.room.repo.CartRepository
import com.example.chaika.data.room.AppDatabase
import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.domain.usecases.SaveCartWithItemsAndOperationUseCase
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }


    @Provides
    fun provideCartItemDao(appDatabase: AppDatabase): CartItemDao {
        return appDatabase.cartItemDao()
    }

    @Provides
    fun provideCartOperationDao(appDatabase: AppDatabase): CartOperationDao {
        return appDatabase.cartOperationDao()
    }

    @Provides
    fun provideCartRepository(
        cartItemDao: CartItemDao,
        cartOperationDao: CartOperationDao
    ): CartRepository {
        return CartRepository(cartItemDao, cartOperationDao)
    }

    @Provides
    fun provideSaveCartWithItemsAndOperationUseCase(
        cartRepository: CartRepository
    ): SaveCartWithItemsAndOperationUseCase {
        return SaveCartWithItemsAndOperationUseCase(cartRepository)
    }
}
