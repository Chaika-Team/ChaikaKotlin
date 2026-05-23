package com.chaikasoft.app.ui.viewmodels

import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.usecases.GetPagedProductsUseCase
import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest : FunSpec({

    lateinit var getPagedProductsUseCase: GetPagedProductsUseCase
    lateinit var vm: ProductViewModel

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        getPagedProductsUseCase = mockk()
        vm = ProductViewModel(getPagedProductsUseCase)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    test("attachCart only attaches cart and does not request product paging") {
        runTest {
            val cartItems = MutableStateFlow<List<CartItemDomain>>(emptyList())

            vm.attachCart(cartItems)
            advanceUntilIdle()

            verify(exactly = 0) { getPagedProductsUseCase(any()) }
        }
    }

    test("repeated attachCart remains idempotent") {
        runTest {
            val cartItems = MutableStateFlow<List<CartItemDomain>>(emptyList())

            vm.attachCart(cartItems)
            vm.attachCart(cartItems)
            advanceUntilIdle()

            verify(exactly = 0) { getPagedProductsUseCase(any()) }
        }
    }
})
