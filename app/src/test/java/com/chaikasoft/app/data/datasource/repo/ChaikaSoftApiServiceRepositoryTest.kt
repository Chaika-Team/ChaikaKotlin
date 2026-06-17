package com.chaikasoft.app.data.datasource.repo

import com.chaikasoft.app.data.datasource.apiservice.ChaikaSoftApiService
import com.chaikasoft.app.data.datasource.dto.ProductInfoDto
import com.chaikasoft.app.data.datasource.dto.ProductInfoListResponseDto
import com.chaikasoft.app.data.datasource.dto.TemplateContentDto
import com.chaikasoft.app.data.datasource.dto.TemplateDetailResponseDto
import com.chaikasoft.app.data.datasource.dto.TemplateDto
import com.chaikasoft.app.data.datasource.dto.TemplateListResponseDto
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.RemoteResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.test.runTest

class ChaikaSoftApiServiceRepositoryTest : FunSpec({

    lateinit var api: ChaikaSoftApiService
    lateinit var repository: ChaikaSoftApiServiceRepository

    beforeTest {
        api = mockk()
        repository = ChaikaSoftApiServiceRepository(api)
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Описание:
     *   - Комбинация: успешный ответ getProducts(limit, offset).
     *   - Ожидаемое поведение: параметры paging передаются без искажений, dto маппится в domain.
     *   - Цель: защитить контракт пагинации и маппинга продуктов.
     */
    test("fetchProducts passes paging params and maps dto list to domain") {
        runTest {
            coEvery { api.getProducts(limit = 50, offset = 100) } returns
                ProductInfoListResponseDto(
                    products = listOf(
                        ProductInfoDto(
                            id = 1,
                            name = "Tea",
                            description = "Black tea",
                            image = "from-api",
                            price = 12.30,
                        ),
                    ),
                )

            val result = repository.fetchProducts(limit = 50, offset = 100)

            result as RemoteResult.Success
            result.data.size shouldBe 1
            result.data.first().id shouldBe 1
            result.data.first().price shouldBe 1230
            result.data.first().image shouldBe "from-api"
            coVerify(exactly = 1) { api.getProducts(limit = 50, offset = 100) }
        }
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Описание:
     *   - Вход: успешный ответ products, где DTO продукта содержит image == null.
     *   - Ожидаемое поведение: repository возвращает продукт с пустым image.
     *   - Цель: закрепить контракт отсутствующего изображения на границе API repository.
     */
    test("fetchProducts maps null image to empty string") {
        runTest {
            coEvery { api.getProducts(limit = 10, offset = 0) } returns
                ProductInfoListResponseDto(
                    products = listOf(
                        ProductInfoDto(
                            id = 1,
                            name = "Tea",
                            description = "Black tea",
                            image = null,
                            price = 10.0,
                        )
                    )
                )

            val result = repository.fetchProducts(limit = 10, offset = 0)

            result as RemoteResult.Success
            result.data.first().image shouldBe ""
            coVerify(exactly = 1) { api.getProducts(limit = 10, offset = 0) }
        }
    }

    test("fetchProducts network error returns RemoteResult.Failure(Network)") {
        runTest {
            coEvery { api.getProducts(limit = 10, offset = 0) } throws IOException("network")

            val result = repository.fetchProducts(limit = 10, offset = 0)

            result shouldBe RemoteResult.Failure(AppError.Network)
            coVerify(exactly = 1) { api.getProducts(limit = 10, offset = 0) }
        }
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Описание:
     *   - Комбинация: успешный ответ getTemplates(query, limit, offset).
     *   - Ожидаемое поведение: параметры запроса сохраняются, список шаблонов маппится корректно.
     *   - Цель: зафиксировать регрессионно-опасные query/paging параметры.
     */
    test("fetchTemplates passes query and paging params and maps list") {
        runTest {
            coEvery { api.getTemplates(query = "cof", limit = 20, offset = 40) } returns
                TemplateListResponseDto(
                    templates = listOf(
                        TemplateDto(
                            id = 5,
                            templateName = "Coffee set",
                            description = "desc",
                            content = listOf(TemplateContentDto(productId = 10, quantity = 2)),
                        ),
                    ),
                )

            val result = repository.fetchTemplates(query = "cof", limit = 20, offset = 40)

            result as RemoteResult.Success
            result.data.size shouldBe 1
            result.data.first().id shouldBe 5
            result.data.first().content.first().productId shouldBe 10
            coVerify(exactly = 1) { api.getTemplates(query = "cof", limit = 20, offset = 40) }
        }
    }

    test("fetchTemplates network error returns RemoteResult.Failure(Network)") {
        runTest {
            coEvery { api.getTemplates(query = "cof", limit = 20, offset = 40) } throws
                IOException("network")

            val result = repository.fetchTemplates(query = "cof", limit = 20, offset = 40)

            result shouldBe RemoteResult.Failure(AppError.Network)
            coVerify(exactly = 1) { api.getTemplates(query = "cof", limit = 20, offset = 40) }
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Эквивалентный класс: успешная загрузка детального шаблона по id.
     *   - Ожидаемое поведение: id передается корректно, detail DTO маппится в TemplateDomain.
     *   - Цель: зафиксировать контракт метода fetchTemplateDetail.
     */
    test("fetchTemplateDetail passes template id and maps detail") {
        runTest {
            coEvery { api.getTemplateDetail(templateId = 777) } returns
                TemplateDetailResponseDto(
                    template = TemplateDto(
                        id = 777,
                        templateName = "Detail",
                        description = "desc",
                        content = listOf(TemplateContentDto(productId = 1, quantity = 1)),
                    ),
                )

            val result = repository.fetchTemplateDetail(templateId = 777)

            result as RemoteResult.Success
            result.data.id shouldBe 777
            result.data.templateName shouldBe "Detail"
            coVerify(exactly = 1) { api.getTemplateDetail(templateId = 777) }
        }
    }

    test("fetchTemplateDetail network error returns RemoteResult.Failure(Network)") {
        runTest {
            coEvery { api.getTemplateDetail(templateId = 777) } throws IOException("network")

            val result = repository.fetchTemplateDetail(templateId = 777)

            result shouldBe RemoteResult.Failure(AppError.Network)
            coVerify(exactly = 1) { api.getTemplateDetail(templateId = 777) }
        }
    }
})
