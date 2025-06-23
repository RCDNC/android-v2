plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.rcdnc.cafezinho.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.rcdnc.cafezinho.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }


}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.navigation.compose.android)
    debugImplementation(libs.compose.ui.tooling)

    // Play Services
    implementation(libs.play.services.location)
    implementation(libs.places)
    implementation(libs.play.services.places)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.google.api.client)
    implementation(libs.play.services.ads)
    implementation(libs.play.services.gcm)
    implementation(libs.app.update)
    implementation(libs.review)
    implementation(libs.androidx.recyclerview)
    implementation(libs.play.services.auth)


}