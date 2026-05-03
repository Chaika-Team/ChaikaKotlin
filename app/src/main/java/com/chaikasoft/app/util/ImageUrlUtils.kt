package com.chaikasoft.app.util

fun String.normalizedRemoteImageUrlOrNull(): String? {
    val normalized = trim()
    return normalized.takeIf {
        it.startsWith("http://", ignoreCase = true) ||
            it.startsWith("https://", ignoreCase = true)
    }
}
