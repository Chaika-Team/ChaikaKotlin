package com.chaikasoft.app.ui.viewmodels

import com.chaikasoft.app.R
import com.chaikasoft.app.data.inmemory.InMemoryCartRepositoryInterface
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.sealed.AddItemToCartWithLimitResult
import com.chaikasoft.app.domain.sealed.SaveOperationResult
import com.chaikasoft.app.domain.usecases.AddItemToCartWithLimitUseCase
import com.chaikasoft.app.domain.usecases.CreateCartUseCase
import com.chaikasoft.app.domain.usecases.GetAvailableQuantityUseCase
import com.chaikasoft.app.domain.usecases.GetCartItemsUseCase
import com.chaikasoft.app.domain.usecases.RemoveItemFromCartUseCase
import com.chaikasoft.app.domain.usecases.SoldCardOpUseCase
import com.chaikasoft.app.domain.usecases.SoldCashOpUseCase
import com.chaikasoft.app.domain.usecases.UpdateQuantityWithLimitUseCase
import com.chaikasoft.app.ui.cartItem
import com.chaikasoft.app.ui.productInfo
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
    lateinit var getAvailableQuantity: GetAvailableQuantityUseCase
    lateinit var soldCashOp: SoldCashOpUseCase
    lateinit var soldCardOp: SoldCardOpUseCase
    lateinit var cart: InMemoryCartRepositoryInterface
    lateinit var cartItemsFlow: MutableStateFlow<List<CartItemDomain>>
    lateinit var vm: SaleViewModel

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        createCart = mockk()
        getCartItems = mockk()
        addItemToCart = mockk(relaxed = true)
        removeItemFromCart = mockk(relaxed = true)
        updateQuantityWithLimit = mockk(relaxed = true)
        getAvailableQuantity = mockk()
        soldCashOp = mockk()
        soldCardOp = mockk()
        cart = mockk()
        cartItemsFlow = MutableStateFlow(emptyList())

        every { createCart() } returns cart
        every { getCartItems(cart) } returns cartItemsFlow
        coEvery { getAvailableQuantity(any()) } returns 1
        coEvery { soldCashOp(cart, any()) } returns SaveOperationResult.Success(1)
        coEvery { soldCardOp(cart, any()) } returns SaveOperationResult.Success(1)

        vm = SaleViewModel(
            createCart = createCart,
            getCartItems = getCartItems,
            addItemToCartWithLimitUseCase = addItemToCart,
            removeItemFromCart = removeItemFromCart,
            updateQuantityWithLimit = updateQuantityWithLimit,
            getAvailableQuantity = getAvailableQuantity,
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

    test("onAdd emits stock limit notice when product is out of stock") {
        runTest {
            val item = cartItem(product = productInfo(id = 1, name = "Tea"), quantity = 1)
            coEvery { addItemToCart(cart, item) } returns AddItemToCartWithLimitResult.OutOfStock

            vm.onAdd(item)
            advanceUntilIdle()

            vm.stockLimitNotice.value?.messageRes shouldBe R.string.error_out_of_stock
        }
    }

    test("onAdd does not emit stock limit notice when item is already in cart") {
        runTest {
            val item = cartItem(product = productInfo(id = 1, name = "Tea"), quantity = 1)
            coEvery { addItemToCart(cart, item) } returns AddItemToCartWithLimitResult.AlreadyInCart

            vm.onAdd(item)
            advanceUntilIdle()

            vm.stockLimitNotice.value shouldBe null
        }
    }

    test("onAdd does not emit stock limit notice when item is added") {
        runTest {
            val item = cartItem(product = productInfo(id = 1, name = "Tea"), quantity = 1)
            coEvery { addItemToCart(cart, item) } returns AddItemToCartWithLimitResult.Added

            vm.onAdd(item)
            advanceUntilIdle()

            vm.stockLimitNotice.value shouldBe null
        }
    }

    test("onQuantityChange emits stock limit notice when quantity update is rejected") {
        runTest {
            coEvery { updateQuantityWithLimit(cart, 1, 2) } returns false

            vm.onQuantityChange(1, 2)
            advanceUntilIdle()

            vm.stockLimitNotice.value?.messageRes shouldBe R.string.error_out_of_stock
        }
    }

    test("onQuantityChange does not emit stock limit notice when item is removed") {
        runTest {
            coEvery { updateQuantityWithLimit(cart, 1, 0) } returns true

            vm.onQuantityChange(1, 0)
            advanceUntilIdle()

            vm.stockLimitNotice.value shouldBe null
        }
    }

    test("onQuantityChange does not emit stock limit notice for non-positive rejection") {
        runTest {
            coEvery { updateQuantityWithLimit(cart, 1, -1) } returns false

            vm.onQuantityChange(1, -1)
            advanceUntilIdle()

            vm.stockLimitNotice.value shouldBe null
        }
    }

    test("dismissStockLimitNotice clears pending stock limit notice") {
        runTest {
            val item = cartItem(product = productInfo(id = 1, name = "Tea"), quantity = 1)
            coEvery { addItemToCart(cart, item) } returns AddItemToCartWithLimitResult.OutOfStock

            vm.onAdd(item)
            advanceUntilIdle()
            vm.stockLimitNotice.value?.messageRes shouldBe R.string.error_out_of_stock

            vm.dismissStockLimitNotice()

            vm.stockLimitNotice.value shouldBe null
        }
    }

    test("onSellCash emits sold-out notice for products that reached zero") {
        runTest {
            val tea = productInfo(id = 1, name = "Чай")
            val coffee = productInfo(id = 2, name = "Кофе")
            cartItemsFlow.value = listOf(
                cartItem(product = tea, quantity = 1),
                cartItem(product = coffee, quantity = 2)
            )
            coEvery { getAvailableQuantity(1) } returns 0
            coEvery { getAvailableQuantity(2) } returns 0

            vm.onSellCash(1)
            advanceUntilIdle()

            vm.soldOutNotice.value?.productNames shouldContainExactly listOf("Чай", "Кофе")
        }
    }

    test("onSellCash emits sold-out notice only for newly depleted product") {
        runTest {
            val tea = productInfo(id = 1, name = "Чай")
            val coffee = productInfo(id = 2, name = "Кофе")
            cartItemsFlow.value = listOf(
                cartItem(product = tea, quantity = 1),
                cartItem(product = coffee, quantity = 1)
            )
            coEvery { getAvailableQuantity(1) } returns 3
            coEvery { getAvailableQuantity(2) } returns 0

            vm.onSellCash(1)
            advanceUntilIdle()

            vm.soldOutNotice.value?.productNames shouldContainExactly listOf("Кофе")
        }
    }

    test("onSellCash does not emit sold-out notice for non-success result") {
        runTest {
            cartItemsFlow.value = listOf(
                cartItem(product = productInfo(id = 1, name = "Чай"), quantity = 1)
            )
            coEvery { getAvailableQuantity(1) } returns 0

            coEvery { soldCashOp(cart, 1) } returns SaveOperationResult.EmptyCart
            vm.onSellCash(1)
            advanceUntilIdle()
            vm.soldOutNotice.value shouldBe null

            coEvery { soldCashOp(cart, 1) } returns SaveOperationResult.Failure("x")
            vm.onSellCash(1)
            advanceUntilIdle()
            vm.soldOutNotice.value shouldBe null
        }
    }

    test("dismissSoldOutNotice clears pending notice") {
        runTest {
            cartItemsFlow.value = listOf(
                cartItem(product = productInfo(id = 1, name = "Чай"), quantity = 1)
            )
            coEvery { getAvailableQuantity(1) } returns 0

            vm.onSellCash(1)
            advanceUntilIdle()
            vm.soldOutNotice.value?.productNames shouldContainExactly listOf("Чай")

            vm.dismissSoldOutNotice()

            vm.soldOutNotice.value shouldBe null
        }
    }

    test("onSellCard handles exception with failure dialog") {
        runTest {
            coEvery { soldCardOp(cart, 2) } throws IllegalStateException("boom")

            vm.onSellCard(2)
            advanceUntilIdle()

            vm.sellResultDialog.value?.messageRes shouldBe R.string.sell_failure
            vm.soldOutNotice.value shouldBe null
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
