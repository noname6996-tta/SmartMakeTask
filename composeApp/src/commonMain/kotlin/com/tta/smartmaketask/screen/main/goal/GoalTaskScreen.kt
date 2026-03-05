package com.tta.smartmaketask.screen.main.goal

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tta.smartmaketask.domain.model.GoalTask
import com.tta.smartmaketask.domain.model.TaskStatus
import com.tta.smartmaketask.screen.components.AddTaskSheet
import com.tta.smartmaketask.screen.components.AddTaskType
import com.tta.smartmaketask.ui.theme.*

@Composable
fun GoalTaskScreen(viewModel: GoalTaskViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.tasks.isEmpty()) {
            EmptyGoalState(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "🎯 Your Goals",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${uiState.tasks.count { it.status == TaskStatus.COMPLETED }}/${uiState.tasks.size} completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                items(uiState.tasks, key = { it.id }) { task ->
                    GoalTaskCard(
                        task = task,
                        onToggle = { viewModel.toggleStatus(task) },
                        onDelete = { viewModel.deleteGoalTask(task.id) },
                        onProgressChange = { viewModel.updateProgress(task, it) }
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
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, "Add Goal", tint = MaterialTheme.colorScheme.onPrimary)
        }

        AddTaskSheet(
            visible = showAddSheet,
            onDismiss = { showAddSheet = false },
            initialType = AddTaskType.GOAL,
            onAddGoalTask = { title, desc -> viewModel.addGoalTask(title, desc) }
        )
    }
}

@Composable
private fun GoalTaskCard(
    task: GoalTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onProgressChange: (Int) -> Unit
) {
    val isCompleted = task.status == TaskStatus.COMPLETED

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Priority indicator
                Surface(
                    modifier = Modifier.size(8.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = when (task.priority) {
                        com.tta.smartmaketask.domain.model.TaskPriority.HIGH -> PriorityHigh
                        com.tta.smartmaketask.domain.model.TaskPriority.MEDIUM -> PriorityMedium
                        com.tta.smartmaketask.domain.model.TaskPriority.LOW -> PriorityLow
                    }
                ) {}

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }

                // Status chip
                AssistChip(
                    onClick = onToggle,
                    label = {
                        Text(
                            text = task.status.label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier.height(28.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Progress bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { task.progressPercent / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    color = if (isCompleted) SuccessGreen else LavenderLight,
                    strokeCap = StrokeCap.Round
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "${task.progressPercent}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Quick progress buttons
            if (!isCompleted) {
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(25, 50, 75, 100).forEach { pct ->
                        TextButton(
                            onClick = { onProgressChange(pct) },
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text("$pct%", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = onDelete,
                        modifier = Modifier.height(32.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyGoalState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎯", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text(
            "No goals yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Set long-term goals and track your progress.\nTap + to get started!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
