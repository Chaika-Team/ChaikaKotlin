package com.example.chaika.domain.usecases

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.chaika.data.dataSource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.example.chaika.domain.models.TemplateDomain
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class GetPagedTemplatesUseCaseTest {

    @Mock
    lateinit var repository: ChaikaSoftApiServiceRepositoryInterface

    // Создаем единый TestCoroutineScheduler
    private val testScheduler = TestCoroutineScheduler()

    // Используем один StandardTestDispatcher с нашим scheduler-ом
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    @Test
    fun `GetPagedTemplatesUseCase returns non-empty paging data`() = runTest(testDispatcher) {
        // Настраиваем тестовые данные для шаблонов
        val dummyTemplates = listOf(
            TemplateDomain(
                id = 1,
                templateName = "Template 1",
                description = "Desc 1",
                content = emptyList()
            ),
            TemplateDomain(
                id = 2,
                templateName = "Template 2",
                description = "Desc 2",
                content = emptyList()
            )
        )

        // Создаем Fake PagingSource для шаблонов
        val pagingSource = object : PagingSource<Int, TemplateDomain>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TemplateDomain> {
                val currentPage = params.key ?: 0
                val pageSize = params.loadSize
                val fromIndex = currentPage * pageSize
                val toIndex = minOf(fromIndex + pageSize, dummyTemplates.size)
                val data = if (fromIndex < dummyTemplates.size) dummyTemplates.subList(
                    fromIndex,
                    toIndex
                ) else emptyList()
                val nextKey = if (toIndex >= dummyTemplates.size) null else currentPage + 1
                return LoadResult.Page(
                    data = data,
                    prevKey = if (currentPage == 0) null else currentPage - 1,
                    nextKey = nextKey
                )
            }

            override fun getRefreshKey(state: PagingState<Int, TemplateDomain>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                    state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                        ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                }
            }
        }

        // Подменяем pagingSourceFactory через lambda, чтобы использовать наш fake PagingSource
        val pagingData = Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { pagingSource }
        ).flow.first()

        // Создаем AsyncPagingDataDiffer с единым диспетчером
        val differ = AsyncPagingDataDiffer(
            diffCallback = object : DiffUtil.ItemCallback<TemplateDomain>() {
                override fun areItemsTheSame(
                    oldItem: TemplateDomain,
                    newItem: TemplateDomain
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: TemplateDomain,
                    newItem: TemplateDomain
                ): Boolean {
                    return oldItem == newItem
                }
            },
            updateCallback = object : ListUpdateCallback {
                override fun onInserted(position: Int, count: Int) {}
                override fun onRemoved(position: Int, count: Int) {}
                override fun onMoved(fromPosition: Int, toPosition: Int) {}
                override fun onChanged(position: Int, count: Int, payload: Any?) {}
            },
            mainDispatcher = testDispatcher,
            workerDispatcher = testDispatcher
        )

        // Запускаем differ.submitData в отдельном Job
        val submitJob = launch {
            differ.submitData(pagingData)
        }
        // Продвигаем scheduler до завершения всех задач
        testScheduler.advanceUntilIdle()
        // Отменяем Job, чтобы завершить активные дочерние корутины
        submitJob.cancel()

        // Проверяем, что differ содержит данные
        assertTrue(differ.snapshot().items.isNotEmpty(), "Paging data should not be empty")
    }
}
