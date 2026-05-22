package com.chaikasoft.app.ui.viewmodels

import app.cash.turbine.test
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.sealed.RefreshProductsResult
import com.chaikasoft.app.domain.usecases.GetPagedProductsUseCase
import com.chaikasoft.app.domain.usecases.RefreshProductsOnLaunchUseCase
import com.chaikasoft.app.ui.mappers.UiError
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
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
    lateinit var refreshProductsOnLaunchUseCase: RefreshProductsOnLaunchUseCase
    lateinit var vm: ProductViewModel

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        getPagedProductsUseCase = mockk()
        refreshProductsOnLaunchUseCase = mockk()
        coEvery { refreshProductsOnLaunchUseCase() } returns RefreshProductsResult.SkippedFreshCache
        vm = ProductViewModel(getPagedProductsUseCase, refreshProductsOnLaunchUseCase)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    test("attachCart starts product refresh once without product paging") {
        runTest {
            val cartItems = MutableStateFlow<List<CartItemDomain>>(emptyList())

            vm.attachCart(cartItems)
            advanceUntilIdle()

            vm.isSyncing.value shouldBe false
            coVerify(exactly = 1) { refreshProductsOnLaunchUseCase() }
            verify(exactly = 0) { getPagedProductsUseCase(any()) }
        }
    }

    test("attachCart does not emit sync error when cache is fresh") {
        runTest {
            val cartItems = MutableStateFlow<List<CartItemDomain>>(emptyList())

            vm.syncError.test {
                vm.attachCart(cartItems)
                advanceUntilIdle()

                expectNoEvents()
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("attachCart maps remote failure to ui error") {
        runTest {
            val cartItems = MutableStateFlow<List<CartItemDomain>>(emptyList())
            coEvery { refreshProductsOnLaunchUseCase() } returns
                RefreshProductsResult.RemoteFailure(AppError.Network)

            vm.syncError.test {
                vm.attachCart(cartItems)

                awaitItem() shouldBe UiError(
                    messageRes = R.string.error_no_connection,
                    retryable = true
                )
                cancelAndIgnoreRemainingEvents()
            }

            advanceUntilIdle()
            vm.isSyncing.value shouldBe false
        }
    }

    test("attachCart maps local failure to generic ui error") {
        runTest {
            val cartItems = MutableStateFlow<List<CartItemDomain>>(emptyList())
            coEvery { refreshProductsOnLaunchUseCase() } returns
                RefreshProductsResult.LocalFailure(IllegalStateException("db"))

            vm.syncError.test {
                vm.attachCart(cartItems)

                awaitItem() shouldBe UiError(
                    messageRes = R.string.error_try_later,
                    retryable = false
                )
                cancelAndIgnoreRemainingEvents()
            }

            advanceUntilIdle()
            vm.isSyncing.value shouldBe false
        }
    }

    test("repeated attachCart does not start refresh twice") {
        runTest {
            val cartItems = MutableStateFlow<List<CartItemDomain>>(emptyList())

            vm.attachCart(cartItems)
            vm.attachCart(cartItems)
            advanceUntilIdle()

            coVerify(exactly = 1) { refreshProductsOnLaunchUseCase() }
        }
    }
})
