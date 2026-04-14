package com.chaikasoft.app.ui.viewmodels

import app.cash.turbine.test
import com.chaikasoft.app.domain.usecases.HasAnyPackageItemsOnceUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ProductGateViewModelTest : FunSpec({

    lateinit var hasAnyOnce: HasAnyPackageItemsOnceUseCase
    lateinit var vm: ProductGateViewModel

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        hasAnyOnce = mockk()
        vm = ProductGateViewModel(hasAnyOnce)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    test("resolveTarget emits Resolved(PACKAGE) when package exists") {
        runTest {
            coEvery { hasAnyOnce() } returns true

            vm.uiState.test {
                awaitItem() shouldBe ProductGateViewModel.ProductGateUiState.Loading
                vm.resolveTarget()
                awaitItem() shouldBe ProductGateViewModel.ProductGateUiState.Resolved(
                    ProductGateViewModel.Target.PACKAGE
                )
                cancelAndIgnoreRemainingEvents()
            }

            coVerify(exactly = 1) { hasAnyOnce() }
        }
    }

    test("resolveTarget emits Resolved(ENTRY) when package is empty") {
        runTest {
            coEvery { hasAnyOnce() } returns false

            vm.uiState.test {
                awaitItem() shouldBe ProductGateViewModel.ProductGateUiState.Loading
                vm.resolveTarget()
                awaitItem() shouldBe ProductGateViewModel.ProductGateUiState.Resolved(
                    ProductGateViewModel.Target.ENTRY
                )
                cancelAndIgnoreRemainingEvents()
            }

            coVerify(exactly = 1) { hasAnyOnce() }
        }
    }

    test("resolveTarget falls back to ENTRY when use case fails") {
        runTest {
            coEvery { hasAnyOnce() } throws IllegalStateException("db failure")

            vm.uiState.test {
                awaitItem() shouldBe ProductGateViewModel.ProductGateUiState.Loading
                vm.resolveTarget()
                awaitItem() shouldBe ProductGateViewModel.ProductGateUiState.Resolved(
                    ProductGateViewModel.Target.ENTRY
                )
                cancelAndIgnoreRemainingEvents()
            }

            coVerify(exactly = 1) { hasAnyOnce() }
        }
    }
})

