package com.chaikasoft.app.ui.viewModels

import com.chaikasoft.app.domain.usecases.HasAnyPackageItemsOnceUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class ProductGateViewModelTest : FunSpec({

    lateinit var hasAnyOnce: HasAnyPackageItemsOnceUseCase
    lateinit var vm: ProductGateViewModel

    beforeTest {
        hasAnyOnce = mockk()
        vm = ProductGateViewModel(hasAnyOnce)
    }

    test("decide returns PACKAGE when package exists") {
        runTest {
            coEvery { hasAnyOnce() } returns true

            vm.decide() shouldBe ProductGateViewModel.Target.PACKAGE
        }
    }

    test("decide returns ENTRY when package is empty") {
        runTest {
            coEvery { hasAnyOnce() } returns false

            vm.decide() shouldBe ProductGateViewModel.Target.ENTRY
        }
    }
})

