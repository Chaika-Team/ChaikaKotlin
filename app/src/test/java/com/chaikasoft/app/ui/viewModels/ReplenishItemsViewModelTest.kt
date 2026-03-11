package com.chaikasoft.app.ui.viewModels

import app.cash.turbine.test
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.PackageItemDomain
import com.chaikasoft.app.domain.usecases.GetPackageItemUseCase
import com.chaikasoft.app.ui.cartItem
import com.chaikasoft.app.ui.packageItem
import com.chaikasoft.app.ui.productInfo
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ReplenishItemsViewModelTest : FunSpec({

    lateinit var getPackageItems: GetPackageItemUseCase
    lateinit var packageFlow: MutableStateFlow<List<PackageItemDomain>>
    lateinit var cartFlow: MutableStateFlow<List<CartItemDomain>>
    lateinit var vm: ReplenishItemsViewModel

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        getPackageItems = mockk()
        packageFlow = MutableStateFlow(emptyList())
        cartFlow = MutableStateFlow(emptyList())
        every { getPackageItems() } returns packageFlow
        vm = ReplenishItemsViewModel(getPackageItems)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    test("getDisplayProducts merges package list with cart quantities") {
        runTest {
            val p1 = productInfo(id = 1, name = "Tea")
            val p2 = productInfo(id = 2, name = "Coffee")
            packageFlow.value = listOf(packageItem(product = p1), packageItem(product = p2))
            cartFlow.value = listOf(cartItem(product = p1, quantity = 3))

            val display = vm.getDisplayProducts(cartFlow)
            display.test {
                val first = awaitItem()
                val tea = first.first { it.id == 1 }
                val coffee = first.first { it.id == 2 }

                tea.isInCart shouldBe true
                tea.quantity shouldBe 3
                coffee.isInCart shouldBe false
                coffee.quantity shouldBe 0
            }
        }
    }
})

