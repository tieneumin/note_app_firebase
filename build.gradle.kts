// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        // Safe Args from https://developer.android.com/jetpack/androidx/releases/navigation#kts
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // https://developer.android.com/training/dependency-injection/hilt-android
    id("com.google.dagger.hilt.android") version "2.55" apply false
}