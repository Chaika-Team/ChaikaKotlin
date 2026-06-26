package com.chaikasoft.app.diagnostics

import com.chaikasoft.app.BuildConfig
import io.sentry.SentryEvent
import io.sentry.SentryOptions
import io.sentry.protocol.Message
import java.util.Collections

object SentryEventSanitizer {
    fun sanitize(event: SentryEvent): SentryEvent? {
        event.user = null
        event.request = null
        event.breadcrumbs = Collections.emptyList()
        event.tags = DiagnosticSanitizer.safeTags(event.tags.orEmpty())
        event.message = event.message?.sanitized()
        event.exceptions?.forEach { exception ->
            exception.value = DiagnosticSanitizer.safeMessage(exception.value) ?: "sanitized"
        }
        return event
    }

    private fun Message.sanitized(): Message? {
        val sanitizedMessage = DiagnosticSanitizer.safeMessage(formatted ?: message) ?: return null
        return Message().apply {
            message = sanitizedMessage
            formatted = sanitizedMessage
        }
    }
}

object SentryDiagnosticsInitializer {
    fun init(application: android.app.Application) {
        if (!BuildConfig.ERROR_REPORTING_ENABLED || BuildConfig.GLITCHTIP_DSN.isBlank()) return
        io.sentry.android.core.SentryAndroid.init(application) { options ->
            options.dsn = BuildConfig.GLITCHTIP_DSN
            options.environment = BuildConfig.ERROR_REPORTING_ENVIRONMENT
            options.release = buildString {
                append(BuildConfig.APPLICATION_ID)
                append('@')
                append(BuildConfig.VERSION_NAME)
                append('+')
                append(BuildConfig.VERSION_CODE)
            }
            options.isSendDefaultPii = false
            options.isDebug = false
            options.isEnableAutoSessionTracking = false
            options.isEnableUserInteractionBreadcrumbs = false
            options.isEnableUserInteractionTracing = false
            options.isEnableAppLifecycleBreadcrumbs = false
            options.isEnableActivityLifecycleBreadcrumbs = false
            options.isEnableSystemEventBreadcrumbs = false
            options.isAttachScreenshot = false
            options.isAttachViewHierarchy = false
            options.isSendClientReports = false
            options.isCollectAdditionalContext = false
            options.maxBreadcrumbs = 0
            options.maxRequestBodySize = SentryOptions.RequestSize.NONE
            options.beforeBreadcrumb = SentryOptions.BeforeBreadcrumbCallback { _, _ -> null }
            options.beforeSend = SentryOptions.BeforeSendCallback { event, _ ->
                SentryEventSanitizer.sanitize(event)
            }
        }
    }
}
