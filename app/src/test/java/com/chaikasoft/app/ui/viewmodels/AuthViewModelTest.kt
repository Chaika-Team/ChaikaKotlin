package com.chaikasoft.app.ui.viewmodels

import android.content.Intent
import com.chaikasoft.app.auth.NoOpAuthSessionBootstrap
import com.chaikasoft.app.domain.sealed.LogoutResult
import com.chaikasoft.app.domain.usecases.CompleteAuthorizationFlowUseCase
import com.chaikasoft.app.domain.usecases.GetAccessTokenUseCase
import com.chaikasoft.app.domain.usecases.LogoutUseCase
import com.chaikasoft.app.domain.usecases.StartAuthorizationUseCase
import com.chaikasoft.app.ui.conductor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest : FunSpec({

    lateinit var getAccessToken: GetAccessTokenUseCase
    lateinit var completeAuth: CompleteAuthorizationFlowUseCase
    lateinit var logout: LogoutUseCase
    lateinit var startAuth: StartAuthorizationUseCase

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        getAccessToken = mockk()
        completeAuth = mockk()
        logout = mockk()
        startAuth = mockk()
        coEvery { getAccessToken() } returns null
        coEvery { logout() } returns LogoutResult.Success
    }

    afterTest {
        Dispatchers.resetMain()
    }

    fun createVm(): AuthViewModel = AuthViewModel(
        getAccessTokenUseCase = getAccessToken,
        completeAuthorizationFlowUseCase = completeAuth,
        logoutUseCase = logout,
        startAuthorizationUseCase = startAuth,
        authSessionBootstrap = NoOpAuthSessionBootstrap()
    )

    test("init sets Authenticated when token exists") {
        runTest {
            coEvery { getAccessToken() } returns "token"

            val vm = createVm()
            advanceUntilIdle()

            vm.uiState.value.state shouldBe AuthState.Authenticated
        }
    }

    test("startAuth clears previous error and returns intent") {
        runTest {
            val vm = createVm()
            val intent = mockk<Intent>(relaxed = true)
            every { startAuth() } returns intent

            val result = vm.startAuth()

            result shouldBe intent
            vm.uiState.value.errorMessage shouldBe null
        }
    }

    test("handleAuthResult success with conductor id sets Authenticated") {
        runTest {
            val vm = createVm()
            val intent = mockk<Intent>(relaxed = true)
            coEvery { completeAuth(intent) } returns ("token" to conductor(id = 7))

            vm.handleAuthResult(intent)
            advanceUntilIdle()

            vm.uiState.value.state shouldBe AuthState.Authenticated
            vm.uiState.value.errorMessage shouldBe null
        }
    }

    test("handleAuthResult failure sets Unauthenticated and performs cleanup logout") {
        runTest {
            val vm = createVm()
            val intent = mockk<Intent>(relaxed = true)
            coEvery { completeAuth(intent) } throws IllegalStateException("auth failed")
            coEvery { logout() } returns LogoutResult.Success

            vm.handleAuthResult(intent)
            advanceUntilIdle()

            vm.uiState.value.state shouldBe AuthState.Unauthenticated
            vm.uiState.value.errorMessage shouldBe "auth failed"
            coVerify(exactly = 1) { logout() }
        }
    }

    test("logout maps domain result to dialogs") {
        runTest {
            val vm = createVm()

            coEvery { logout() } returns LogoutResult.ActiveShiftExists
            vm.logout()
            advanceUntilIdle()
            vm.uiState.value.showActiveShiftDialog shouldBe true

            coEvery { logout() } returns LogoutResult.Failure("boom")
            vm.logout()
            advanceUntilIdle()
            vm.uiState.value.showLogoutErrorDialog shouldBe true
            vm.uiState.value.logoutErrorMessage shouldBe "boom"

            coEvery { logout() } returns LogoutResult.Success
            vm.logout()
            advanceUntilIdle()
            vm.uiState.value.state shouldBe AuthState.Unauthenticated
            vm.uiState.value.showLogoutErrorDialog shouldBe false
            vm.uiState.value.showActiveShiftDialog shouldBe false
        }
    }
})
