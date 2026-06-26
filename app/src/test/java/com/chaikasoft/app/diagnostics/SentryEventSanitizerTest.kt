package com.chaikasoft.app.diagnostics

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.sentry.Breadcrumb
import io.sentry.SentryEvent
import io.sentry.protocol.Message
import io.sentry.protocol.Request
import io.sentry.protocol.SentryException
import io.sentry.protocol.User

class SentryEventSanitizerTest : FunSpec({
    test("sanitize clears user request breadcrumbs and non-allowlisted tags") {
        val event = SentryEvent().apply {
            user = User().apply { email = "user@example.test" }
            request = Request().apply { url = "https://example.test/path?token=secret" }
            breadcrumbs = listOf(Breadcrumb().apply { message = "clicked button" })
            tags = mapOf(
                "app_area" to "auth",
                "operation" to "authorize",
                "unsafe" to "user@example.test"
            )
        }

        val sanitized = SentryEventSanitizer.sanitize(event)

        sanitized?.user.shouldBeNull()
        sanitized?.request.shouldBeNull()
        sanitized?.breadcrumbs.orEmpty().shouldBeEmpty()
        sanitized?.tags.orEmpty().shouldContainExactly(
            mapOf(
                "app_area" to "auth",
                "operation" to "authorize"
            )
        )
    }

    test("sanitize replaces unsafe event and exception messages") {
        val event = SentryEvent().apply {
            message = Message().apply {
                message = "Failed for user@example.test"
                formatted = "Failed for user@example.test"
            }
            exceptions = listOf(
                SentryException().apply { value = "order=12345" }
            )
        }

        val sanitized = SentryEventSanitizer.sanitize(event)

        sanitized?.message?.message shouldBe "sanitized"
        sanitized?.message?.formatted shouldBe "sanitized"
        sanitized?.exceptions?.first()?.value shouldBe "sanitized"
    }
})
