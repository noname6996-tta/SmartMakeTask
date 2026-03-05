package com.tta.smartmaketask.screen.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import androidx.savedstate.serialization.SavedStateConfiguration

val navSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(HomeRoute::class, HomeRoute.serializer())
        subclass(GoalRoute::class, GoalRoute.serializer())
        subclass(DailyRoute::class, DailyRoute.serializer())
        subclass(QuickRoute::class, QuickRoute.serializer())
        subclass(ProfileRoute::class, ProfileRoute.serializer())
        subclass(DetailRoute::class, DetailRoute.serializer())
        subclass(SettingsRoute::class, SettingsRoute.serializer())
    }
}

val navConfig = SavedStateConfiguration {
    serializersModule = navSerializersModule
}