package com.example.chaika.domain.usecases

import com.example.chaika.data.dataSource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.example.chaika.domain.models.TemplateDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case для получения списка шаблонов с сервера.
 *
 * @param repository Репозиторий, реализующий работу с ChaikaSoft API.
 */
class FetchTemplatesUseCase @Inject constructor(
    private val repository: ChaikaSoftApiServiceRepositoryInterface
) {
    suspend operator fun invoke(limit: Int = 100, offset: Int = 0): List<TemplateDomain> =
        withContext(Dispatchers.IO) {
            repository.fetchTemplates(limit, offset)
        }
}
