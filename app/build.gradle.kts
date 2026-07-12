import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

android {
    namespace = "edu.mx.cmjg.smarthealthmonitor"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "edu.mx.cmjg.smarthealthmonitor"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildFeatures {
            buildConfig = true
        }

        buildConfigField("String", "MQTT_BROKER_URL", "\"${localProperties.getProperty("MQTT_BROKER_URL")}\"")
        buildConfigField("int", "MQTT_PORT", "${localProperties.getProperty("MQTT_PORT")}")
        buildConfigField("String", "MQTT_USERNAME", "\"${localProperties.getProperty("MQTT_USERNAME")}\"")
        buildConfigField("String", "MQTT_PASSWORD", "\"${localProperties.getProperty("MQTT_PASSWORD")}\"")
        buildConfigField("String", "NEON_API_KEY", "\"${localProperties.getProperty("NEON_API_KEY")}\"")
        buildConfigField("String", "NEON_HOST", "\"${localProperties.getProperty("NEON_HOST")}\"")
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
}

dependencies {
    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("com.google.android.gms:play-services-wearable:18.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.material.icons.extended)

    // MediaRouter — el UI del botón de Cast
    implementation("androidx.mediarouter:mediarouter:1.7.0")
    // Cast Framework — el protocolo de comunicación
    implementation("com.google.android.gms:play-services-cast-framework:21.5.0")
    // AppCompat necesario para el tema del botón de Cast
    implementation("androidx.appcompat:appcompat:1.6.1")
    
    // Eclipse Paho MQTT para Android
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")
    // Kotlinx Serialization para JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Retrofit + OkHttp para llamadas a Neon HTTP API
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // WorkManager para sync periódico en background
    implementation("androidx.work:work-runtime-ktx:2.9.1")
}