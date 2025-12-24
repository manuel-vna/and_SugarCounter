import java.util.regex.Pattern

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
    id("kotlin-kapt")
    id("com.mikepenz.aboutlibraries.plugin")
    id("androidx.room")
    id("de.mannodermaus.android-junit5")
}

// retrieve versionCode and versionName from the last commit-TAG
val gitVersion = getAppGitVersion().ifEmpty { "v0.0.0" }
val versionCodeValue = getAppVersionCode(gitVersion)
val versionNameValue = getAppVersionNameValue(gitVersion)

android {
    namespace = "com.jumparoundcreations.mva_sugarcounter"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jumparoundcreations.mva_sugarcounter"
        minSdk = 28
        targetSdk = 36
        versionCode = versionCodeValue // e.g. 1
        versionName = versionNameValue // e.g. "v0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }

    }

    signingConfigs {
        create("release") {
            keyAlias = System.getenv("SUGAR_COUNTER_KEY_ALIAS") ?: "debug"
            keyPassword = System.getenv("SUGAR_COUNTER_KEY") ?: "password"
            storeFile = file(System.getenv("SUGAR_COUNTER_KEY_FILE") ?: "debug.keystore")
            storePassword = System.getenv("RELEASE_PLAY_CONSOLE_MVA_KEYSTORE") ?: "password"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlin {
        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
    sourceSets {
        // Adds exported Room schema location as test app assets.
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }
}

// Ensure the folder exists
tasks.register("ensureSchemaFolder") {
    doLast {
        val schemaDir = file("$projectDir/schemas")
        if (!schemaDir.exists()) {
            schemaDir.mkdirs()
        }
    }
}

fun getAppGitVersion(): String {
    // ProviderFactory.exec() is from Gradle 8.12+ the recommended Gradle API for executing shell commands within a Gradle build script.
    val gitVersion = providers.exec {
        commandLine("git", "describe", "--tags", "--long", "--always")
    }.standardOutput.asText.get().trim()
    println("Git version: $gitVersion")
    return gitVersion
}

fun getAppVersionCode(gitVersion: String): Int {
    var versionCode = "0"
    val pattern = Pattern.compile("v.*_([0-9]+)-.*")
    val matcher = pattern.matcher(gitVersion)
    if (matcher.matches()) {
        versionCode = matcher.group(1)
    }
    println("Version code: $versionCode")
    return versionCode.toInt()
}

fun getAppVersionNameValue(gitVersion: String): String {
    var versionName = "v0.0.0"
    val pattern = Pattern.compile("(v[0-9.]+)_.*")
    val matcher = pattern.matcher(gitVersion)
    if (matcher.matches()) {
        versionName = matcher.group(1)
    }
    println("Version name: $versionName")
    return versionName
}

//noinspection UseTomlInstead
dependencies {

    implementation(libs.androidx.material3.window.size.class1.android)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.truth)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.mockk.android)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose.core)
    implementation(libs.aboutlibraries.compose.m3)
    implementation(libs.accompanist.webview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.icons)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.app.cash.turbine)
    implementation(libs.bundles.room)
    implementation(libs.androidx.work)
    implementation(libs.github.vanpra.datetime)
    implementation(libs.gms.scanner)
    implementation(libs.google.accompanist)
    implementation(libs.jetbrans.kotlinx)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.material)
    implementation(libs.sldw.onboarding)
    implementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.androidx.work.testing)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.mockk)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junitFive.android.core)
    testRuntimeOnly(libs.junitFive.test.runner)
    testRuntimeOnly(libs.junit.vintage.engine) // for usage of junit4 along junit5, remove when no more junit4 tests exist
    testRuntimeOnly(libs.junit.jupiter.engine)
    ksp(libs.androidx.room.compiler)

}