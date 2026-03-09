package com.chaikasoft.app.data.room.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.chaikasoft.app.data.room.dao.StationDao
import com.chaikasoft.app.data.room.entities.Station
import com.chaikasoft.app.domain.models.trip.StationDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class RoomStationRepositoryTest : FunSpec({

    lateinit var dao: StationDao
    lateinit var repository: RoomStationRepository

    beforeTest {
        dao = mockk()
        repository = RoomStationRepository(dao)
    }

    /**
     * Test design: boundary values.
     * Blank query must short-circuit to empty paging and not touch DAO.
     */
    test("pagedQuery returns empty stream for blank query without dao call") {
        runTest {
            repository.pagedQuery("   ", pageSize = 20).first()

            verify(exactly = 0) { dao.pagingByQuery(any()) }
        }
    }

    /**
     * Test design: boundary values.
     * LIKE escaping of %, _ and backslash protects query semantics in SQL.
     */
    test("pagedQuery escapes special LIKE symbols before calling dao") {
        runTest {
            every { dao.pagingByQuery(any()) } returns EmptyStationPagingSource()

            repository.pagedQuery("""ab%_c\z""", pageSize = 20).first()

            verify(exactly = 1) { dao.pagingByQuery("""ab\%\_c\\z%""") }
        }
    }

    /**
     * Test design: equivalence classes.
     * getByCode should map DAO entity to domain model.
     */
    test("getByCode maps station to domain") {
        runTest {
            coEvery { dao.getByCode("MSK") } returns Station("MSK", "Moscow", "Moscow")

            repository.getByCode("MSK") shouldBe StationDomain("MSK", "Moscow", "Moscow")
        }
    }
}) {
    private class EmptyStationPagingSource : PagingSource<Int, Station>() {
        override fun getRefreshKey(state: PagingState<Int, Station>): Int? = null

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Station> =
            LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null,
            )
    }
}

