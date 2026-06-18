package com.chaikasoft.app.domain.usecases.templatesUseCases

import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.RemoteResult
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
            coEvery { getTemplateDetailUseCase(1) } returns RemoteResult.Success(template)
            coEvery {
                getProductsByIdsUseCase(listOf(productCoffee.id, productTea.id))
            } returns mapOf(
                productTea.id to productTea,
                productCoffee.id to productCoffee
            )

            val result = useCase(1)

            result shouldBe RemoteResult.Success(ResolvedTemplateDetailDomain(
                template = template,
                items = listOf(
                    ResolvedTemplateItemDomain(productCoffee.id, quantity = 1, productCoffee),
                    ResolvedTemplateItemDomain(productTea.id, quantity = 2, productTea),
                    ResolvedTemplateItemDomain(productCoffee.id, quantity = 3, productCoffee)
                )
            ))
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
            coEvery { getTemplateDetailUseCase(2) } returns RemoteResult.Success(template)
            coEvery { getProductsByIdsUseCase(listOf(missingId)) } returns emptyMap()

            val result = useCase(2) as RemoteResult.Success

            result.data.items shouldBe listOf(
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
            coEvery { getTemplateDetailUseCase(3) } returns RemoteResult.Success(template)
            coEvery { getProductsByIdsUseCase(emptyList()) } returns emptyMap()

            val result = useCase(3)

            result shouldBe RemoteResult.Success(
                ResolvedTemplateDetailDomain(template = template, items = emptyList())
            )
            coVerify(exactly = 1) { getProductsByIdsUseCase(emptyList()) }
        }
    }

    test("remote failure is returned without resolving products") {
        runTest {
            coEvery { getTemplateDetailUseCase(4) } returns RemoteResult.Failure(AppError.Network())

            val result = useCase(4)

            result shouldBe RemoteResult.Failure(AppError.Network())
            coVerify(exactly = 0) { getProductsByIdsUseCase(any()) }
        }
    }

    test("local product lookup failure maps to Unknown") {
        runTest {
            val template = TemplateDomain(
                id = 5,
                templateName = "Local failure set",
                description = "Local error",
                content = listOf(TemplateContentDomain(productId = productTea.id, quantity = 1))
            )
            val error = IllegalStateException("db failed")
            coEvery { getTemplateDetailUseCase(5) } returns RemoteResult.Success(template)
            coEvery { getProductsByIdsUseCase(listOf(productTea.id)) } throws error

            val result = useCase(5) as RemoteResult.Failure

            (result.error as AppError.Unknown).cause shouldBe error
        }
    }
})
