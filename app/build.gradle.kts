//import java.io.FileInputStream
//import java.util.Properties
//
//val localProperties = Properties()
//val localPropertiesFile = rootProject.file("local.properties")
//
//if (localPropertiesFile.exists()) {
//    localProperties.load(FileInputStream(localPropertiesFile))
//}
//
//val serverClientId: String = localProperties.getProperty("server_client_id", "")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Safe Args from https://developer.android.com/jetpack/androidx/releases/navigation#kts
    id("androidx.navigation.safeargs.kotlin")
    // https://developer.android.com/training/dependency-injection/hilt-android
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.noteappfirebase"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.noteappfirebase"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
//        debug {
//            buildConfigField("String", "SERVER_CLIENT_ID", "\"${serverClientId}\"")
//        }
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
        viewBinding = true
//        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // https://developer.android.com/training/dependency-injection/hilt-android
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // https://github.com/bumptech/glide
    implementation(libs.glide)
}