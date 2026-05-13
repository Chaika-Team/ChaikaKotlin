package com.chaikasoft.app.e2e.rules

import android.os.ParcelFileDescriptor
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.services.storage.TestStorage
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class FailureDiagnosticsRule : TestWatcher() {

    override fun failed(e: Throwable, description: Description) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val device = UiDevice.getInstance(instrumentation)
        val cacheDir = instrumentation.targetContext.cacheDir
        val baseName = sanitizeFileName("${description.className}_${description.methodName}")

        captureScreenshot(device, cacheDir, baseName)
        captureWindowHierarchy(device, cacheDir, baseName)
        captureLogcat(instrumentation, baseName)
    }

    private fun captureScreenshot(device: UiDevice, cacheDir: File, baseName: String) {
        val screenshotFile = File(cacheDir, "$baseName.png")
        runCatching {
            device.takeScreenshot(screenshotFile)
            if (screenshotFile.exists()) {
                screenshotFile.inputStream().use { input ->
                    writeToTestStorage("screenshots/$baseName.png", input)
                }
            }
        }
    }

    private fun captureWindowHierarchy(device: UiDevice, cacheDir: File, baseName: String) {
        val hierarchyFile = File(cacheDir, "$baseName.xml")
        runCatching {
            device.dumpWindowHierarchy(hierarchyFile)
            if (hierarchyFile.exists()) {
                hierarchyFile.inputStream().use { input ->
                    writeToTestStorage("hierarchy/$baseName.xml", input)
                }
            }
        }
    }

    private fun captureLogcat(instrumentation: android.app.Instrumentation, baseName: String) {
        runCatching {
            val logcat = readShellOutput(instrumentation, "logcat -d -t 2000")
            writeToTestStorage("logcat/$baseName.txt", logcat.byteInputStream())
        }
        runCatching {
            instrumentation.uiAutomation.executeShellCommand("logcat -c").close()
        }
    }

    private fun readShellOutput(instrumentation: android.app.Instrumentation, command: String): String {
        val parcel = instrumentation.uiAutomation.executeShellCommand(command)
        return parcel.useAndReadAllText()
    }

    private fun ParcelFileDescriptor.useAndReadAllText(): String {
        return try {
            FileInputStream(fileDescriptor).bufferedReader().use { it.readText() }
        } finally {
            close()
        }
    }

    private fun writeToTestStorage(path: String, input: InputStream) {
        TestStorage().openOutputFile(path).use { output ->
            input.copyTo(output)
        }
    }

    private fun sanitizeFileName(value: String): String {
        return value.replace(Regex("[^A-Za-z0-9._-]"), "_")
    }
}
