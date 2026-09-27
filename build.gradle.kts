plugins {
    id("com.android.application") version "9.4.1" apply false
    // AGP 9 内置 Kotlin 支持，无需（也不允许）再应用 org.jetbrains.kotlin.android
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.20" apply false
}
