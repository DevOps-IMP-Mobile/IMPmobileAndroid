plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp) // KSP 추가
    alias(libs.plugins.hilt) // Hilt 추가
    alias(libs.plugins.google.services) // Google Services
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    lint {
        disable += "NullSafeMutableLiveData"
        abortOnError = false
    }
    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"  // 수동 설정
    }
    lint {
        disable += setOf(
            "NullSafeMutableLiveData",
            "UnusedResources",
            "IconMissingDensityFolder"
        )
        abortOnError = false
        checkReleaseBuilds = false

    }

    dependencies {
        // SplashScreen API
        implementation("androidx.core:core-splashscreen:1.0.1")

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3)

        // Hilt
        implementation(libs.hilt.android)
        ksp(libs.hilt.compiler)
        implementation(libs.hilt.navigation.compose)

        // Navigation
        implementation(libs.androidx.navigation.compose)

        // Lifecycle
        implementation(libs.androidx.lifecycle.viewmodel.compose)

        // Coroutines
        implementation(libs.kotlinx.coroutines.android)

        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)

        // 모듈 의존성 추가
        implementation(project(":core:ui"))
        implementation(project(":core:data")) // data 모듈 추가
        implementation(project(":feature:login"))
        implementation(project(":feature:home"))
        implementation(project(":feature:project"))
        implementation(project(":feature:issue"))
        implementation(project(":feature:profile"))

        // firebase BOM
        implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
        implementation("com.google.firebase:firebase-analytics")
    }
}