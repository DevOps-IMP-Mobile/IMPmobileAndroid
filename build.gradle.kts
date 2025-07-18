// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    //alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false // KSP 플러그인 추가
    alias(libs.plugins.hilt) apply false // Hilt 플러그인 추가
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.kotlin.serialization) apply false // Serialization 플러그인 추가
}