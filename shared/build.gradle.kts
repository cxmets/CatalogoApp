import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinPluginSerialization) // Usando alias do TOML
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting { // Renomeado para val para clareza
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                // Dependências do Compose Multiplatform serão adicionadas aqui quando movermos a UI
            }
        }
        val commonTest by getting { // Renomeado para val
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val androidMain by getting
    }
}

android {
    namespace = "com.comets.catalogo" // Seu namespace para a lib android do shared
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    // Adicionar buildFeatures se for usar DataBinding, ViewBinding, etc. (não necessário para Compose puro)
    // buildFeatures {
    //     compose = true // Se for usar compose direto no shared/androidMain UI (menos comum)
    // }
    // O plugin do compose compiler é geralmente aplicado no nível do projeto ou no módulo que usa UI Compose
    // Para Compose Multiplatform, há um plugin org.jetbrains.compose específico.
}