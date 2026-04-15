@file:Suppress("DEPRECATION")

import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.dagger.hilt.android")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "edu.ucne.atlaspath"
    compileSdk = 36

    defaultConfig {
        applicationId = "edu.ucne.atlaspath"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "EXERCISE_DB_KEY", "\"${localProperties.getProperty("EXERCISE_DB_KEY") ?: ""}\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties.getProperty("GEMINI_API_KEY") ?: ""}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)
    implementation(libs.androidx.navigation.compose.v280rc01)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.benchmark.traceprocessor)
    implementation(libs.androidx.camera.core)
    implementation(libs.material3)
    implementation(libs.androidx.hilt.work)
    implementation(libs.firebase.components)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.generativeai)
    implementation(libs.androidx.adapters)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.compose.ui)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation (libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.retrofit)
    implementation (libs.squareup.moshi.kotlin)
    implementation(libs.converter.moshi)
    implementation (libs.logging.interceptor)
    implementation(libs.androidx.compose.material3.v150alpha08)
    implementation  (libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.datastore.preferences)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.genai.prompt)
    implementation(libs.play.services.tasks)
    implementation(libs.moshi)
    implementation(libs.coil.compose)
    implementation(libs.coil.compose.v260)
    implementation(libs.coil.gif)
    implementation(libs.capturable)
    // Dependencias para Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}