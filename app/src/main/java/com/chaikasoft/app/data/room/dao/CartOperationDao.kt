package com.chaikasoft.app.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.chaikasoft.app.data.room.entities.CartOperation
import com.chaikasoft.app.data.room.entities.OperationInfoView
import com.chaikasoft.app.data.room.relations.CartItemWithProduct
import com.chaikasoft.app.data.room.relations.CartOperationWithConductor
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

    /** Полностью удалить все операции (каскадом удалятся cart_items). */
    @Query("DELETE FROM cart_operations")
    suspend fun clearAllOperations()

    // Пагинация «шапок» из VIEW
    @Query(
        """
        SELECT *
        FROM operation_info_view
        ORDER BY operation_time DESC, operation_id DESC
        """
    )
    fun getPagedOperationInfos(): PagingSource<Int, OperationInfoView>

    // Дозагрузка товаров конкретной операции (Room сам подтянет ProductInfo через @Relation)
    @Transaction
    @Query(
        """
        SELECT *
        FROM cart_items
        WHERE cart_operation_id = :operationId
        ORDER BY id ASC
        """
    )
    fun observeItemsWithProducts(operationId: Int): Flow<List<CartItemWithProduct>>

    /** Шапки для отчёта: операция + её проводник (через @Relation). */
    @Transaction
    @Query(
        """
        SELECT *
        FROM cart_operations
        ORDER BY operation_time ASC, id ASC
        """
    )
    fun getOperationsWithConductorForReport(): Flow<List<CartOperationWithConductor>>

    /** Одноразовый счётчик по типу операции */
    @Query("SELECT COUNT(*) FROM cart_operations WHERE operation_type = :type")
    suspend fun countByType(type: Int): Int

    /** Пагинированный список «шапок» по типу из VIEW (удобно для UI) */
    @Query(
        """
        SELECT *
        FROM operation_info_view
        WHERE operation_type = :type
        ORDER BY operation_time DESC, operation_id DESC
        """
    )
    fun getPagedOperationInfosByType(type: Int): PagingSource<Int, OperationInfoView>
}
