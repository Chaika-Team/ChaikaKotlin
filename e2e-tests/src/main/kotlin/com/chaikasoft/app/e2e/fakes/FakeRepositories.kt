package com.chaikasoft.app.e2e.fakes

import com.chaikasoft.app.data.datasource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.chaikasoft.app.data.datasource.repo.ChaikaSoftReportsRepositoryInterface
import com.chaikasoft.app.data.datasource.repo.ChaikaTripperRepositoryInterface
import com.chaikasoft.app.data.datasource.repo.IAMApiServiceRepositoryInterface
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.ConductorDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.models.TemplateDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.sealed.UploadResult
import com.chaikasoft.app.e2e.fixtures.E2EFixtures
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeChaikaTripperRepository @Inject constructor() : ChaikaTripperRepositoryInterface {
    override suspend fun searchTripsByStations(
        date: String,
        fromCode: String,
        toCode: String,
    ): RemoteResult<List<TripDomain>> {
        val hasRoute = E2EFixtures.trips.any { it.from.code == fromCode && it.to.code == toCode }
        return if (hasRoute) {
            RemoteResult.Success(E2EFixtures.trips)
        } else {
            RemoteResult.Failure(AppError.Http(code = 404, body = "Route not found"))
        }
    }

    override suspend fun fetchAllStations(limit: Int): RemoteResult<List<StationDomain>> {
        return RemoteResult.Success(E2EFixtures.stations.take(limit))
    }
}

@Singleton
class FakeChaikaSoftApiServiceRepository @Inject constructor() : ChaikaSoftApiServiceRepositoryInterface {
    override suspend fun fetchProducts(limit: Int, offset: Int): List<ProductInfoDomain> {
        return E2EFixtures.products.drop(offset).take(limit)
    }

    override suspend fun fetchTemplates(
        query: String,
        limit: Int,
        offset: Int,
    ): List<TemplateDomain> {
        return E2EFixtures.templates
            .filter { template ->
                query.isBlank() || template.templateName.contains(query, ignoreCase = true)
            }
            .drop(offset)
            .take(limit)
    }

    override suspend fun fetchTemplateDetail(templateId: Int): TemplateDomain {
        return E2EFixtures.templates.firstOrNull { it.id == templateId }
            ?: error("Template with id=$templateId not found")
    }
}

@Singleton
class FakeChaikaSoftReportsRepository @Inject constructor() : ChaikaSoftReportsRepositoryInterface {
    override suspend fun uploadShiftReport(reportJson: String): UploadResult = UploadResult.Ok
}

@Singleton
class FakeIAMApiServiceRepository @Inject constructor() : IAMApiServiceRepositoryInterface {
    override suspend fun fetchUserInfo(accessToken: String): Result<ConductorDomain> {
        return Result.success(E2EFixtures.conductor)
    }
}
