package com.tta.smartmaketask.screen.navigation

import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey

@Serializable object HomeRoute : NavKey
@Serializable object GoalRoute : NavKey
@Serializable object DailyRoute : NavKey
@Serializable object QuickRoute : NavKey
@Serializable object ProfileRoute : NavKey
@Serializable object SettingsRoute : NavKey
@Serializable data class DetailRoute(val id: String) : NavKey