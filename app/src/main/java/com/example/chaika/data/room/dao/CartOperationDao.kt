package com.example.chaika.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.chaika.data.room.entities.CartOperation
import com.example.chaika.data.room.entities.OperationInfoView
import com.example.chaika.data.room.relations.CartItemWithProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface CartOperationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(cartOperation: CartOperation): Long

    @Query("SELECT * FROM cart_operations WHERE id = :id")
    suspend fun getById(id: Int): CartOperation?

    @Query("SELECT * FROM cart_operations")
    fun getAllOperations(): Flow<List<CartOperation>>

    @Delete
    suspend fun delete(cartOperation: CartOperation)

    // Пагинация «шапок» из VIEW
    @Query("""
        SELECT * 
        FROM operation_info_view
        ORDER BY operation_time DESC, operation_id DESC
    """)
    fun getPagedOperationInfos(): PagingSource<Int, OperationInfoView>

    // Дозагрузка товаров конкретной операции (Room сам подтянет ProductInfo через @Relation)
    @Transaction
    @Query("""
        SELECT * 
        FROM cart_items
        WHERE cart_operation_id = :operationId
        ORDER BY id ASC
    """)
    fun observeItemsWithProducts(
        operationId: Int
    ): Flow<List<CartItemWithProduct>>
}
