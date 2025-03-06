plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.group1_petfood"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.group1_petfood"
        minSdk = 24
        targetSdk = 35
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Sử dụng BOM để tự động đồng bộ phiên bản Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Firebase Database
    implementation("com.google.firebase:firebase-database")

    // Fix lỗi Firebase App Check
    implementation("com.google.firebase:firebase-appcheck-playintegrity")

    // Google Play Services (cần thiết để Firebase hoạt động)
    implementation("com.google.android.gms:play-services-auth:20.7.0")

}