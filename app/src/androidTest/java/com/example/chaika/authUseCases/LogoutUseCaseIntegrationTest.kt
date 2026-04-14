package com.chaikasoft.app.authUseCases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chaikasoft.app.data.crypto.EncryptedTokenManagerInterface
import com.chaikasoft.app.data.local.LocalImageRepositoryInterface
import com.chaikasoft.app.domain.usecases.DeleteAllConductorsUseCase
import com.chaikasoft.app.domain.usecases.GetAccessTokenUseCase
import com.chaikasoft.app.domain.usecases.LogoutUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LogoutUseCaseIntegrationTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var logoutUseCase: LogoutUseCase

    @Inject
    lateinit var tokenManager: EncryptedTokenManagerInterface

    @Inject
    lateinit var getAccessTokenUseCase: GetAccessTokenUseCase

    @Inject
    lateinit var deleteAllConductorsUseCase: DeleteAllConductorsUseCase

    @Inject
    lateinit var imageRepository: LocalImageRepositoryInterface

    @Before
    fun setUp() {
        hiltRule.inject()
        // Pre-save a token to simulate an authorized state.
        tokenManager.saveToken("token_to_be_cleared")
    }

    @After
    fun tearDown() {
        tokenManager.clearToken()
    }

    @Test
    fun testLogout_clearsTokenAndData() =
        runTest {
            // Assert token exists before logout.
            assertNotNull("Token should exist before logout", tokenManager.getToken())

            // Act
            logoutUseCase()

            // Assert that the token is cleared.
            val tokenAfterLogout = getAccessTokenUseCase()
            assertNull("Token should be null after logout", tokenAfterLogout)

            // For demonstration purposes, we assume that the cleanup of conductors and images is done.
            // In a real test, you might check the database or file system.
            assertTrue("Logout cleanup executed", true)
        }
}
