plugins {
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.chaika.e2eTests"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34
        testInstrumentationRunner = "com.example.chaika.KaspressoHiltTestRunner"
    }

    targetProjectPath = ":app"

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin")
            manifest.srcFile("src/main/AndroidManifest.xml")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    testOptions {
        animationsDisabled = true
    }
}

dependencies {
    // подключаем приложение под тестом
    implementation(
        project(
            path = ":app",
            configuration = "debugRuntimeElements",
        ),
    )

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-android-compiler:2.48")
    implementation("com.google.dagger:hilt-android-testing:2.48")

    // Core AndroidX Test
    implementation("androidx.test:core:1.6.1")
    implementation("androidx.test.ext:junit:1.1.5")
    implementation("androidx.test.espresso:espresso-core:3.6.1")

    // Compose UI tests
    implementation("androidx.compose.ui:ui-test-junit4:1.5.11")

    // Kaspresso + Compose-support
    implementation("com.kaspersky.android-components:kaspresso:1.6.0")
    implementation("com.kaspersky.android-components:kaspresso-compose-support:1.6.0")
    implementation("androidx.compose.ui:ui-test-junit4:1.5.11")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.11")
    implementation("androidx.tracing:tracing:1.3.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
