plugins {
    id("com.android.application") version "8.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.19" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("androidx.room") version "2.6.1" apply false
    id("org.sonarqube") version "6.0.1.5171"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("org.jetbrains.kotlinx.kover") version "0.9.8" apply false
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

sonar {
    properties {
        property("sonar.projectKey", "ChaikaKotlin")
        property("sonar.projectName", "Chaika Kotlin")
        property("sonar.projectVersion", "0.0.2-alpha")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.log.level", "INFO")
        property("sonar.scm.provider", "git")

        property("sonar.issue.ignore.multicriteria", "fi1,fi2")
        property("sonar.issue.ignore.multicriteria.fi1.ruleKey", "kotlin:S6517")
        property("sonar.issue.ignore.multicriteria.fi1.resourceKey", "**/*")
        property("sonar.issue.ignore.multicriteria.fi2.ruleKey", "kotlin:S6624")
        property("sonar.issue.ignore.multicriteria.fi2.resourceKey", "**/*.gradle.kts")
    }
}

project(":app") {
    sonar {
        properties {
            property("sonar.androidVariant", "stage")
            property("sonar.junit.reportPaths", "build/test-results/testStageUnitTest")
            property("sonar.androidLint.reportPaths", "build/reports/lint-results-stage.xml")
            property(
                "sonar.kotlin.detekt.reportPaths",
                "build/reports/detekt/detekt-sonar.xml"
            )
            property(
                "sonar.coverage.jacoco.xmlReportPaths",
                "build/reports/kover/stage/xml/report.xml"
            )

            property(
                "sonar.exclusions",
                listOf(
                    "**/build/**",
                    "**/generated/**"
                ).joinToString(",")
            )
            property(
                "sonar.coverage.exclusions",
                listOf(
                    "**/test/**",
                    "**/tests/**",
                    "**/di/**",
                    "**/domain/models/**",
                    "**/domain/sealed/**",
                    "**/data/datasource/apiservice/**",
                    "**/data/datasource/dto/**",
                    "**/data/room/dao/**",
                    "**/data/room/entities/**",
                    "**/data/room/relations/**",
                    "**/data/room/sync/**",
                    "**/ui/activities/**",
                    "**/ui/components/**",
                    "**/ui/dto/**",
                    "**/ui/navigation/**",
                    "**/ui/savers/**",
                    "**/ui/screens/**",
                    "**/ui/state/**",
                    "**/ui/theme/**",
                    "**/myApp.kt",
                    "**/MyApp.kt",
                    "**/AppDatabase.kt",
                    "**/Fake*"
                ).joinToString(",")
            )
        }
    }
}

project(":e2e-tests") {
    sonar {
        properties {
            property("sonar.skipProject", true)
        }
    }
}
