package com.tta.smartmaketask.screen.main.home

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tta.smartmaketask.screen.components.AddTaskSheet
import com.tta.smartmaketask.screen.components.AddTaskType
import com.tta.smartmaketask.screen.components.StreakCard
import com.tta.smartmaketask.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddGoalTask: (String, String) -> Unit = { _, _ -> },
    onAddDailyTask: (String, String) -> Unit = { _, _ -> },
    onAddQuickTask: (String, String, Boolean, Boolean) -> Unit = { _, _, _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Greeting
            item {
                Column {
                    Text(
                        text = viewModel.getGreeting(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Let's make today productive!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            // Streak Card
            item {
                StreakCard(streak = uiState.summary.streak)
            }

            // Task Summary Cards
            item {
                Text(
                    text = "Today's Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        emoji = "🎯",
                        title = "Goals",
                        count = "${uiState.summary.goalTasksCompleted}/${uiState.summary.goalTasksCount}",
                        subtitle = "completed",
                        color = LavenderLight,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        emoji = "📅",
                        title = "Daily",
                        count = "${uiState.summary.dailyTasksCompletedToday}/${uiState.summary.dailyTasksCount}",
                        subtitle = "today",
                        color = SageLight,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        emoji = "⚡",
                        title = "Quick",
                        count = "${uiState.summary.quickTasksPending}",
                        subtitle = "pending",
                        color = PeachLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Motivational section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "💡 Smart Tip",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = getMotivationalTip(uiState),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Bottom spacing for FAB
            item { Spacer(Modifier.height(60.dp)) }
        }

        // FAB
        FloatingActionButton(
            onClick = { showAddSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add task",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Add Task Sheet
        AddTaskSheet(
            visible = showAddSheet,
            onDismiss = { showAddSheet = false },
            onAddGoalTask = onAddGoalTask,
            onAddDailyTask = onAddDailyTask,
            onAddQuickTask = onAddQuickTask
        )
    }
}

@Composable
private fun SummaryCard(
    emoji: String,
    title: String,
    count: String,
    subtitle: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(6.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getMotivationalTip(state: HomeUiState): String {
    val streak = state.summary.streak.currentStreak
    return when {
        streak == 0 -> "Complete a task today to start a new streak! 🚀"
        streak < 3 -> "You're building momentum! Keep going for ${3 - streak} more days. 💪"
        streak < 7 -> "Your streak is growing! Aim for a full week. 🔥"
        streak < 30 -> "Amazing $streak-day streak! You're a productivity machine. 🏆"
        else -> "Incredible $streak-day streak! You're unstoppable! 🌟"
    }
}