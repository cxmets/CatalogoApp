plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.comets.catalogo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.comets.catalogo"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(platform(libs.compose.bom))

    implementation(libs.ui.graphics)
    implementation(libs.ui.util)
    implementation(libs.androidx.ui.text)
    implementation(libs.foundation)
    implementation(libs.material3)
    implementation(libs.material.icons.extended)
    implementation(libs.activity.compose)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Lifecycle
    implementation(libs.lifecycle.runtime.ktx)

    // Core KTX
    implementation(libs.androidx.core.ktx)

    implementation(libs.accompanist.systemuicontroller)

    implementation(libs.coil.compose)

    // Dependências de Teste
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(libs.ui.test.junit4)

    // Dependências de Debug
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.test.manifest)
}