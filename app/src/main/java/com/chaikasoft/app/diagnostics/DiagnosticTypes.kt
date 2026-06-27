package com.chaikasoft.app.diagnostics

import com.chaikasoft.app.domain.common.AppError

enum class DiagnosticArea(val wireName: String) {
    AUTH("auth"),
    STARTUP("startup"),
    STORAGE("storage"),
    TEMPLATES("templates"),
    PRODUCTS("products"),
    TRIPS("trips"),
    REPORTS("reports")
}

enum class DiagnosticOperation(val wireName: String) {
    AUTHORIZE("authorize"),
    CHECK_AUTH_STATUS("check_auth_status"),
    RESTORE_ENCRYPTED_STORAGE("restore_encrypted_storage"),
    REFRESH_STATIONS("refresh_stations"),
    REFRESH_PRODUCTS("refresh_products"),
    RESOLVE_TEMPLATE_DETAIL("resolve_template_detail"),
    DTO_MAPPING("dto_mapping"),
    DOMAIN_MAPPING("domain_mapping"),
    UNKNOWN("unknown")
}

enum class DiagnosticSeverity(val wireName: String) {
    WARNING("warning"),
    ERROR("error"),
    FATAL("fatal")
}

enum class DiagnosticEventType(val wireName: String) {
    APP_ERROR_UNKNOWN("app_error_unknown"),
    APP_ERROR_SERIALIZATION("app_error_serialization"),
    ENCRYPTED_STORAGE_RECOVERY("encrypted_storage_recovery"),
    POST_AUTH_STARTUP_LOCAL_FAILURE("post_auth_startup_local_failure"),
    DTO_MAPPING_FAILED("dto_mapping_failed"),
    DOMAIN_MAPPING_FAILED("domain_mapping_failed")
}

data class DiagnosticContext(
    val area: DiagnosticArea,
    val operation: DiagnosticOperation = DiagnosticOperation.UNKNOWN
)

data class DiagnosticEvent(
    val type: DiagnosticEventType,
    val area: DiagnosticArea,
    val operation: DiagnosticOperation,
    val severity: DiagnosticSeverity = DiagnosticSeverity.ERROR,
    val throwable: Throwable,
    val tags: Map<String, String> = emptyMap()
) {
    fun safeTags(): Map<String, String> = DiagnosticSanitizer.safeTags(
        tags + mapOf(
            DiagnosticTag.APP_AREA.key to area.wireName,
            DiagnosticTag.OPERATION.key to operation.wireName,
            DiagnosticTag.EVENT_TYPE.key to type.wireName,
            DiagnosticTag.SEVERITY.key to severity.wireName,
            DiagnosticTag.THROWABLE_TYPE.key to DiagnosticSanitizer.safeThrowableType(throwable)
        )
    )
}

enum class DiagnosticTag(val key: String) {
    APP_AREA("app_area"),
    OPERATION("operation"),
    EVENT_TYPE("event_type"),
    SEVERITY("severity"),
    THROWABLE_TYPE("throwable_type"),
    BUILD_TYPE("build_type"),
    APP_VERSION("app_version")
}

interface ErrorReporter {
    fun recordNonFatal(event: DiagnosticEvent)
    fun setContext(context: DiagnosticContext)
    fun clearContext()
}

fun AppError.toDiagnosticEvent(
    area: DiagnosticArea,
    operation: DiagnosticOperation
): DiagnosticEvent? = DiagnosticPolicy.fromAppError(this, area, operation)
