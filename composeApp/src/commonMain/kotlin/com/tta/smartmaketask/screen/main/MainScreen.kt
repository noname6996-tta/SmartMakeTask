package com.tta.smartmaketask.screen.main

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.rememberNavBackStack
import com.tta.smartmaketask.screen.main.daily.DailyTaskScreen
import com.tta.smartmaketask.screen.main.daily.DailyTaskViewModel
import com.tta.smartmaketask.screen.main.goal.GoalTaskScreen
import com.tta.smartmaketask.screen.main.goal.GoalTaskViewModel
import com.tta.smartmaketask.screen.main.home.HomeScreen
import com.tta.smartmaketask.screen.main.home.HomeViewModel
import com.tta.smartmaketask.screen.main.profile.ProfileScreen
import com.tta.smartmaketask.screen.main.profile.ProfileViewModel
import com.tta.smartmaketask.screen.main.quick.QuickTaskScreen
import com.tta.smartmaketask.screen.main.quick.QuickTaskViewModel
import com.tta.smartmaketask.screen.main.settings.SettingsScreen
import com.tta.smartmaketask.screen.navigation.DailyRoute
import com.tta.smartmaketask.screen.navigation.DetailRoute
import com.tta.smartmaketask.screen.navigation.GoalRoute
import com.tta.smartmaketask.screen.navigation.HomeRoute
import com.tta.smartmaketask.screen.navigation.NavDisplay
import com.tta.smartmaketask.screen.navigation.ProfileRoute
import com.tta.smartmaketask.screen.navigation.QuickRoute
import com.tta.smartmaketask.screen.navigation.SettingsRoute
import com.tta.smartmaketask.screen.navigation.navConfig
import org.koin.compose.KoinContext
import org.koin.compose.koinInject


@Composable
fun MainScreen(
    isDarkMode: Boolean = false,
    onToggleDarkMode: (Boolean) -> Unit = {}
) {
    // Wrap with KoinContext to ensure koinInject() can find the Koin instance
    KoinContext {
        val backStack = rememberNavBackStack(navConfig, HomeRoute)

        // Inject ViewModels here once, rather than inside the NavDisplay block
        val vm: HomeViewModel = koinInject()
        val goalVm: GoalTaskViewModel = koinInject()
        val dailyVm: DailyTaskViewModel = koinInject()
        val quickVm: QuickTaskViewModel = koinInject()
        val profileVm: ProfileViewModel = koinInject()

        Scaffold(
            bottomBar = {
                BottomNavigationBar(backStack)
            }
        ) { paddingValues ->
            NavDisplay(
                backStack = backStack,
                onBack = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) },
                paddingValues = paddingValues
            ) { route ->
                when (route) {
                    is HomeRoute -> {
                        HomeScreen(
                            viewModel = vm,
                            onAddGoalTask = { title, desc -> goalVm.addGoalTask(title, desc) },
                            onAddDailyTask = { title, desc ->
                                dailyVm.addDailyTask(
                                    title,
                                    desc
                                )
                            },
                            onAddQuickTask = { title, desc, urgent, important ->
                                quickVm.addQuickTask(title, desc, urgent, important)
                            }
                        )
                    }

                    is GoalRoute -> GoalTaskScreen(goalVm)
                    is DailyRoute -> DailyTaskScreen(dailyVm)
                    is QuickRoute -> QuickTaskScreen(quickVm)
                    is ProfileRoute -> ProfileScreen(profileVm)
                    is SettingsRoute -> {
                        SettingsScreen(
                            onBack = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) },
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = onToggleDarkMode
                        )
                    }

                    else -> Text("Unknown route: $route")
                }
            }
        }
    }
}