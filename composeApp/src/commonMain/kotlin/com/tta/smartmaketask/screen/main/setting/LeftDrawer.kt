package com.tta.smartmaketask.screen.main.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.tta.smartmaketask.screen.navigation.DetailRoute
import com.tta.smartmaketask.screen.navigation.SettingsRoute

@Composable
fun LeftDrawer(backStack: NavBackStack<NavKey>) {
    Column {
        Text("Menu", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Text("Settings", Modifier.clickable { backStack.add(SettingsRoute) })
        Spacer(Modifier.height(8.dp))
        Text("Go to Details", Modifier.clickable { backStack.add(DetailRoute("123")) })
    }
}