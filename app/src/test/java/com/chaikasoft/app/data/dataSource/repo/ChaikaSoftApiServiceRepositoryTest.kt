package com.chaikasoft.app.data.dataSource.repo

import com.chaikasoft.app.data.dataSource.apiService.ChaikaSoftApiService
import com.chaikasoft.app.data.dataSource.dto.ProductInfoDto
import com.chaikasoft.app.data.dataSource.dto.ProductInfoListResponseDto
import com.chaikasoft.app.data.dataSource.dto.TemplateContentDto
import com.chaikasoft.app.data.dataSource.dto.TemplateDetailResponseDto
import com.chaikasoft.app.data.dataSource.dto.TemplateDto
import com.chaikasoft.app.data.dataSource.dto.TemplateListResponseDto
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

private const val DEFAULT_IMAGE =
    "https://i.pinimg.com/736x/5b/d3/e7/5bd3e779f192cb04cf35b859e0d50cbc.jpg"

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

            result.size shouldBe 1
            result.first().id shouldBe 1
            result.first().price shouldBe 1230
            result.first().image shouldBe DEFAULT_IMAGE
            coVerify(exactly = 1) { api.getProducts(limit = 50, offset = 100) }
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

            result.size shouldBe 1
            result.first().id shouldBe 5
            result.first().content.first().productId shouldBe 10
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

            result.id shouldBe 777
            result.templateName shouldBe "Detail"
            coVerify(exactly = 1) { api.getTemplateDetail(templateId = 777) }
        }
    }
})
