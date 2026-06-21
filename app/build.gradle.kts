plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.yourname.aimagiceditor"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yourname.aimagiceditor"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.6"
    }
}

dependencies {
    // Basic Android Needs
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // UI Framework (Your Code)
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")

    // Video Playback & Media Tools (Your Code)
    implementation("androidx.media3:media3-exoplayer:1.2.0")
    implementation("androidx.media3:media3-ui:1.2.0")

    // The "Magic" Engines (Your Code)
    implementation("com.arthenica:ffmpeg-kit-full-gpl:4.5.1-1") // Fixed cloud link
    implementation("com.google.mediapipe:tasks-vision:0.10.0")     
}
