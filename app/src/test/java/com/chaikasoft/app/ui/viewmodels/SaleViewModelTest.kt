package com.chaikasoft.app.ui.viewmodels

import com.chaikasoft.app.R
import com.chaikasoft.app.data.inmemory.InMemoryCartRepositoryInterface
import com.chaikasoft.app.domain.sealed.SaveOperationResult
import com.chaikasoft.app.domain.usecases.AddItemToCartWithLimitUseCase
import com.chaikasoft.app.domain.usecases.CreateCartUseCase
import com.chaikasoft.app.domain.usecases.GetCartItemsUseCase
import com.chaikasoft.app.domain.usecases.RemoveItemFromCartUseCase
import com.chaikasoft.app.domain.usecases.SoldCardOpUseCase
import com.chaikasoft.app.domain.usecases.SoldCashOpUseCase
import com.chaikasoft.app.domain.usecases.UpdateQuantityWithLimitUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SaleViewModelTest : FunSpec({

    lateinit var createCart: CreateCartUseCase
    lateinit var getCartItems: GetCartItemsUseCase
    lateinit var addItemToCart: AddItemToCartWithLimitUseCase
    lateinit var removeItemFromCart: RemoveItemFromCartUseCase
    lateinit var updateQuantityWithLimit: UpdateQuantityWithLimitUseCase
    lateinit var soldCashOp: SoldCashOpUseCase
    lateinit var soldCardOp: SoldCardOpUseCase
    lateinit var cart: InMemoryCartRepositoryInterface
    lateinit var vm: SaleViewModel

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        createCart = mockk()
        getCartItems = mockk()
        addItemToCart = mockk(relaxed = true)
        removeItemFromCart = mockk(relaxed = true)
        updateQuantityWithLimit = mockk(relaxed = true)
        soldCashOp = mockk()
        soldCardOp = mockk()
        cart = mockk()

        every { createCart() } returns cart
        every { getCartItems(cart) } returns flowOf(emptyList())
        coEvery { soldCashOp(cart, any()) } returns SaveOperationResult.Success(1)
        coEvery { soldCardOp(cart, any()) } returns SaveOperationResult.Success(1)

        vm = SaleViewModel(
            createCart = createCart,
            getCartItems = getCartItems,
            addItemToCartWithLimitUseCase = addItemToCart,
            removeItemFromCart = removeItemFromCart,
            updateQuantityWithLimit = updateQuantityWithLimit,
            soldCashOp = soldCashOp,
            soldCardOp = soldCardOp
        )
    }

    afterTest {
        Dispatchers.resetMain()
    }

    test("onSellCash maps results to correct dialog message") {
        runTest {
            coEvery { soldCashOp(cart, 1) } returns SaveOperationResult.EmptyCart
            vm.onSellCash(1)
            advanceUntilIdle()
            vm.sellResultDialog.value?.messageRes shouldBe R.string.sell_empty_cart

            coEvery { soldCashOp(cart, 1) } returns SaveOperationResult.Failure("x")
            vm.onSellCash(1)
            advanceUntilIdle()
            vm.sellResultDialog.value?.messageRes shouldBe R.string.sell_failure

            coEvery { soldCashOp(cart, 1) } returns SaveOperationResult.Success(12)
            vm.onSellCash(1)
            advanceUntilIdle()
            vm.sellResultDialog.value?.messageRes shouldBe R.string.sell_success
        }
    }

    test("onSellCard handles exception with failure dialog") {
        runTest {
            coEvery { soldCardOp(cart, 2) } throws IllegalStateException("boom")

            vm.onSellCard(2)
            advanceUntilIdle()

            vm.sellResultDialog.value?.messageRes shouldBe R.string.sell_failure
        }
    }

    test("dismissSellResultDialog clears dialog state") {
        runTest {
            vm.onSellCash(1)
            advanceUntilIdle()
            vm.sellResultDialog.value?.messageRes shouldBe R.string.sell_success

            vm.dismissSellResultDialog()

            vm.sellResultDialog.value shouldBe null
        }
    }
})

