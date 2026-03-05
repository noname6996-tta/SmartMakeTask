package com.tta.smartmaketask.screen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.tta.smartmaketask.di.appModule
import com.tta.smartmaketask.screen.main.MainScreen
import com.tta.smartmaketask.ui.theme.SmartTaskTheme
import com.tta.smartmaketask.db.DatabaseDriverFactory
import org.koin.compose.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Composable
fun App(
    databaseDriverFactory: DatabaseDriverFactory
) {
    // Initialize Koin only if not already started
    KoinApplication(application = {
        modules(
            appModule,
            module {
                single { databaseDriverFactory }
            }
        )
    }) {

    }

    val systemDark = isSystemInDarkTheme()
    var isDarkMode by remember { mutableStateOf(systemDark) }

    SmartTaskTheme(darkTheme = isDarkMode) {
        MainScreen(
            isDarkMode = isDarkMode,
            onToggleDarkMode = { isDarkMode = it }
        )
    }
}