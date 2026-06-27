package com.chaikasoft.app.diagnostics

import java.util.Locale

object DiagnosticSanitizer {
    private val safeTagValuePattern = Regex("^[a-z0-9_.-]{1,64}$")
    private val throwableTypePattern = Regex("^[A-Za-z0-9_.-]{1,80}$")
    private val ruBusinessWords = listOf(
        "\u0431\u0438\u043b\u0435\u0442",
        "\u0437\u0430\u043a\u0430\u0437",
        "\u043f\u043e\u0435\u0437\u0434",
        "\u0440\u0435\u0439\u0441",
        "\u0441\u043c\u0435\u043d",
        "\u043e\u0442\u0447\u0435\u0442",
        "\u043e\u0442\u0447\u0451\u0442"
    ).joinToString("|")
    private val forbiddenPatterns = listOf(
        Regex("(?i)bearer\\s+[A-Za-z0-9._~+/-]{12,}"),
        Regex("(?i)(access|refresh)[_-]?token\\s*[:=]\\s*[^\\s,;]+"),
        Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"),
        Regex("(?i)https?://[^\\s?]+\\?[^\\s]+"),
        Regex(
            "(?i)(ticket|order|train|shift|report|trip)" +
                "\\s*[#:=\\u2116-]?\\s*[\\p{L}0-9_-]{2,}"
        ),
        Regex(
            "(?i)($ruBusinessWords)" +
                "\\s*[#:=\\u2116-]?\\s*[\\p{L}0-9_-]{2,}"
        ),
        Regex("(?i)(phone|tabno|personnel)\\s*[#:=\\u2116-]?\\s*[^\\s,;]+"),
        Regex("\\+?7[\\s()-]*\\d{3}[\\s()-]*\\d{3}[\\s()-]*\\d{2}[\\s()-]*\\d{2}"),
        Regex("\\{\\s*\"[^\"]+\"\\s*:")
    )

    fun safeThrowable(original: Throwable, eventType: DiagnosticEventType): Throwable {
        val safeType = safeThrowableType(original)
        val sanitized = SanitizedDiagnosticException(
            "diagnostic_event=${eventType.wireName}; throwable_type=$safeType"
        )
        sanitized.stackTrace = original.stackTrace
        return sanitized
    }

    fun safeThrowableType(throwable: Throwable): String = throwable::class.java.simpleName
        .takeIf { throwableTypePattern.matches(it) }
        ?: "Throwable"

    fun safeTags(tags: Map<String, String>): Map<String, String> = tags
        .filterKeys { key -> DiagnosticTag.entries.any { it.key == key } }
        .mapValuesNotNull { (_, value) -> safeTagValue(value) }

    fun safeTagValue(value: String): String? {
        val normalized = value.trim().lowercase(Locale.US)
        return normalized.takeIf { safeTagValuePattern.matches(it) && !containsForbiddenText(it) }
    }

    fun safeMessage(value: String?): String? = when {
        value == null -> null
        containsForbiddenText(value) -> "sanitized"
        value.any(Char::isWhitespace) -> "sanitized"
        value.length > MAX_SAFE_MESSAGE_LENGTH -> "sanitized"
        else -> value
    }

    fun containsForbiddenText(value: String?): Boolean {
        if (value.isNullOrBlank()) return false
        return forbiddenPatterns.any { it.containsMatchIn(value) }
    }

    private inline fun <K, V, R : Any> Map<K, V>.mapValuesNotNull(
        transform: (Map.Entry<K, V>) -> R?
    ): Map<K, R> = mapNotNull { entry -> transform(entry)?.let { entry.key to it } }.toMap()

    private const val MAX_SAFE_MESSAGE_LENGTH = 80
}

class SanitizedDiagnosticException(message: String) : RuntimeException(message)
