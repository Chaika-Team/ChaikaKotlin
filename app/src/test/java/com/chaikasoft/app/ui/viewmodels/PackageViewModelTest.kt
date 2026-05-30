package com.chaikasoft.app.ui.viewmodels

import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.PackageItemDomain
import com.chaikasoft.app.domain.usecases.GetAvailableQuantityUseCase
import com.chaikasoft.app.domain.usecases.GetPackageItemUseCase
import com.chaikasoft.app.ui.cartItem
import com.chaikasoft.app.ui.packageItem
import com.chaikasoft.app.ui.productInfo
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class PackageViewModelTest : FunSpec({

    lateinit var getPackageItems: GetPackageItemUseCase
    lateinit var getAvailableQuantity: GetAvailableQuantityUseCase
    lateinit var packageFlow: MutableStateFlow<List<PackageItemDomain>>
    lateinit var cartFlow: MutableStateFlow<List<CartItemDomain>>
    lateinit var vm: PackageViewModel

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        getPackageItems = mockk()
        getAvailableQuantity = mockk()
        packageFlow = MutableStateFlow(emptyList())
        cartFlow = MutableStateFlow(emptyList())
        every { getPackageItems() } returns packageFlow

        vm = PackageViewModel(getPackageItems, getAvailableQuantity)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    test("loadProducts merges package and cart with quantity>=1 rule") {
        runTest {
            val p1 = productInfo(id = 1, name = "Tea")
            val p2 = productInfo(id = 2, name = "Coffee")
            packageFlow.value = listOf(packageItem(product = p1), packageItem(product = p2))
            cartFlow.value = listOf(
                cartItem(product = p1, quantity = 2),
                cartItem(product = p2, quantity = 0)
            )

            vm.loadProducts(cartFlow)
            advanceUntilIdle()

            vm.productsFlow.value shouldHaveSize 2
            vm.productsFlow.value.first { it.id == 1 }.isInCart shouldBe true
            vm.productsFlow.value.first { it.id == 1 }.quantity shouldBe 2
            vm.productsFlow.value.first { it.id == 2 }.isInCart shouldBe false
            vm.productsFlow.value.first { it.id == 2 }.quantity shouldBe 0
        }
    }

    test("loadProducts exposes loading until first package emission") {
        runTest {
            val pendingPackageFlow = MutableSharedFlow<List<PackageItemDomain>>()
            every { getPackageItems() } returns pendingPackageFlow

            vm.loadProducts(cartFlow)
            advanceUntilIdle()

            vm.isLoading.value shouldBe true

            pendingPackageFlow.emit(listOf(packageItem(product = productInfo(id = 1))))
            advanceUntilIdle()

            vm.isLoading.value shouldBe false
            vm.productsFlow.value shouldHaveSize 1
        }
    }

    test("checkProductQuantity caches result when force is false") {
        runTest {
            coEvery { getAvailableQuantity(10) } returns 7

            vm.checkProductQuantity(10)
            vm.checkProductQuantity(10)
            advanceUntilIdle()

            vm.productQuantities.value[10] shouldBe 7
            coVerify(exactly = 1) { getAvailableQuantity(10) }
        }
    }

    test("checkProductQuantity bypasses cache when force=true") {
        runTest {
            coEvery { getAvailableQuantity(10) } returnsMany listOf(7, 3)

            vm.checkProductQuantity(10)
            vm.checkProductQuantity(10, force = true)
            advanceUntilIdle()

            vm.productQuantities.value[10] shouldBe 3
            coVerify(exactly = 2) { getAvailableQuantity(10) }
        }
    }

    test("refreshAllQuantities requests quantity for all loaded products") {
        runTest {
            val p1 = productInfo(id = 1)
            val p2 = productInfo(id = 2)
            packageFlow.value = listOf(packageItem(product = p1), packageItem(product = p2))
            every { getPackageItems() } returns packageFlow
            coEvery { getAvailableQuantity(1) } returns 10
            coEvery { getAvailableQuantity(2) } returns 11

            vm.loadProducts(cartFlow)
            advanceUntilIdle()
            vm.refreshAllQuantities()
            advanceUntilIdle()

            vm.productQuantities.value[1] shouldBe 10
            vm.productQuantities.value[2] shouldBe 11
        }
    }

    test("clearProductState resets flows and cancels current load job") {
        runTest {
            packageFlow.value = listOf(packageItem(product = productInfo(id = 1)))
            vm.loadProducts(cartFlow)
            advanceUntilIdle()

            vm.clearProductState()

            vm.productsFlow.value shouldBe emptyList()
            vm.productQuantities.value shouldBe emptyMap()
            vm.isLoading.value shouldBe false
        }
    }
})
