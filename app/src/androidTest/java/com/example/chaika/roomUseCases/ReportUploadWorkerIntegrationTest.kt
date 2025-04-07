// ReportUploadWorkerIntegrationTest.kt
package com.example.chaika.roomUseCases

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.TestDriver
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.chaika.domain.models.CartItemReport
import com.example.chaika.domain.models.CartOperationReport
import com.example.chaika.domain.models.TripReport
import com.example.chaika.domain.usecases.EnqueueSendTripReportUseCase
import com.example.chaika.data.room.repo.RoomTripReportRepositoryInterface
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ReportUploadWorkerIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var enqueueSendTripReportUseCase: EnqueueSendTripReportUseCase

    @Inject
    lateinit var reportRepository: RoomTripReportRepositoryInterface

    @Inject
    lateinit var workManager: WorkManager

    private lateinit var testDriver: TestDriver

    @Before
    fun setup() {
        hiltRule.inject()
        val context = ApplicationProvider.getApplicationContext<Context>()
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        testDriver = WorkManagerTestInitHelper.getTestDriver(context)!!
    }

    @Test
    fun testReportUploadWorkerMarksReportAsSent() = runBlocking {
        // Создаем dummy-отчет с тестовыми данными
        val dummyReport = TripReport(
            routeID = "TestRoute",
            startTime = "2024-10-15T00:00:00+03:00",
            endTime = "2024-10-15T01:00:00+03:00",
            carriageID = "TestCarriage",
            carts = listOf(
                CartOperationReport(
                    employeeID = "12345",
                    operationType = 0,
                    operationTime = "2024-10-15T00:12:00+03:00",
                    items = listOf(
                        CartItemReport(
                            productID = 101,
                            quantity = 2,
                            price = 15.0
                        )
                    )
                )
            )
        )

        // Вызываем Use Case для сохранения отчета и постановки задачи в очередь
        enqueueSendTripReportUseCase(dummyReport)

        // Получаем список WorkInfo для уникальной работы "reportUploadWork"
        var workInfos = workManager.getWorkInfosForUniqueWork("reportUploadWork").get()
        assertTrue("There should be at least one enqueued work", workInfos.isNotEmpty())

        // Для каждой работы, которая в состоянии ENQUEUED, симулируем удовлетворение ограничений
        workInfos.forEach { workInfo ->
            if (workInfo.state == WorkInfo.State.ENQUEUED) {
                try {
                    testDriver.setAllConstraintsMet(workInfo.id)
                } catch (e: IllegalArgumentException) {
                    println("Work with id ${workInfo.id} is not enqueued; skipping setAllConstraintsMet")
                }
            }
        }

        // Ждем завершения работ: опрашиваем WorkManager до тех пор, пока все работы не будут завершены
        val timeout = System.currentTimeMillis() + 5000 // 5 секунд ожидания
        do {
            Thread.sleep(500)
            workInfos = workManager.getWorkInfosForUniqueWork("reportUploadWork").get()
        } while (workInfos.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
            && System.currentTimeMillis() < timeout)

        // Выводим состояние работы для отладки
        workInfos.forEach {
            println("WorkInfo id=${it.id} state=${it.state}")
        }

        // Проверяем, что после выполнения воркера список неподтвержденных отчетов пуст
        val pendingReports = reportRepository.getPendingTripReports()
        assertTrue(
            "Pending reports should be empty after successful upload, but found: $pendingReports",
            pendingReports.isEmpty()
        )
    }

}
