package com.chaikasoft.app.domain.usecases.productInfoUseCases

import com.chaikasoft.app.data.room.repo.RoomProductInfoRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomSyncMetaRepositoryInterface
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.sealed.RefreshProductsResult
import com.chaikasoft.app.domain.usecases.FetchProductsFromServerUseCase
import com.chaikasoft.app.domain.usecases.HasActiveShiftUseCase
import com.chaikasoft.app.domain.usecases.RefreshProductsOnLaunchUseCase
import com.chaikasoft.app.domain.usecases.SaveProductsLocallyUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class RefreshProductsOnLaunchUseCaseTest : FunSpec({

    lateinit var fetchProductsFromServerUseCase: FetchProductsFromServerUseCase
    lateinit var productRepository: RoomProductInfoRepositoryInterface
    lateinit var syncMetaRepository: RoomSyncMetaRepositoryInterface
    lateinit var saveProductsLocallyUseCase: SaveProductsLocallyUseCase
    lateinit var hasActiveShift: HasActiveShiftUseCase
    lateinit var useCase: RefreshProductsOnLaunchUseCase

    val product = ProductInfoDomain(
        id = 1,
        name = "Tea",
        description = "Black tea",
        image = "  ",
        price = 100,
    )
    val productForStorage = product.copy(image = "")

    beforeTest {
        fetchProductsFromServerUseCase = mockk()
        productRepository = mockk()
        syncMetaRepository = mockk()
        saveProductsLocallyUseCase = mockk()
        hasActiveShift = mockk()
        coEvery { hasActiveShift() } returns false

        useCase = RefreshProductsOnLaunchUseCase(
            fetchProductsFromServerUseCase = fetchProductsFromServerUseCase,
            productInfoRepository = productRepository,
            syncMetaRepo = syncMetaRepository,
            saveProductsLocallyUseCase = saveProductsLocallyUseCase,
            hasActiveShift = hasActiveShift,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    test("when active shift exists - returns SkippedActiveShift") {
        runTest {
            coEvery { hasActiveShift() } returns true

            val result = useCase()

            result shouldBe RefreshProductsResult.SkippedActiveShift
            coVerify(exactly = 1) { hasActiveShift() }
            coVerify(exactly = 0) { productRepository.hasAnyProductsOnce() }
            coVerify(exactly = 0) { syncMetaRepository.getLastSuccessfulSyncAt(any()) }
            coVerify(exactly = 0) { fetchProductsFromServerUseCase(any(), any()) }
            coVerify(exactly = 0) { saveProductsLocallyUseCase(any()) }
            coVerify(exactly = 0) { syncMetaRepository.setLastSuccessfulSyncAt(any(), any()) }
        }
    }

    test("when local products are empty - refreshes and stores sync timestamp") {
        runTest {
            coEvery { productRepository.hasAnyProductsOnce() } returns false
            coEvery { fetchProductsFromServerUseCase(100, 0) } returns
                RemoteResult.Success(listOf(product))
            coEvery { saveProductsLocallyUseCase(listOf(product)) } returns listOf(productForStorage)
            coEvery { syncMetaRepository.setLastSuccessfulSyncAt(any(), any()) } returns Unit

            val result = useCase()

            result shouldBe RefreshProductsResult.Success(productCount = 1)
            coVerify(exactly = 1) { productRepository.hasAnyProductsOnce() }
            coVerify(exactly = 0) { syncMetaRepository.getLastSuccessfulSyncAt(any()) }
            coVerify(exactly = 1) { fetchProductsFromServerUseCase(100, 0) }
            coVerify(exactly = 1) { saveProductsLocallyUseCase(listOf(product)) }
            coVerify(exactly = 1) {
                syncMetaRepository.setLastSuccessfulSyncAt("products", any())
            }
        }
    }

    test("when local products exist and ttl is not expired - returns SkippedFreshCache") {
        runTest {
            val now = System.currentTimeMillis()
            coEvery { productRepository.hasAnyProductsOnce() } returns true
            coEvery { syncMetaRepository.getLastSuccessfulSyncAt("products") } returns now

            val result = useCase()

            result shouldBe RefreshProductsResult.SkippedFreshCache
            coVerify(exactly = 1) { syncMetaRepository.getLastSuccessfulSyncAt("products") }
            coVerify(exactly = 0) { fetchProductsFromServerUseCase(any(), any()) }
            coVerify(exactly = 0) { saveProductsLocallyUseCase(any()) }
            coVerify(exactly = 0) { syncMetaRepository.setLastSuccessfulSyncAt(any(), any()) }
        }
    }

    test("when ttl is expired - refreshes and writes changed products") {
        runTest {
            val expired = System.currentTimeMillis() - (8L * 24L * 60L * 60L * 1000L)
            coEvery { productRepository.hasAnyProductsOnce() } returns true
            coEvery { syncMetaRepository.getLastSuccessfulSyncAt("products") } returns expired
            coEvery { fetchProductsFromServerUseCase(100, 0) } returns
                RemoteResult.Success(listOf(product))
            coEvery { saveProductsLocallyUseCase(listOf(product)) } returns listOf(productForStorage)
            coEvery { syncMetaRepository.setLastSuccessfulSyncAt(any(), any()) } returns Unit

            val result = useCase()

            result shouldBe RefreshProductsResult.Success(productCount = 1)
            coVerify(exactly = 1) { fetchProductsFromServerUseCase(100, 0) }
            coVerify(exactly = 1) { saveProductsLocallyUseCase(listOf(product)) }
            coVerify(exactly = 1) {
                syncMetaRepository.setLastSuccessfulSyncAt("products", any())
            }
        }
    }

    test("when remote fetch fails - returns RemoteFailure and does not update sync timestamp") {
        runTest {
            coEvery { productRepository.hasAnyProductsOnce() } returns false
            coEvery { fetchProductsFromServerUseCase(100, 0) } returns
                RemoteResult.Failure(AppError.Network())

            val result = useCase()

            result shouldBe RefreshProductsResult.RemoteFailure(AppError.Network())
            coVerify(exactly = 1) { fetchProductsFromServerUseCase(100, 0) }
            coVerify(exactly = 0) { saveProductsLocallyUseCase(any()) }
            coVerify(exactly = 0) { syncMetaRepository.setLastSuccessfulSyncAt(any(), any()) }
        }
    }

    test("when local upsert fails - returns LocalFailure and does not update sync timestamp") {
        runTest {
            val error = IllegalStateException("db failure")
            coEvery { productRepository.hasAnyProductsOnce() } returns false
            coEvery { fetchProductsFromServerUseCase(100, 0) } returns
                RemoteResult.Success(listOf(product))
            coEvery { saveProductsLocallyUseCase(listOf(product)) } throws error

            val result = useCase()

            result shouldBe RefreshProductsResult.LocalFailure(error)
            coVerify(exactly = 1) { saveProductsLocallyUseCase(listOf(product)) }
            coVerify(exactly = 0) { syncMetaRepository.setLastSuccessfulSyncAt(any(), any()) }
        }
    }

    test("when local save is cancelled - rethrows CancellationException") {
        runTest {
            val error = CancellationException("cancelled")
            coEvery { productRepository.hasAnyProductsOnce() } returns false
            coEvery { fetchProductsFromServerUseCase(100, 0) } returns
                RemoteResult.Success(listOf(product))
            coEvery { saveProductsLocallyUseCase(listOf(product)) } throws error

            shouldThrow<CancellationException> { useCase() }

            coVerify(exactly = 1) { saveProductsLocallyUseCase(listOf(product)) }
            coVerify(exactly = 0) { syncMetaRepository.setLastSuccessfulSyncAt(any(), any()) }
        }
    }

    test("when local save succeeds without changes - updates timestamp") {
        runTest {
            val remoteProduct = product.copy(image = "https://example.test/tea.jpg")
            val expired = System.currentTimeMillis() - (8L * 24L * 60L * 60L * 1000L)

            coEvery { productRepository.hasAnyProductsOnce() } returns true
            coEvery { syncMetaRepository.getLastSuccessfulSyncAt("products") } returns expired
            coEvery { fetchProductsFromServerUseCase(100, 0) } returns
                RemoteResult.Success(listOf(remoteProduct))
            coEvery { saveProductsLocallyUseCase(listOf(remoteProduct)) } returns listOf(remoteProduct)
            coEvery { syncMetaRepository.setLastSuccessfulSyncAt(any(), any()) } returns Unit

            val result = useCase()

            result shouldBe RefreshProductsResult.Success(productCount = 1)
            coVerify(exactly = 1) { saveProductsLocallyUseCase(listOf(remoteProduct)) }
            coVerify(exactly = 1) {
                syncMetaRepository.setLastSuccessfulSyncAt("products", any())
            }
        }
    }
})
