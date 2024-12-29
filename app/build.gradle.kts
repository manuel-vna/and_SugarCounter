import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)
    id("kotlin-kapt")
    id("com.google.android.gms.oss-licenses-plugin")
    id("androidx.room")
}

// retrieve versionCode and versionName from the last commit-TAG
val gitVersion = getAppGitVersion().ifEmpty { "v0.0.0" }
val versionCodeValue = getAppVersionCode(gitVersion)
val versionNameValue = getAppVersionNameValue(gitVersion)

android {
    namespace = "com.jumparoundcreations.mva_sugarcounter"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jumparoundcreations.mva_sugarcounter"
        minSdk = 28
        targetSdk = 35
        versionCode = versionCodeValue // e.g. 1
        versionName = versionNameValue // e.g. "v0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
}


fun getAppGitVersion(): String {
    println("ABC")
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "describe", "--tags", "--long", "--always")
        standardOutput = stdout
    }
    val gitVersion = stdout.toString().trim()
    println("Print git Version:$gitVersion")
    return gitVersion
}

fun getAppVersionCode(gitVersion: String): Int {
    var versionCode = ""
    val pattern = Pattern.compile("v.*_([0-9]+)-.*")
    val matcher = pattern.matcher(gitVersion)
    if (matcher.matches()) {
        versionCode = matcher.group(1)
    }
    return versionCode.toInt()
}

fun getAppVersionNameValue(gitVersion: String): String {
    var versionName = ""
    val pattern = Pattern.compile("(v[0-9.]+)_.*")
    val matcher = pattern.matcher(gitVersion)
    if (matcher.matches()) {
        versionName = matcher.group(1)
    }
    return versionName
}

//noinspection UseTomlInstead
dependencies {

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.truth)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.android.gms)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.icons)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.work)
    implementation(libs.app.cash.turbine)
    implementation(libs.bundles.room)
    implementation(libs.github.vanpra.datetime)
    implementation(libs.gms.scanner)
    implementation(libs.google.accompanist)
    implementation(libs.jetbrans.kotlinx)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.material)
    implementation(libs.sldw.onboarding)
    implementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.junit)
    testImplementation(libs.koin.test.junit4)

    kapt("androidx.room:room-compiler:2.6.1") // kapt needs to be replaced with ksp

    //implementation(libs.mockk) // activating this versionCatalog reference leads to a build error
    testImplementation("io.mockk:mockk:1.13.12") // so the old way is still used here

}