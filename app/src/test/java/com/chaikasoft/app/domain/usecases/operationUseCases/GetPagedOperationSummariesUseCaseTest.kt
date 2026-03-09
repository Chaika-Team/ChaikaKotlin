package com.chaikasoft.app.domain.usecases.operationUseCases

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.chaikasoft.app.data.room.repo.RoomCartOperationRepositoryInterface
import com.chaikasoft.app.domain.models.OperationSummaryDomain
import com.chaikasoft.app.domain.usecases.GetPagedOperationSummariesUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf

class GetPagedOperationSummariesUseCaseTest : FunSpec({

    lateinit var repo: RoomCartOperationRepositoryInterface
    lateinit var useCase: GetPagedOperationSummariesUseCase

    beforeTest {
        repo = mockk()
        useCase = GetPagedOperationSummariesUseCase(repo)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Codex
     *
     * Описание:
     *   - Входы: pageSize 1, 20, 50 с разными флагами placeholders.
     *   - Ожидаемое поведение: PagingConfig содержит переданные pageSize и enablePlaceholders.
     *   - Цель: поймать случайное захардкоживание параметров пагинации.
     */
    test("when called with varying page sizes - passes paging config to repo") {
        val expectedFlow = flowOf(PagingData.empty<OperationSummaryDomain>())
        val configSlot = slot<PagingConfig>()
        val cases = listOf(
            1 to false,
            20 to true,
            50 to false,
        )

        every { repo.getPagedOperationSummaries(capture(configSlot)) } returns expectedFlow

        cases.forEach { (pageSize, enablePlaceholders) ->
            val result = useCase(pageSize, enablePlaceholders)

            result shouldBe expectedFlow
            configSlot.captured.pageSize shouldBe pageSize
            configSlot.captured.enablePlaceholders shouldBe enablePlaceholders
        }

        verify(exactly = cases.size) { repo.getPagedOperationSummaries(any()) }
        confirmVerified(repo)
    }
})
