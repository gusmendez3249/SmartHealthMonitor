plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}
 
android {
    namespace = "mx.utng.smarthealthmonitor.tv"
    compileSdk = 35
    defaultConfig {
        applicationId = "mx.utng.smarthealthmonitor.tv"
        minSdk  = 26
        targetSdk = 35
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}
 
dependencies {
    // Leanback Library — el estándar de Android TV
    implementation("androidx.leanback:leanback:1.2.0")
    // Glide para cargar imágenes en las cards
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // ViewModel + Coroutines
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.fragment:fragment-ktx:1.8.3")
    
    // Media3 + ExoPlayer para Android TV
    val media3Version = "1.4.1"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")
    implementation("androidx.leanback:leanback-preference:1.2.0")
    // Corrección para Media3: Usamos media3-ui-leanback en lugar de exoplayer-leanback (v2) para evitar conflictos
    implementation("androidx.media3:media3-ui-leanback:$media3Version")
}
