package com.tta.smartmaketask.screen.main.daily

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tta.smartmaketask.domain.model.DailyTask
import com.tta.smartmaketask.screen.components.AddTaskSheet
import com.tta.smartmaketask.screen.components.AddTaskType
import com.tta.smartmaketask.screen.components.TaskCard
import com.tta.smartmaketask.ui.theme.*

@Composable
fun DailyTaskScreen(viewModel: DailyTaskViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.tasks.isEmpty()) {
            EmptyDailyState(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "📅 Daily Habits",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${uiState.tasks.count { it.isCompletedToday }}/${uiState.tasks.size} completed today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                // Today's progress
                item {
                    val completedCount = uiState.tasks.count { it.isCompletedToday }
                    val total = uiState.tasks.size
                    val progress = if (total > 0) completedCount.toFloat() / total else 0f

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (progress >= 1f) "🎉 All done for today!" else "Today's Progress",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp),
                                color = SageLight,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }
                }

                items(uiState.tasks, key = { it.id }) { task ->
                    DailyTaskItem(
                        task = task,
                        onToggle = { viewModel.toggleCompletion(task) },
                        onDelete = { viewModel.deleteDailyTask(task.id) }
                    )
                }

                item { Spacer(Modifier.height(60.dp)) }
            }
        }

        FloatingActionButton(
            onClick = { showAddSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(Icons.Default.Add, "Add Habit", tint = MaterialTheme.colorScheme.onSecondary)
        }

        AddTaskSheet(
            visible = showAddSheet,
            onDismiss = { showAddSheet = false },
            initialType = AddTaskType.DAILY,
            onAddDailyTask = { title, desc -> viewModel.addDailyTask(title, desc) }
        )
    }
}

@Composable
private fun DailyTaskItem(
    task: DailyTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    TaskCard(
        title = task.title,
        subtitle = task.description,
        isCompleted = task.isCompletedToday,
        accentColor = SageLight,
        onToggleComplete = onToggle,
        onDelete = onDelete,
        trailingContent = {
            if (task.currentStreak > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = StreakFireOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = "${task.currentStreak}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = StreakFireOrange
                    )
                }
            }
        }
    )
}

@Composable
private fun EmptyDailyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📅", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text(
            "No daily habits",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Build powerful habits one day at a time.\nTap + to create your first daily habit!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
