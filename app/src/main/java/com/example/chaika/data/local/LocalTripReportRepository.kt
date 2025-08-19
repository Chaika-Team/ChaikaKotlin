package com.example.chaika.data.local

import android.content.Context
import com.example.chaika.domain.models.TripReport
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class LocalTripReportRepository @Inject constructor(
    private val context: Context,
) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(TripReport::class.java)

    fun saveTripReport(tripReport: TripReport, fileName: String): Boolean {
        return try {
            val jsonString = adapter.toJson(tripReport)

            // Создаём директорию для отчётов, если её ещё нет
            val reportDir = File(context.filesDir, "reports")
            if (!reportDir.exists()) {
                reportDir.mkdirs()
            }

            // Сохраняем JSON-файл в папку "reports"
            val file = File(reportDir, fileName)
            FileOutputStream(file).use { output ->
                output.write(jsonString.toByteArray(StandardCharsets.UTF_8))
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}