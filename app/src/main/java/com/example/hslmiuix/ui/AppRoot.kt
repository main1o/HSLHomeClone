package com.example.hslmiuix.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.example.hslmiuix.data.DeviceCategory
import com.example.hslmiuix.data.Store
import com.example.hslmiuix.ui.device.DeviceScreen
import com.example.hslmiuix.ui.history.HistoryScreen
import com.example.hslmiuix.ui.home.HomeScreen
import com.example.hslmiuix.ui.login.LoginScreen
import com.example.hslmiuix.ui.mine.MineScreen
import com.example.hslmiuix.ui.tasks.TasksScreen

/** 页面路由（手写栈，不引入导航库） */
sealed interface Screen {
    data object Home : Screen
    data object Mine : Screen
    data object Tasks : Screen
    data object History : Screen
    data class Device(val category: DeviceCategory) : Screen
}

@Composable
fun AppRoot() {
    AppTheme(appearance = Store.appearance) {
        if (!Store.loggedIn) {
            LoginScreen()
            return@AppTheme
        }

        val stack = remember { mutableStateListOf<Screen>(Screen.Home) }

        fun pop() {
            if (stack.size > 1) stack.removeAt(stack.lastIndex)
        }

        BackHandler(enabled = stack.size > 1) { pop() }

        when (val current = stack.last()) {
            Screen.Home -> HomeScreen(
                onOpenDevice = { stack.add(Screen.Device(it)) },
                onOpenMine = { stack.add(Screen.Mine) },
                onOpenTasks = { stack.add(Screen.Tasks) },
            )

            Screen.Mine -> MineScreen(
                onBack = { pop() },
                onOpenTasks = { stack.add(Screen.Tasks) },
                onOpenHistory = { stack.add(Screen.History) },
            )

            Screen.Tasks -> TasksScreen(onBack = { pop() })

            Screen.History -> HistoryScreen(onBack = { pop() })

            is Screen.Device -> DeviceScreen(
                category = current.category,
                onBack = { pop() },
            )
        }
    }
}
