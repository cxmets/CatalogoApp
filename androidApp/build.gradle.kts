plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler) // Plugin do compilador Compose para Android
}

android {
    namespace = "com.comets.catalogoappkmp.android" // Namespace que você definiu no wizard
    compileSdk = 35

    defaultConfig {
        applicationId = "com.comets.catalogoappkmp.android" // Mantenha o Application ID do seu KMP app
        minSdk = 24
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
            // Adicione seus proguardFiles aqui se necessário para release
            // proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8 // Consistente com shared module
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8" // Consistente com shared module
    }
}

dependencies {
    implementation(project(":shared")) // Dependência crucial do módulo compartilhado

    // Compose (usando o Bill of Materials - BOM)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.androidx.material.icons.extended) // Ícones
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest) // Para testes de UI, se necessário

    // Activity & Lifecycle & ViewModel
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Core KTX
    implementation(libs.androidx.core.ktx)

    // Accompanist (se ainda necessário e não substituído por APIs do Compose Material 3)
    implementation(libs.accompanist.systemuicontroller)

    // Coil
    implementation(libs.coil.compose)

    // UI Util (se usado diretamente no androidApp)
    implementation(libs.androidx.ui.util)

    // Coroutines para Android (se usar Dispatchers.Main especificamente, ou para lifecycleScope)
    implementation(libs.kotlinx.coroutines.android)
}