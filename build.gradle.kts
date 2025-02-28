plugins {
    id("com.android.application") version "8.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.19" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("org.sonarqube") version "6.0.1.5171"
    id("io.gitlab.arturbosch.detekt") version "1.23.6" apply false
    id("jacoco")
}

sonar {
    properties {
        property("sonar.projectKey", "ChaikaKotlin")
        property("sonar.projectName", "ChaikaKotlin")
    }
}
