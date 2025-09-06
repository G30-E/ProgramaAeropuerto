plugins {
    id("com.android.application") version "8.12.2"
}

android {
    namespace = "com.example.aeropuerto"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.aeropuerto"
        minSdk = 21
        targetSdk = 36
        versionCode = 2
        versionName = "1.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    // no external libraries
}