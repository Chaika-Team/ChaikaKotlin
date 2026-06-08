package com.chaikasoft.app.domain.usecases.templatesUseCases

import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.models.ResolvedTemplateDetailDomain
import com.chaikasoft.app.domain.models.ResolvedTemplateItemDomain
import com.chaikasoft.app.domain.models.TemplateContentDomain
import com.chaikasoft.app.domain.models.TemplateDomain
import com.chaikasoft.app.domain.usecases.GetProductsByIdsUseCase
import com.chaikasoft.app.domain.usecases.GetResolvedTemplateDetailUseCase
import com.chaikasoft.app.domain.usecases.GetTemplateDetailUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class GetResolvedTemplateDetailUseCaseTest : FunSpec({

    lateinit var getTemplateDetailUseCase: GetTemplateDetailUseCase
    lateinit var getProductsByIdsUseCase: GetProductsByIdsUseCase
    lateinit var useCase: GetResolvedTemplateDetailUseCase

    val productTea = ProductInfoDomain(
        id = 10,
        name = "Tea",
        description = "Black tea",
        image = "tea.png",
        price = 120
    )
    val productCoffee = ProductInfoDomain(
        id = 20,
        name = "Coffee",
        description = "Ground coffee",
        image = "coffee.png",
        price = 250
    )

    beforeTest {
        getTemplateDetailUseCase = mockk()
        getProductsByIdsUseCase = mockk()
        useCase = GetResolvedTemplateDetailUseCase(
            getTemplateDetailUseCase = getTemplateDetailUseCase,
            getProductsByIdsUseCase = getProductsByIdsUseCase
        )
    }

    test("resolves products while preserving template item order and quantities") {
        runTest {
            val template = TemplateDomain(
                id = 1,
                templateName = "Tea set",
                description = "Basic set",
                content = listOf(
                    TemplateContentDomain(productId = productCoffee.id, quantity = 1),
                    TemplateContentDomain(productId = productTea.id, quantity = 2),
                    TemplateContentDomain(productId = productCoffee.id, quantity = 3)
                )
            )
            coEvery { getTemplateDetailUseCase(1) } returns template
            coEvery {
                getProductsByIdsUseCase(listOf(productCoffee.id, productTea.id))
            } returns mapOf(
                productTea.id to productTea,
                productCoffee.id to productCoffee
            )

            val result = useCase(1)

            result shouldBe ResolvedTemplateDetailDomain(
                template = template,
                items = listOf(
                    ResolvedTemplateItemDomain(productCoffee.id, quantity = 1, productCoffee),
                    ResolvedTemplateItemDomain(productTea.id, quantity = 2, productTea),
                    ResolvedTemplateItemDomain(productCoffee.id, quantity = 3, productCoffee)
                )
            )
            coVerify(exactly = 1) { getTemplateDetailUseCase(1) }
            coVerify(exactly = 1) {
                getProductsByIdsUseCase(listOf(productCoffee.id, productTea.id))
            }
        }
    }

    test("keeps missing products as null instead of throwing") {
        runTest {
            val missingId = 999
            val template = TemplateDomain(
                id = 2,
                templateName = "Broken set",
                description = "Missing product",
                content = listOf(TemplateContentDomain(productId = missingId, quantity = 1))
            )
            coEvery { getTemplateDetailUseCase(2) } returns template
            coEvery { getProductsByIdsUseCase(listOf(missingId)) } returns emptyMap()

            val result = useCase(2)

            result.items shouldBe listOf(
                ResolvedTemplateItemDomain(productId = missingId, quantity = 1, product = null)
            )
        }
    }

    test("returns empty items when template content is empty") {
        runTest {
            val template = TemplateDomain(
                id = 3,
                templateName = "Empty set",
                description = "No products",
                content = emptyList()
            )
            coEvery { getTemplateDetailUseCase(3) } returns template
            coEvery { getProductsByIdsUseCase(emptyList()) } returns emptyMap()

            val result = useCase(3)

            result shouldBe ResolvedTemplateDetailDomain(template = template, items = emptyList())
            coVerify(exactly = 1) { getProductsByIdsUseCase(emptyList()) }
        }
    }
})
