package com.chaikasoft.app.ui.viewmodels

import androidx.paging.PagingData
import app.cash.turbine.test
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.ResolvedTemplateDetailDomain
import com.chaikasoft.app.domain.models.ResolvedTemplateItemDomain
import com.chaikasoft.app.domain.models.TemplateContentDomain
import com.chaikasoft.app.domain.models.TemplateDomain
import com.chaikasoft.app.domain.usecases.GetPagedTemplatesUseCase
import com.chaikasoft.app.domain.usecases.GetResolvedTemplateDetailUseCase
import com.chaikasoft.app.ui.state.TemplateDetailUiState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
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
import kotlinx.coroutines.yield

@OptIn(ExperimentalCoroutinesApi::class)
class TemplateViewModelTest : FunSpec({

    lateinit var getPagedTemplatesUseCase: GetPagedTemplatesUseCase
    lateinit var getResolvedTemplateDetailUseCase: GetResolvedTemplateDetailUseCase
    lateinit var vm: TemplateViewModel

    val template = TemplateDomain(
        id = 42,
        templateName = "Tea Set",
        description = "Template description",
        content = listOf(TemplateContentDomain(productId = 101, quantity = 2))
    )
    val resolvedDetail = ResolvedTemplateDetailDomain(
        template = template,
        items = listOf(
            ResolvedTemplateItemDomain(productId = 101, quantity = 2, product = null)
        )
    )

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        getPagedTemplatesUseCase = mockk()
        getResolvedTemplateDetailUseCase = mockk()

        every { getPagedTemplatesUseCase(any(), any()) } returns flowOf(PagingData.empty())

        vm = TemplateViewModel(
            getPagedTemplatesUseCase = getPagedTemplatesUseCase,
            getResolvedTemplateDetailUseCase = getResolvedTemplateDetailUseCase
        )
    }

    afterTest {
        Dispatchers.resetMain()
    }

    test("loadTemplateDetail emits Loading then Content on success") {
        runTest {
            coEvery { getResolvedTemplateDetailUseCase(42) } coAnswers {
                yield()
                RemoteResult.Success(resolvedDetail)
            }

            vm.templateDetailState.test {
                awaitItem() shouldBe TemplateDetailUiState.Idle
                vm.loadTemplateDetail(42)
                awaitItem() shouldBe TemplateDetailUiState.Loading
                awaitItem() shouldBe TemplateDetailUiState.Content(resolvedDetail)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("loadTemplateDetail emits Error on remote failure") {
        runTest {
            coEvery { getResolvedTemplateDetailUseCase(42) } coAnswers {
                yield()
                RemoteResult.Failure(AppError.Network())
            }

            vm.templateDetailState.test {
                awaitItem() shouldBe TemplateDetailUiState.Idle
                vm.loadTemplateDetail(42)
                awaitItem() shouldBe TemplateDetailUiState.Loading
                awaitItem() shouldBe TemplateDetailUiState.Error(
                    messageRes = R.string.error_no_connection,
                    retryable = true
                )
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("retryLoadTemplateDetail retries last failed request") {
        runTest {
            var callCount = 0
            coEvery { getResolvedTemplateDetailUseCase(42) } coAnswers {
                callCount++
                yield()
                if (callCount == 1) {
                    RemoteResult.Failure(AppError.Network())
                } else {
                    RemoteResult.Success(resolvedDetail)
                }
            }

            vm.templateDetailState.test {
                awaitItem() shouldBe TemplateDetailUiState.Idle
                vm.loadTemplateDetail(42)
                awaitItem() shouldBe TemplateDetailUiState.Loading
                (awaitItem() is TemplateDetailUiState.Error) shouldBe true

                vm.retryLoadTemplateDetail()
                awaitItem() shouldBe TemplateDetailUiState.Loading
                awaitItem() shouldBe TemplateDetailUiState.Content(resolvedDetail)
                cancelAndIgnoreRemainingEvents()
            }
            coVerify(exactly = 2) { getResolvedTemplateDetailUseCase(42) }
        }
    }

    test("loadTemplateDetail does not reload same template id when already loaded") {
        runTest {
            coEvery { getResolvedTemplateDetailUseCase(42) } returns
                RemoteResult.Success(resolvedDetail)

            vm.loadTemplateDetail(42)
            advanceUntilIdle()
            vm.loadTemplateDetail(42)
            advanceUntilIdle()

            coVerify(exactly = 1) { getResolvedTemplateDetailUseCase(42) }
        }
    }
})
