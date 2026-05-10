import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.chaikasoft.app.e2e"
    compileSdk = 34
    targetProjectPath = ":app"

    defaultConfig {
        minSdk = 26
        targetSdk = 34
        testInstrumentationRunner = "com.chaikasoft.app.e2e.HiltE2ETestRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += setOf("META-INF/LICENSE.md")
        }
    }

    testOptions {
        animationsDisabled = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"

        managedDevices {
            devices {
                create<ManagedVirtualDevice>("pixel6Api34") {
                    device = "Pixel 6"
                    apiLevel = 34
                    systemImageSource = "aosp"
                }
                create<ManagedVirtualDevice>("pixelTabletApi34") {
                    device = "Pixel Tablet"
                    apiLevel = 34
                    systemImageSource = "aosp"
                }
            }
            groups {
                create("pr") {
                    targetDevices.add(devices["pixel6Api34"])
                }
                create("nightly") {
                    targetDevices.add(devices["pixel6Api34"])
                    targetDevices.add(devices["pixelTabletApi34"])
                }
            }
        }
    }
}

dependencies {
    // подключаем приложение под тестом
    implementation(
        project(
            path = ":app",
            configuration = "debugRuntimeElements"
        )
    )

    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.23"))
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))

    // Explicit compile classpath for IDE stability in com.android.test module.
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("net.openid:appauth:0.10.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("androidx.test:core:1.6.1")
    implementation("androidx.test:runner:1.6.2")
    implementation("androidx.test:rules:1.6.1")
    implementation("androidx.test.ext:junit:1.2.1")
    implementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("androidx.test.uiautomator:uiautomator:2.3.0")

    implementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("com.google.dagger:hilt-android:2.48")
    implementation("com.google.dagger:hilt-android-testing:2.48")
    ksp("com.google.dagger:hilt-android-compiler:2.48")

    androidTestUtil("androidx.test:orchestrator:1.5.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
