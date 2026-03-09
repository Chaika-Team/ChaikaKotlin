package com.chaikasoft.app.ui.viewModels

import app.cash.turbine.test
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.usecases.GetFastReportDataUseCase
import com.chaikasoft.app.domain.usecases.GetOperationCountByTypeUseCase
import com.chaikasoft.app.ui.report
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
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
class StatisticsViewModelTest : FunSpec({

    lateinit var getFastReportData: GetFastReportDataUseCase
    lateinit var getOperationCountByType: GetOperationCountByTypeUseCase
    lateinit var reportsFlow: MutableStateFlow<List<com.chaikasoft.app.domain.models.FastReportDomain>>
    lateinit var vm: StatisticsViewModel

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        getFastReportData = mockk()
        getOperationCountByType = mockk()
        reportsFlow = MutableStateFlow(emptyList())
        every { getFastReportData() } returns reportsFlow
        coEvery { getOperationCountByType(any()) } returns 0

        vm = StatisticsViewModel(getFastReportData, getOperationCountByType)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    test("cashRevenue sums productPrice * soldCashQuantity") {
        runTest {
            vm.cashRevenue.test {
                awaitItem() shouldBe 0
                reportsFlow.value = listOf(
                    report(price = 100, soldCash = 2),
                    report(price = 50, soldCash = 3)
                )
                awaitItem() shouldBe 350
            }
        }
    }

    test("refreshCartChecks updates cashChecksCount using SOLD_CART type") {
        runTest {
            coEvery { getOperationCountByType(OperationTypeDomain.SOLD_CART) } returns 12

            vm.refreshCartChecks()
            advanceUntilIdle()

            vm.cashChecksCount.value shouldBe 12
            coVerify(exactly = 1) { getOperationCountByType(OperationTypeDomain.SOLD_CART) }
        }
    }
})
