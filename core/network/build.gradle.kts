plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.network"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(project(":core:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Network
    implementation(libs.retrofit)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.kotlinx.serialization)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Gson
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
}