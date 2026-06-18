plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("androidx.room")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.kotlinx.kover")
    id("de.mannodermaus.android-junit5") version "1.12.0.0"
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    namespace = "com.chaikasoft.app"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }

    androidResources {
        generateLocaleConfig = true
    }

    defaultConfig {
        applicationId = "com.chaikasoft.app"
        minSdk = 26
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 1
        versionName = "1.2.0"

        manifestPlaceholders["appAuthRedirectScheme"] = "com.chaikasoft.app"
        testInstrumentationRunner = "com.chaikasoft.app.HiltTestRunner"
    }

    lint {
        abortOnError = false
        xmlReport = true
        htmlReport = true
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            buildConfigField("String", "CLIENT_ID", "\"${System.getenv("REL_ZITADEL_TOKEN")}\"")
            buildConfigField(
                "String",
                "CHAIKA_SOFT_URL",
                "\"${System.getenv("REL_CHAIKA_SOFT_URL")}\""
            )
            buildConfigField("String", "ZITADEL_URL", "\"${System.getenv("REL_ZITADEL_URL")}\"")
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "CLIENT_ID", "\"${System.getenv("REL_ZITADEL_TOKEN")}\"")
            buildConfigField(
                "String",
                "CHAIKA_SOFT_URL",
                "\"${System.getenv("REL_CHAIKA_SOFT_URL")}\""
            )
            buildConfigField("String", "ZITADEL_URL", "\"${System.getenv("REL_ZITADEL_URL")}\"")
            signingConfig = signingConfigs.getByName("debug")
        }

        create("stage") {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-STAGING"
            buildConfigField("String", "CLIENT_ID", "\"${System.getenv("STAGE_ZITADEL_TOKEN")}\"")
            buildConfigField(
                "String",
                "CHAIKA_SOFT_URL",
                "\"${System.getenv("STAGE_CHAIKA_SOFT_URL")}\""
            )
            buildConfigField("String", "ZITADEL_URL", "\"${System.getenv("STAGE_ZITADEL_URL")}\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        animationsDisabled = true
        unitTests {
            isReturnDefaultValues = true
        }
    }

    sourceSets {
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }

    packaging {
        resources {
            excludes += setOf("META-INF/LICENSE.md")
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kover {
    reports {
        variant("stage") {
            filters {
                excludes {
                    classes(
                        "*.R",
                        "*.R$*",
                        "*.BuildConfig",
                        "*.Manifest",
                        "*.Manifest$*",
                        "*Test*",
                        "android.*",
                        "*.databinding.*",
                        "*Binding",
                        "*BindingImpl",
                        "*JsonAdapter*",
                        "*_Factory*",
                        "*_MembersInjector*",
                        "*_Impl",
                        "*Dao_Impl*",
                        "*Database_Impl*",
                        "*GeneratedInjector*",
                        "*.Hilt_*",
                        "*_HiltModules*",
                        "dagger.hilt.*",
                        "hilt_aggregated_deps.*",
                        "com.chaikasoft.app.MyApp",
                        "com.chaikasoft.app.AppDatabase",
                        "com.chaikasoft.app.di.*",
                        "com.chaikasoft.app.domain.models.*",
                        "com.chaikasoft.app.domain.sealed.*",
                        "com.chaikasoft.app.data.datasource.apiservice.*",
                        "com.chaikasoft.app.data.datasource.dto.*",
                        "com.chaikasoft.app.data.room.dao.*",
                        "com.chaikasoft.app.data.room.entities.*",
                        "com.chaikasoft.app.data.room.relations.*",
                        "com.chaikasoft.app.data.room.sync.*",
                        "com.chaikasoft.app.ui.activities.*",
                        "com.chaikasoft.app.ui.components.*",
                        "com.chaikasoft.app.ui.dto.*",
                        "com.chaikasoft.app.ui.navigation.*",
                        "com.chaikasoft.app.ui.savers.*",
                        "com.chaikasoft.app.ui.screens.*",
                        "com.chaikasoft.app.ui.state.*",
                        "com.chaikasoft.app.ui.theme.*"
                    )
                }
            }
            xml {
                xmlFile =
                    layout.buildDirectory.file("reports/kover/stage/xml/report.xml").get().asFile
            }
            html {
                htmlDir = layout.buildDirectory.dir("reports/kover/stage/html").get().asFile
            }
        }
    }
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.23"))
    testImplementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.23"))
    androidTestImplementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.23"))

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.7")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.9")

    // AppAuth
    implementation("net.openid:appauth:0.10.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    implementation("com.google.code.gson:gson:2.11.0")

    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    implementation("com.squareup.moshi:moshi:1.13.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.13.0")

    implementation("com.github.bumptech.glide:glide:4.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    ksp("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.3.0")

    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("androidx.test:core:1.7.0")
    testImplementation("androidx.test:runner:1.7.0")
    testImplementation("androidx.test:rules:1.7.0")

    testImplementation("org.robolectric:robolectric:4.14")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.12.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.12.0")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.12.0")
    testImplementation("tech.apter.junit5.jupiter:robolectric-extension:0.9.0")
    // Kotest
    val kotestVersion = "5.9.1"
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    // Mocking
    testImplementation("io.mockk:mockk:1.13.11")
    testImplementation("org.mockito:mockito-core:4.5.1")
    testImplementation("org.mockito:mockito-inline:4.5.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.5.1")
    // Coroutines / Flow
    val coroutinesVersion = "1.8.1"
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("app.cash.turbine:turbine:1.1.0")

    androidTestImplementation("org.junit.jupiter:junit-jupiter-api:5.12.0")
    androidTestRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.12.0")
//    // Если нужны параметризованные тесты в androidTest
//    androidTestImplementation("org.junit.jupiter:junit-jupiter-params:5.12.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")

    androidTestImplementation("org.mockito:mockito-android:4.5.1")

    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")
    androidTestImplementation("androidx.room:room-testing:$roomVersion")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("androidx.paging:paging-runtime-ktx:3.3.6")
    testImplementation("androidx.paging:paging-testing:3.3.6")
    androidTestImplementation("androidx.paging:paging-testing:3.3.6")

    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

    val lifecycleVersion = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    // Hilt dependencies
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-compiler:2.48")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
    kspAndroidTest("com.google.dagger:hilt-compiler:2.48")
    testImplementation("com.google.dagger:hilt-android-testing:2.48")
    kspTest("com.google.dagger:hilt-compiler:2.48")

    // Базовые зависимости Compose (обязательные)
    implementation("androidx.activity:activity-compose:1.8.0")
    // BOM для согласованных версий
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))

    // Material 3 (основная тема)
    implementation("androidx.compose.material3:material3")

    // Опциональные компоненты (добавлять по необходимости)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Дополнительные модули (если используются)
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.paging:paging-compose:3.3.0") // or 3.2.1
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation("androidx.datastore:datastore-preferences:1.2.1")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    ksp("com.google.dagger:hilt-android-compiler:2.48")
    implementation("androidx.constraintlayout:constraintlayout-compose-android:1.1.1")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

junitPlatform {
    instrumentationTests {
        includeExtensions.set(true)
    }
}

ktlint {
    android.set(true)
    verbose.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    enableExperimentalRules.set(false)

    filter {
        exclude { it.file.path.contains("generated") }
        exclude { it.file.path.contains("src/androidTest") }
        exclude { it.file.path.contains("src/test") }
    }

    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
    }
}

tasks.named("preBuild") {
    dependsOn(rootProject.tasks.named("addKtlintFormatGitPreCommitHook"))
}
