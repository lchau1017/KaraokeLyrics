plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.karaokelyrics.app"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.karaokelyrics.app"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
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

dependencies {
    // Kyrics Library
    implementation(libs.kyrics)

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.lifecycle)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.guava)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Media3
    implementation(libs.bundles.media3)

    // Utils
    implementation(libs.timber)

    // Testing - Unit Tests
    testImplementation(libs.bundles.testing.unit)
    testImplementation(libs.androidx.arch.core.testing)

    // Testing - Hilt
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)

    // Testing - Android/Instrumented Tests
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.testing.android)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    // Debug
    debugImplementation(libs.bundles.compose.debug)
}
