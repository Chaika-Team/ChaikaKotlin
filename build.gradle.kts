plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.19" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("org.sonarqube") version "6.0.1.5171"
}
    
sonar {
  properties {
    property("sonar.projectKey", "ChaikaKotlin")
    property("sonar.projectName", "ChaikaKotlin")
  }
}
