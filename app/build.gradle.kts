import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)
    id("kotlin-kapt")
    id("com.google.android.gms.oss-licenses-plugin")
}

// retrieve versionCode and versionName from the last commit-TAG
val gitVersion = getAppGitVersion().ifEmpty { "v0.0.0" }
val versionCodeValue = getAppVersionCode(gitVersion)
val versionNameValue = getAppVersionNameValue(gitVersion)

android {
    namespace = "com.jumparoundcreations.mva_sugarcounter"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jumparoundcreations.mva_sugarcounter"
        minSdk = 28
        targetSdk = 34
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
}

tasks.register("writeVersionInfo") {
    doLast {
        val versionCode = android.defaultConfig.versionCode
        val versionName = android.defaultConfig.versionName
        val versionInfoFile = file("${buildDir}/version-info.txt")
        versionInfoFile.parentFile.mkdirs()
        versionInfoFile.writeText("versionCode=$versionCode\nversionName=$versionName")
    }
}
tasks.named("preBuild") {
    dependsOn("writeVersionInfo")
}

fun getAppGitVersion(): String {
    println("ABC")
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "describe", "--tags", "--long", "--always")
        standardOutput = stdout
    }
    val gitVersion = stdout.toString().trim()
    println("git Version XYZ:$gitVersion")
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


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Material Icons
    implementation("androidx.compose.material:material-icons-extended")

    //Room

    implementation("androidx.room:room-common:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    //ThirdParty Libraries
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")

    //Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    //MockK
    testImplementation("io.mockk:mockk:1.13.12")

    //Third-Party Licenses
    implementation("com.google.android.gms:play-services-oss-licenses:17.1.0")
    implementation("androidx.appcompat:appcompat:1.7.0")

    //DI
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    testImplementation(libs.koin.test.junit4)
    implementation("androidx.compose.runtime:runtime:1.6.8")

    //Google Code Scanner
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")

    //WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    //Onboarding
    implementation("de.sldw:compose-onboarding:0.0.2-0")
}