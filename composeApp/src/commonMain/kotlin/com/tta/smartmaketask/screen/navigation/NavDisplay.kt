package com.tta.smartmaketask.screen.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Displays the current navigation destination with animated transitions.
 *
 * @param backStack The navigation back stack to observe
 * @param onBack Callback when back navigation is requested
 * @param paddingValues Padding to apply to the content (typically from Scaffold)
 * @param content Composable lambda that renders the screen for a given NavKey
 */
@Composable
fun NavDisplay(
    backStack: NavBackStack<NavKey>,
    onBack: () -> Unit,
    paddingValues: PaddingValues = PaddingValues(),
    content: @Composable (NavKey) -> Unit
) {
    // Animate transitions between screens
    val currentRoute = backStack.lastOrNull()
    if (currentRoute != null) {
        AnimatedContent(
            targetState = currentRoute,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            modifier = Modifier.padding(paddingValues)
        ) { route ->
            content(route)
        }
    }
}
