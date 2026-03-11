package com.chaikasoft.app.domain.usecases.stationUseCases

import androidx.paging.PagingData
import com.chaikasoft.app.data.room.repo.RoomStationRepositoryInterface
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.usecases.GetPagedStationSuggestionsUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf

class GetPagedStationSuggestionsUseCaseTest : FunSpec({

    lateinit var repo: RoomStationRepositoryInterface
    lateinit var useCase: GetPagedStationSuggestionsUseCase

    beforeTest {
        repo = mockk()
        useCase = GetPagedStationSuggestionsUseCase(repo)
    }

    /**
     * Test design technique: #1 Equivalence classes
     *
     * Description:
     * - Input class: any query string with default page size.
     * - Expected behavior: delegates to repo.pagedQuery(query, 20) and returns that flow.
     * - Goal: protect default page size contract.
     */
    test("when invoked with default page size - delegates to repo") {
        val query = "mos"
        val pagingData = PagingData.empty<StationDomain>()
        val expectedFlow = flowOf(pagingData)

        every { repo.pagedQuery(query, 20) } returns expectedFlow

        val result = useCase(query)

        result shouldBe expectedFlow

        verify(exactly = 1) { repo.pagedQuery(query, 20) }
        confirmVerified(repo)
    }
})
