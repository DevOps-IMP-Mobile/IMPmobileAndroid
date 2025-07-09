plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // alias(libs.plugins.hilt)  <- 이 줄 주석처리
}

android {
    namespace = "com.example.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 35
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // 일단 기본만
    // implementation(libs.retrofit)
    // implementation(libs.retrofit.kotlinx.serialization)
    // implementation(libs.okhttp.logging)
    // implementation(libs.hilt.android)
}