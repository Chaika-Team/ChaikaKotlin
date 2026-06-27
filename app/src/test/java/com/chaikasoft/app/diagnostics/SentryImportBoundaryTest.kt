package com.chaikasoft.app.diagnostics

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.pathString
import kotlin.streams.asSequence

class SentryImportBoundaryTest : FunSpec({
    test("Sentry imports stay inside diagnostics layer") {
        val root = findProjectRoot(Path.of(""))
        val sourceRoot = root.resolve("app/src/main/java")
        val offenders = Files.walk(sourceRoot).use { stream ->
            stream.asSequence()
                .filter { it.name.endsWith(".kt") }
                .filter { path -> path.toFile().readText().contains("import io.sentry") }
                .filterNot { path -> path.pathString.contains("${java.io.File.separator}diagnostics${java.io.File.separator}") }
                .map { sourceRoot.relativize(it).pathString }
                .toList()
        }

        offenders.shouldBeEmpty()
    }
})

private fun findProjectRoot(start: Path): Path {
    var current = start.toAbsolutePath()
    while (!Files.exists(current.resolve("settings.gradle.kts"))) {
        current = current.parent
    }
    return current
}
