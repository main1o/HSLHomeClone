plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

// 注意：AGP 9.0+ 已内置 Kotlin 支持，不能再应用 org.jetbrains.kotlin.android 插件，
// 否则构建期直接报错（See https://kotl.in/gradle/agp-built-in-kotlin）。
// JVM 目标由下方 compileOptions 与 JDK 21 launcher 决定。

android {
    namespace = "com.example.hslmiuix"
    buildToolsVersion = "37.0.0"
    compileSdk {
        version = release(37) {
            minorApiLevel = 0
        }
    }

    defaultConfig {
        applicationId = "com.example.hslmiuix"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose Multiplatform 基础运行时（Android 变体映射到 androidx.compose）
    implementation("org.jetbrains.compose.foundation:foundation:1.12.0")
    implementation("org.jetbrains.compose.ui:ui:1.12.0")
    implementation("org.jetbrains.compose.components:components-resources:1.12.0")

    implementation("androidx.activity:activity-compose:1.13.0")

    // Miuix（HyperOS/MIUI 风格 Compose UI 库）
    implementation("top.yukonga.miuix.kmp:miuix-ui:0.9.4")
    implementation("top.yukonga.miuix.kmp:miuix-preference:0.9.4")
    implementation("top.yukonga.miuix.kmp:miuix-icons:0.9.4")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
}
