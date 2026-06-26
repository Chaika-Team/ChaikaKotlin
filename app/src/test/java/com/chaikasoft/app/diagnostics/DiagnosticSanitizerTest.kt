package com.chaikasoft.app.diagnostics

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class DiagnosticSanitizerTest : FunSpec({
    test("safeThrowable preserves stack trace and replaces original message") {
        val original = IllegalStateException("order=123 token=secret")
        original.stackTrace = arrayOf(StackTraceElement("A", "b", "A.kt", 7))

        val safe = DiagnosticSanitizer.safeThrowable(
            original,
            DiagnosticEventType.APP_ERROR_UNKNOWN
        )

        safe.message shouldBe "diagnostic_event=app_error_unknown; throwable_type=IllegalStateException"
        safe.stackTrace.toList().map { it.lineNumber }.shouldContainExactly(7)
    }

    test("safeTags keeps only allowlisted enum-like values") {
        val tags = mapOf(
            "app_area" to "auth",
            "operation" to "refresh_products",
            "event_type" to "app_error_unknown",
            "free_text" to "should not pass",
            "throwable_type" to "IllegalStateException",
            "severity" to "error",
            "app_version" to "1.2.0",
            "build_type" to "release",
            "bad_value" to "user@example.test"
        )

        DiagnosticSanitizer.safeTags(tags).shouldContainExactly(
            mapOf(
                "app_area" to "auth",
                "operation" to "refresh_products",
                "event_type" to "app_error_unknown",
                "throwable_type" to "illegalstateexception",
                "severity" to "error",
                "app_version" to "1.2.0",
                "build_type" to "release"
            )
        )
    }

    test("safeTagValue rejects pii tokens urls and free text") {
        DiagnosticSanitizer.safeTagValue("Bearer abcdefghijklmnopqrstuvwxyz").shouldBeNull()
        DiagnosticSanitizer.safeTagValue("https://example.test/path?token=secret").shouldBeNull()
        DiagnosticSanitizer.safeTagValue("user@example.test").shouldBeNull()
        DiagnosticSanitizer.safeTagValue("plain free text").shouldBeNull()
    }

    test("safeMessage sanitizes free text and sensitive patterns") {
        DiagnosticSanitizer.safeMessage("simple_error") shouldBe "simple_error"
        DiagnosticSanitizer.safeMessage("Authorization failed for user@example.test") shouldBe "sanitized"
        DiagnosticSanitizer.safeMessage("This is free text") shouldBe "sanitized"
        DiagnosticSanitizer.safeMessage("{\"access_token\":\"secret\"}") shouldBe "sanitized"
    }
})
