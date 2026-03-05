package com.tta.smartmaketask.screen.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.tta.smartmaketask.screen.navigation.DailyRoute
import com.tta.smartmaketask.screen.navigation.GoalRoute
import com.tta.smartmaketask.screen.navigation.HomeRoute
import com.tta.smartmaketask.screen.navigation.ProfileRoute
import com.tta.smartmaketask.screen.navigation.QuickRoute

private data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: NavKey
)

private val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Default.Home, HomeRoute),
    BottomNavItem("Goals", Icons.Default.Star, GoalRoute),
    BottomNavItem("Daily", Icons.Default.CalendarToday, DailyRoute),
    BottomNavItem("Quick", Icons.Default.FlashOn, QuickRoute),
    BottomNavItem("Profile", Icons.Default.Person, ProfileRoute)
)

@Composable
fun BottomNavigationBar(backStack: NavBackStack<NavKey>) {
    val currentRoute = backStack.lastOrNull()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        backStack.clear()
                        backStack.add(item.route)
                    }
                },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}