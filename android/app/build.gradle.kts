import java.io.BufferedReader
import java.io.InputStreamReader

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "2.0.0"
}

android {
    namespace = "io.github.winemu"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.winemu"
        minSdk = 26
        targetSdk = 28
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildFeatures {
            viewBinding = true
        }
    }

    val isPr: String? = System.getenv("IS_PR")
    if (isPr != "true") {
        signingConfigs {
            create("release") {
                storeFile = file(System.getenv("ANDROID_KEYSTORE_FILE"))
                storePassword = System.getenv("ANDROID_KEYSTORE_PASS")
                keyAlias = System.getenv("ANDROID_KEY_ALIAS")
                keyPassword = System.getenv("ANDROID_KEYSTORE_PASS")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = if (isPr != "true") { signingConfigs.getByName("release") } else { signingConfigs.getByName("debug") }
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    packagingOptions {
        exclude 'AndroidManifest.xml'
    }
}

dependencies {
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("io.coil-kt.coil3:coil:3.0.2")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.2")
    implementation("com.google.android.material:material:1.10.0")
    implementation("io.ktor:ktor-client-core:2.3.11")
    implementation("io.ktor:ktor-client-cio:2.3.11")
    implementation("io.ktor:ktor-client-okhttp:3.0.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.11")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("io.ktor:ktor-client-json:2.3.1")
    implementation("io.ktor:ktor-client-serialization:2.3.1")
    implementation("org.json:json:20210307")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.palette:palette:1.0.0")
    implementation(files("libs/win-debug.aar"))
}

fun getVersionName(): String {
    var tag = "1.0"
    val githubToken = System.getenv("GITHUB_TOKEN") ?: ""
    try {
        val process = Runtime.getRuntime().exec(arrayOf("bash", "-c", "GH_TOKEN=$githubToken gh release list --limit 1 --json tagName --jq '.[0].tagName'"))
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        tag = reader.readLine().trim()
        if (tag.startsWith("v")) {
            tag = tag.substring(1)
        }
    } catch (e: Exception) {
        println("Failed to get latest GitHub release tag: ${e.message}")
    }
    return tag
}

fun getVersionCode(): Int {
    var versionCode = 1
    val tag = getVersionName()
    if (tag.isNotEmpty() && tag[0].isDigit()) {
        versionCode = tag[0].toString().toInt()
    }
    if (versionCode == 0) {
        versionCode = 1 // return dummy version code if the version code isn't positive
    }
    return versionCode
}
