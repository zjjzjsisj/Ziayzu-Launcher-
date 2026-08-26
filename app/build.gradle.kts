plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// ── CI wiring (GitHub Actions passes these) ──────────────────────────
// -PbuildVersionCode  → auto-incrementing build number (from CI run number)
// -PbuildVersionName  → version taken from the release tag (e.g. 1.0.1)
val ciVersionCode = (project.findProperty("buildVersionCode") as String?)?.toIntOrNull() ?: 1
val ciVersionName = (project.findProperty("buildVersionName") as String? ?: "1.0.0")

// ── Release signing (injected by CI from GitHub Secrets) ─────────────
val ksPath = System.getenv("ZIAYZU_KEYSTORE")
val ksPassword = System.getenv("ZIAYZU_KEYSTORE_PASSWORD")
val ksAlias = System.getenv("ZIAYZU_KEY_ALIAS")
val ksKeyPassword = System.getenv("ZIAYZU_KEY_PASSWORD")

android {
    namespace = "com.ziayzu.launcher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ziayzu.launcher"
        minSdk = 26
        targetSdk = 34
        versionCode = ciVersionCode
        versionName = ciVersionName
    }

    signingConfigs {
        if (!ksPath.isNullOrBlank() && !ksPassword.isNullOrBlank() && !ksAlias.isNullOrBlank()) {
            create("release") {
                storeFile = file(ksPath)
                storePassword = ksPassword
                keyAlias = ksAlias
                keyPassword = ksKeyPassword ?: ksPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Real release key when secrets exist; otherwise debug key
            // so the APK is always signed and installable.
            signingConfig = if (signingConfigs.findByName("release") != null) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.09.02"))
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
