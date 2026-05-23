package com.chaikasoft.app.ui.viewmodels

import com.chaikasoft.app.startup.PostAuthStartupOutcome
import com.chaikasoft.app.startup.PostAuthStartupSeam
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class PostAuthGateViewModelTest : FunSpec({

    lateinit var postAuthStartupSeam: PostAuthStartupSeam

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        postAuthStartupSeam = mockk()
    }

    afterTest {
        Dispatchers.resetMain()
    }

    fun createVm(): PostAuthGateViewModel = PostAuthGateViewModel(
        postAuthStartupSeam = postAuthStartupSeam
    )

    test("prepare marks ready without failure when startup succeeds") {
        runTest {
            coEvery { postAuthStartupSeam.prepareForAuthenticatedApp() } returns
                PostAuthStartupOutcome(hadRefreshFailure = false)
            val vm = createVm()

            vm.prepare()
            advanceUntilIdle()

            vm.uiState.value shouldBe
                PostAuthGateViewModel.PostAuthGateUiState.Ready(hadRefreshFailure = false)
            coVerify(exactly = 1) { postAuthStartupSeam.prepareForAuthenticatedApp() }
        }
    }

    test("prepare marks ready with failure when startup returns failure") {
        runTest {
            coEvery { postAuthStartupSeam.prepareForAuthenticatedApp() } returns
                PostAuthStartupOutcome(hadRefreshFailure = true)
            val vm = createVm()

            vm.prepare()
            advanceUntilIdle()

            vm.uiState.value shouldBe
                PostAuthGateViewModel.PostAuthGateUiState.Ready(hadRefreshFailure = true)
            coVerify(exactly = 1) { postAuthStartupSeam.prepareForAuthenticatedApp() }
        }
    }

    test("prepare is idempotent and does not launch startup twice") {
        runTest {
            coEvery { postAuthStartupSeam.prepareForAuthenticatedApp() } returns
                PostAuthStartupOutcome(hadRefreshFailure = false)
            val vm = createVm()

            vm.prepare()
            vm.prepare()
            advanceUntilIdle()

            vm.uiState.value shouldBe
                PostAuthGateViewModel.PostAuthGateUiState.Ready(hadRefreshFailure = false)
            coVerify(exactly = 1) { postAuthStartupSeam.prepareForAuthenticatedApp() }
        }
    }
})
