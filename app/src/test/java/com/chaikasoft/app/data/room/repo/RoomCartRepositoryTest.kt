package com.chaikasoft.app.data.room.repo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.chaikasoft.app.data.room.AppDatabase
import com.chaikasoft.app.data.room.entities.Conductor
import com.chaikasoft.app.data.room.entities.ProductInfo
import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.CartOperationDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import io.mockk.coEvery
import io.mockk.spyk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomCartRepositoryTest {

    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    /**
     * Test-design technique: #7 Decision table.
     * Saving a cart must persist one operation and all of its items atomically.
     */
    @Test
    fun saveCartWithItemsAndOperation_success_persistsOperationAndItems() = runTest {
        seedConductorAndProduct()
        val repository = createRepository()

        val operationId = repository.saveCartWithItemsAndOperation(cart(), operation())

        assertEquals(1, db.cartOperationDao().getAllOperations().first().size)
        assertEquals(operationId, db.cartOperationDao().getAllOperations().first().first().id)
        assertEquals(1, db.cartItemDao().getAllCartItems().first().size)
        assertEquals(operationId, db.cartItemDao().getAllCartItems().first().first().cartOperationId)
    }

    /**
     * Test-design technique: #5 Error guessing.
     * If item insertion fails after operation insertion, Room must roll back the operation too.
     */
    @Test
    fun saveCartWithItemsAndOperation_itemInsertFailure_rollsBackOperation() = runTest {
        seedConductorAndProduct()
        val failingCartItemDao = spyk(db.cartItemDao())
        coEvery { failingCartItemDao.insertCartItem(any()) } throws IllegalStateException("item failed")
        val repository = createRepository(cartItemRepository = failingCartItemDao)

        try {
            repository.saveCartWithItemsAndOperation(cart(), operation())
            fail("Expected IllegalStateException")
        } catch (_: IllegalStateException) {
        }

        assertEquals(emptyList<Any>(), db.cartOperationDao().getAllOperations().first())
        assertEquals(emptyList<Any>(), db.cartItemDao().getAllCartItems().first())
    }

    private fun createRepository(
        cartItemRepository: com.chaikasoft.app.data.room.dao.CartItemDao = db.cartItemDao()
    ): RoomCartRepository = RoomCartRepository(
        db = db,
        cartItemDao = cartItemRepository,
        cartOperationDao = db.cartOperationDao()
    )

    private suspend fun seedConductorAndProduct() {
        db.conductorDao().insertConductor(
            Conductor(
                id = CONDUCTOR_ID,
                name = "Ivan",
                familyName = "Petrov",
                givenName = "Ivanovich",
                employeeID = "EMP-7",
                image = "img"
            )
        )
        db.productInfoDao().insertProduct(
            ProductInfo(
                id = PRODUCT_ID,
                name = "Tea",
                description = "Black",
                image = "img",
                price = 150
            )
        )
    }

    private fun cart(): CartDomain = CartDomain(
        items = listOf(
            CartItemDomain(
                product = ProductInfoDomain(
                    id = PRODUCT_ID,
                    name = "Tea",
                    description = "Black",
                    image = "img",
                    price = 150
                ),
                quantity = 2
            )
        )
    )

    private fun operation(): CartOperationDomain = CartOperationDomain(
        operationTypeDomain = OperationTypeDomain.ADD,
        conductorId = CONDUCTOR_ID
    )

    private companion object {
        const val PRODUCT_ID = 100
        const val CONDUCTOR_ID = 7
    }
}
