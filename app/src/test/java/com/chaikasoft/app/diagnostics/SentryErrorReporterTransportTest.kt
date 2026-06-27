package com.chaikasoft.app.diagnostics

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.sentry.Hint
import io.sentry.ITransportFactory
import io.sentry.RequestDetails
import io.sentry.Sentry
import io.sentry.SentryEnvelope
import io.sentry.SentryEvent
import io.sentry.SentryItemType
import io.sentry.SentryOptions
import io.sentry.transport.ITransport
import io.sentry.transport.RateLimiter
import java.io.IOException
import java.util.concurrent.CopyOnWriteArrayList

class SentryErrorReporterTransportTest : FunSpec({
    val transport = InMemorySentryTransport()

    afterTest {
        Sentry.close()
        transport.clear()
    }

    test("recordNonFatal creates sanitized Sentry event without network transport") {
        Sentry.init { options ->
            options.dsn = "https://public@example.test/1"
            options.isEnableUncaughtExceptionHandler = false
            options.isEnableShutdownHook = false
            options.isSendDefaultPii = false
            options.maxBreadcrumbs = 0
            options.beforeSend = SentryOptions.BeforeSendCallback { event, _ ->
                SentryEventSanitizer.sanitize(event)
            }
            options.setTransportFactory(InMemorySentryTransportFactory(transport))
        }

        SentryErrorReporter().recordNonFatal(
            DiagnosticEvent(
                type = DiagnosticEventType.APP_ERROR_UNKNOWN,
                area = DiagnosticArea.AUTH,
                operation = DiagnosticOperation.AUTHORIZE,
                throwable = IllegalStateException("token=secret user@example.test"),
                tags = mapOf(
                    DiagnosticTag.BUILD_TYPE.key to "release",
                    "unsafe" to "user@example.test"
                )
            )
        )
        Sentry.flush(FLUSH_TIMEOUT_MS)

        val events = transport.events()
        events.shouldHaveSize(1)
        val event = events.single()
        event.user.shouldBeNull()
        event.request.shouldBeNull()
        event.breadcrumbs.orEmpty().shouldHaveSize(0)
        event.tags.orEmpty().shouldContainExactly(
            mapOf(
                DiagnosticTag.APP_AREA.key to "auth",
                DiagnosticTag.OPERATION.key to "authorize",
                DiagnosticTag.EVENT_TYPE.key to "app_error_unknown",
                DiagnosticTag.SEVERITY.key to "error",
                DiagnosticTag.THROWABLE_TYPE.key to "illegalstateexception",
                DiagnosticTag.BUILD_TYPE.key to "release"
            )
        )
        event.exceptions?.single()?.value shouldBe "sanitized"
    }
}) {
    private companion object {
        const val FLUSH_TIMEOUT_MS = 5_000L
    }
}

private class InMemorySentryTransportFactory(
    private val transport: InMemorySentryTransport
) : ITransportFactory {
    override fun create(options: SentryOptions, requestDetails: RequestDetails): ITransport {
        transport.serializer = options.serializer
        return transport
    }
}

private class InMemorySentryTransport : ITransport {
    @Volatile
    var serializer: io.sentry.ISerializer? = null

    private val events = CopyOnWriteArrayList<SentryEvent>()

    override fun send(envelope: SentryEnvelope, hint: Hint) {
        val currentSerializer = serializer ?: return
        envelope.items
            .filter { item -> item.header.type == SentryItemType.Event }
            .mapNotNull { item -> item.getEvent(currentSerializer) }
            .forEach { event -> events.add(event) }
    }

    fun events(): List<SentryEvent> = events.toList()

    fun clear() {
        events.clear()
        serializer = null
    }

    override fun flush(timeoutMillis: Long) = Unit

    override fun getRateLimiter(): RateLimiter? = null

    override fun close() = Unit

    @Throws(IOException::class)
    override fun close(isRestarting: Boolean) = Unit
}
