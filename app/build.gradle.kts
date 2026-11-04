plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.lifebits.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lifebits.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 4
        versionName = "0.3.1-dev"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
