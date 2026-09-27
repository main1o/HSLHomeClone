package com.example.hslmiuix.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

/**
 * Miuix 主题包装：0 跟随系统 / 1 浅色 / 2 深色
 */
@Composable
fun AppTheme(
    appearance: Int = 0,
    content: @Composable () -> Unit,
) {
    val dark = when (appearance) {
        1 -> false
        2 -> true
        else -> isSystemInDarkTheme()
    }
    MiuixTheme(
        colors = if (dark) darkColorScheme() else lightColorScheme(),
        content = content,
    )
}
