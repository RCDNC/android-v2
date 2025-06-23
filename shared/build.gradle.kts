plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "21"
            }
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
<<<<<<< Updated upstream
                // Coroutines - Updated
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
                
                // Serialization - Compatible with Kotlin 1.9.25
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                
                // Ktor Client - Updated but compatible
                implementation("io.ktor:ktor-client-core:2.3.12")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
                implementation("io.ktor:ktor-client-logging:2.3.12")
=======
                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                
                // Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
                
                // Ktor Client
                implementation("io.ktor:ktor-client-core:3.0.2")
                implementation("io.ktor:ktor-client-content-negotiation:3.0.2")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.2")
                implementation("io.ktor:ktor-client-logging:3.0.2")
>>>>>>> Stashed changes
                
                // DateTime - Updated
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
<<<<<<< Updated upstream
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
=======
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
>>>>>>> Stashed changes
            }
        }
        
        val androidMain by getting {
            dependencies {
<<<<<<< Updated upstream
                implementation("io.ktor:ktor-client-android:2.3.12")
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
=======
                implementation("io.ktor:ktor-client-android:3.0.2")
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
>>>>>>> Stashed changes
            }
        }
        
        
    }
}

android {
    namespace = "com.rcdnc.cafezinho.shared"
    compileSdk = 35
    
    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        create("staging") {
            isMinifyEnabled = false
            initWith(getByName("debug"))
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}