package com.chaikasoft.app.e2e.tests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.LargeTest
import com.chaikasoft.app.R
import com.chaikasoft.app.data.room.AppDatabase
import com.chaikasoft.app.data.room.entities.CartItem
import com.chaikasoft.app.data.room.entities.CartOperation
import com.chaikasoft.app.data.room.repo.RoomConductorRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomConductorTripShiftRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomProductInfoRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomStationRepositoryInterface
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.report.CartIdReport
import com.chaikasoft.app.domain.models.report.CartItemReport
import com.chaikasoft.app.domain.models.report.CartReport
import com.chaikasoft.app.domain.models.report.ShiftReportReport
import com.chaikasoft.app.domain.models.report.TripIdReport
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.chaikasoft.app.domain.sealed.StartShiftResult
import com.chaikasoft.app.domain.usecases.StartShiftUseCase
import com.chaikasoft.app.e2e.fixtures.E2EFixtures
import com.chaikasoft.app.e2e.rules.FailureDiagnosticsRule
import com.chaikasoft.app.ui.activities.MainActivity
import com.squareup.moshi.Moshi
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
@HiltAndroidTest
class HermeticSmokeE2ETest {
    private companion object {
        const val SAVELOVSKAYA_CODE = "2001002"
        const val HISTORICAL_SHIFT_UUID = "trip-e2e-history-report"
        const val UNKNOWN_PRODUCT_ID = 404
        const val UNKNOWN_CONDUCTOR_EMPLOYEE_ID = "E2E-404"
    }

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 2)
    val diagnosticsRule = FailureDiagnosticsRule()

    @Inject
    lateinit var startShiftUseCase: StartShiftUseCase

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var stationRepository: RoomStationRepositoryInterface

    @Inject
    lateinit var productInfoRepository: RoomProductInfoRepositoryInterface

    @Inject
    lateinit var conductorRepository: RoomConductorRepositoryInterface

    @Inject
    lateinit var shiftRepository: RoomConductorTripShiftRepositoryInterface

    @Inject
    lateinit var moshi: Moshi

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun launch_authenticatedSessionOpensTripScreen() {
        waitForTag("tripMainScreen")
        composeRule.onNodeWithTag("newTripButton").assertIsDisplayed()
    }

    @Test
    fun tripSearch_showsStationSuggestionsWithoutNetwork() {
        waitForTag("newTripButton")
        composeRule.onNodeWithTag("newTripButton").performClick()
        waitForTag("findTripScreen")

        composeRule.onNodeWithTag("tripFromStationInput").performTextInput("СА")
        waitForTag("tripFromStationItem_$SAVELOVSKAYA_CODE")
        composeRule.onNodeWithTag("tripFromStationItem_$SAVELOVSKAYA_CODE").performClick()
    }

    @Test
    fun bottomBar_blocksProtectedSectionsWithoutActiveShift() {
        waitForTag("tripMainScreen")

        assertProtectedSectionBlocked(
            bottomBarTag = "bottomBarProduct",
            protectedScreenTags = arrayOf("productEntryScreen", "productPackageScreen"),
        )
        assertProtectedSectionBlocked(
            bottomBarTag = "bottomBarStatistics",
            protectedScreenTags = arrayOf("statisticsScreen"),
        )
        assertProtectedSectionBlocked(
            bottomBarTag = "bottomBarOperation",
            protectedScreenTags = arrayOf("operationScreen"),
        )

        composeRule.onNodeWithTag("bottomBarProfile").performClick()
        waitForTag("profileScreen")

        composeRule.onNodeWithTag("bottomBarTrip").performClick()
        waitForTag("tripMainScreen")
    }

    @Test
    fun bottomBar_allowsProtectedSectionsWithActiveShift() {
        waitForTag("tripMainScreen")
        startActiveShift()

        composeRule.onNodeWithTag("bottomBarProduct").performClick()
        waitForAnyTag("productEntryScreen", "productPackageScreen")

        composeRule.onNodeWithTag("bottomBarStatistics").performClick()
        waitForTag("statisticsScreen")

        composeRule.onNodeWithTag("bottomBarOperation").performClick()
        waitForTag("operationScreen")

        composeRule.onNodeWithTag("bottomBarProfile").performClick()
        waitForTag("profileScreen")

        composeRule.onNodeWithTag("bottomBarTrip").performClick()
        waitForTag("tripMainScreen")
    }

    @Test
    fun productEntry_hasPrimaryCtaTag() {
        waitForTag("bottomBarProduct")
        startActiveShift()

        composeRule.onNodeWithTag("bottomBarProduct").performClick()

        composeRule.waitUntil(timeoutMillis = 10_000L) {
            composeRule.onAllNodesWithTag("productEntryFillPackageButton").fetchSemanticsNodes().isNotEmpty() ||
                composeRule.onAllNodesWithTag("packageListGrid").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun activeTrip_canBeDeletedWhilePreservingPackage() {
        waitForTag("tripMainScreen")
        startActiveShift()
        seedPackageOperation()

        val deleteButtonTag = "currentTripDelete_${E2EFixtures.trips.first().uuid}"
        waitForTag(deleteButtonTag)
        composeRule.onNodeWithTag(deleteButtonTag, useUnmergedTree = true).performClick()

        waitForTag("deleteTripBottomSheet")
        waitForTag("deleteTripPreservePackageCheckbox")
        composeRule.onNodeWithTag("deleteTripPreservePackageCheckbox").assertIsOn()
        composeRule.onNodeWithTag("deleteTripConfirmButton").performClick()

        waitForTag("newTripButton")
        runBlocking {
            check(db.conductorTripShiftDao().getByUuid(E2EFixtures.trips.first().uuid) == null)
            check(db.cartOperationDao().getAllOperations().first().size == 1)
            check(db.cartItemDao().getAllCartItems().first().size == 1)
        }
    }

    @Test
    fun activeTrip_finishingMovesCardToHistory() {
        waitForTag("tripMainScreen")
        startActiveShift()

        waitForTag("currentTripFinishButton")
        composeRule.onNodeWithTag("currentTripFinishButton").performClick()

        waitForTag("newTripButton")
        waitForTag("historyRecordCard_${E2EFixtures.trips.first().uuid}")
        runBlocking {
            val savedShift = db.conductorTripShiftDao().getByUuid(E2EFixtures.trips.first().uuid)
            check(savedShift?.status == TripShiftStatusDomain.SENT.code)
        }
    }

    @Test
    fun logout_returnsToLoginScreen() {
        waitForTag("bottomBarProfile")
        composeRule.onNodeWithTag("bottomBarProfile").performClick()
        waitForTag("profileScreen")

        composeRule.onNodeWithTag("profileLogoutButton").performClick()
        waitForTag("profileLogoutConfirmButton")
        composeRule.onNodeWithTag("profileLogoutConfirmButton").performClick()

        waitForTag("loginScreen")
        composeRule.onNodeWithTag("loginButton").assertIsDisplayed()
    }

    @Test
    fun historicalTrip_opensReadOnlyStatisticsAndOperationsFromSavedReport() {
        waitForTag("tripMainScreen")
        seedHistoricalFinishedShift()

        val historyCardTag = "historyRecordCard_$HISTORICAL_SHIFT_UUID"
        val retryButtonTag = "historyRetrySend_$HISTORICAL_SHIFT_UUID"
        val unknownProductName = composeRule.activity.getString(
            R.string.historical_unknown_product_name,
            UNKNOWN_PRODUCT_ID
        )
        val unknownConductorName = composeRule.activity.getString(
            R.string.historical_unknown_conductor_name,
            UNKNOWN_CONDUCTOR_EMPLOYEE_ID
        )

        waitForTag(historyCardTag)
        composeRule.onNodeWithTag(retryButtonTag, useUnmergedTree = true).assertIsDisplayed()

        composeRule.onNodeWithTag(historyCardTag, useUnmergedTree = true).performClick()
        waitForTag("statisticsScreen")
        composeRule.onNodeWithTag("historicalBottomBarStatistics").assertIsDisplayed()
        composeRule.onNodeWithTag("historicalBottomBarOperation").assertIsDisplayed()
        assertTagDoesNotExist("bottomBarProduct")
        waitForText(unknownProductName)

        composeRule.onNodeWithTag("historicalBottomBarOperation").performClick()
        waitForTag("historicalOperationScreen")
        waitForText(unknownProductName)
        waitForText(unknownConductorName)

        composeRule.onNodeWithTag("historicalBottomBarStatistics").performClick()
        waitForTag("statisticsScreen")
        composeRule.onNodeWithTag("historicalBottomBarOperation").performClick()
        waitForTag("historicalOperationScreen")

        composeRule.onNodeWithContentDescription(
            composeRule.activity.getString(R.string.btn_back)
        ).performClick()
        waitForTag("tripMainScreen")
    }

    private fun waitForTag(tag: String, timeoutMillis: Long = 10_000L) {
        composeRule.waitUntil(timeoutMillis = timeoutMillis) {
            composeRule.onAllNodesWithTag(tag, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForAnyTag(vararg tags: String, timeoutMillis: Long = 10_000L) {
        composeRule.waitUntil(timeoutMillis = timeoutMillis) {
            tags.any { tag ->
                composeRule.onAllNodesWithTag(tag, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
            }
        }
    }

    private fun waitForTagGone(tag: String, timeoutMillis: Long = 10_000L) {
        composeRule.waitUntil(timeoutMillis = timeoutMillis) {
            composeRule.onAllNodesWithTag(tag, useUnmergedTree = true).fetchSemanticsNodes().isEmpty()
        }
    }

    private fun waitForText(text: String, timeoutMillis: Long = 10_000L) {
        composeRule.waitUntil(timeoutMillis = timeoutMillis) {
            composeRule.onAllNodesWithText(text, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun assertProtectedSectionBlocked(
        bottomBarTag: String,
        protectedScreenTags: Array<String>,
    ) {
        composeRule.onNodeWithTag(bottomBarTag).performClick()
        waitForTag("navigationBlockedBottomSheet")
        protectedScreenTags.forEach { tag ->
            assertTagDoesNotExist(tag)
        }
        composeRule.onNodeWithTag("navigationBlockedOkButton").performClick()
        waitForTagGone("navigationBlockedBottomSheet")
        waitForTag("tripMainScreen")
    }

    private fun assertTagDoesNotExist(tag: String) {
        check(composeRule.onAllNodesWithTag(tag, useUnmergedTree = true).fetchSemanticsNodes().isEmpty()) {
            "Expected no nodes with tag $tag"
        }
    }

    private fun startActiveShift() {
        val result = runBlocking {
            startShiftUseCase(
                trip = E2EFixtures.trips.first(),
                activeCarriage = E2EFixtures.activeCarriage,
            )
        }
        check(result == StartShiftResult.Started) {
            "Expected active shift seed to create a fresh active shift, got $result"
        }
        composeRule.waitForIdle()
        waitForTag("currentTripFinishButton")
    }

    private fun seedPackageOperation() = runBlocking {
        val conductor = db.conductorDao()
            .getConductorByEmployeeID(E2EFixtures.conductor.employeeID)
            ?: error("Expected authenticated conductor")
        val product = db.productInfoDao().getProductById(E2EFixtures.products.first().id)
            ?: error("Expected refreshed product")
        val operationId = db.cartOperationDao().insertOperation(
            CartOperation(
                operationType = OperationTypeDomain.ADD.ordinal,
                operationTime = "2026-04-11T08:30:00+03:00",
                conductorId = conductor.id
            )
        ).toInt()
        db.cartItemDao().insertCartItem(
            CartItem(
                cartOperationId = operationId,
                productId = product.id,
                impact = 2
            )
        )
        composeRule.waitForIdle()
    }

    private fun seedHistoricalFinishedShift() = runBlocking {
        db.clearAllTables()
        stationRepository.upsertAll(E2EFixtures.stations)
        productInfoRepository.upsertAll(E2EFixtures.products)
        conductorRepository.insertConductor(E2EFixtures.conductor)

        val trip = E2EFixtures.trips.first().copy(
            uuid = HISTORICAL_SHIFT_UUID,
            trainNumber = "099A",
        )
        val shiftCreated = shiftRepository.tryStartNewShift(
            ConductorTripShiftDomain(
                trip = trip,
                activeCarriage = E2EFixtures.activeCarriage,
                status = TripShiftStatusDomain.ACTIVE
            )
        )
        check(shiftCreated == StartShiftResult.Started) {
            "Expected historical shift seed to create a fresh active shift"
        }

        shiftRepository.updateStatusAndReport(
            uuid = HISTORICAL_SHIFT_UUID,
            newStatus = TripShiftStatusDomain.FINISHED.code,
            reportJson = buildHistoricalReportJson(trip),
            updatedAt = System.currentTimeMillis()
        )
        composeRule.waitForIdle()
    }

    private fun buildHistoricalReportJson(trip: TripDomain): String {
        val report = ShiftReportReport(
            tripId = TripIdReport(
                routeId = trip.trainNumber,
                startTime = trip.departure
            ),
            endTime = trip.arrival,
            carriageId = E2EFixtures.activeCarriage.carNumber.toInt(),
            carts = listOf(
                CartReport(
                    cartId = CartIdReport(
                        employeeId = E2EFixtures.conductor.employeeID,
                        operationTime = "2026-04-11T09:00:00+03:00"
                    ),
                    operationType = OperationTypeDomain.SOLD_CASH.ordinal,
                    items = listOf(
                        CartItemReport(productId = 1, quantity = -2, price = 15000)
                    )
                ),
                CartReport(
                    cartId = CartIdReport(
                        employeeId = UNKNOWN_CONDUCTOR_EMPLOYEE_ID,
                        operationTime = "2026-04-11T09:10:00+03:00"
                    ),
                    operationType = OperationTypeDomain.SOLD_CART.ordinal,
                    items = listOf(
                        CartItemReport(
                            productId = UNKNOWN_PRODUCT_ID,
                            quantity = -1,
                            price = 9900
                        )
                    )
                )
            )
        )
        return moshi.adapter(ShiftReportReport::class.java).toJson(report)
    }
}
