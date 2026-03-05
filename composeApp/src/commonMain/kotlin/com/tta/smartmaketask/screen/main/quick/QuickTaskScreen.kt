package com.tta.smartmaketask.screen.main.quick

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tta.smartmaketask.domain.model.EisenhowerQuadrant
import com.tta.smartmaketask.domain.model.QuickTask
import com.tta.smartmaketask.screen.components.AddTaskSheet
import com.tta.smartmaketask.screen.components.AddTaskType
import com.tta.smartmaketask.ui.theme.*

@Composable
fun QuickTaskScreen(viewModel: QuickTaskViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "⚡ Eisenhower Matrix",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Prioritize by urgency & importance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                // 2x2 Grid
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuadrantCard(
                                quadrant = EisenhowerQuadrant.DO,
                                tasks = uiState.tasksByQuadrant[EisenhowerQuadrant.DO] ?: emptyList(),
                                color = QuadrantDo,
                                onComplete = { viewModel.completeTask(it) },
                                onDelete = { viewModel.deleteTask(it.id) },
                                modifier = Modifier.weight(1f)
                            )
                            QuadrantCard(
                                quadrant = EisenhowerQuadrant.SCHEDULE,
                                tasks = uiState.tasksByQuadrant[EisenhowerQuadrant.SCHEDULE] ?: emptyList(),
                                color = QuadrantSchedule,
                                onComplete = { viewModel.completeTask(it) },
                                onDelete = { viewModel.deleteTask(it.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuadrantCard(
                                quadrant = EisenhowerQuadrant.DELEGATE,
                                tasks = uiState.tasksByQuadrant[EisenhowerQuadrant.DELEGATE] ?: emptyList(),
                                color = QuadrantDelegate,
                                onComplete = { viewModel.completeTask(it) },
                                onDelete = { viewModel.deleteTask(it.id) },
                                modifier = Modifier.weight(1f)
                            )
                            QuadrantCard(
                                quadrant = EisenhowerQuadrant.ELIMINATE,
                                tasks = uiState.tasksByQuadrant[EisenhowerQuadrant.ELIMINATE] ?: emptyList(),
                                color = QuadrantEliminate,
                                onComplete = { viewModel.completeTask(it) },
                                onDelete = { viewModel.deleteTask(it.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
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
            containerColor = MaterialTheme.colorScheme.tertiary
        ) {
            Icon(Icons.Default.Add, "Add Quick Task", tint = MaterialTheme.colorScheme.onTertiary)
        }

        AddTaskSheet(
            visible = showAddSheet,
            onDismiss = { showAddSheet = false },
            initialType = AddTaskType.QUICK,
            onAddQuickTask = { title, desc, urgent, important ->
                viewModel.addQuickTask(title, desc, urgent, important)
            }
        )
    }
}

@Composable
private fun QuadrantCard(
    quadrant: EisenhowerQuadrant,
    tasks: List<QuickTask>,
    color: Color,
    onComplete: (QuickTask) -> Unit,
    onDelete: (QuickTask) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.heightIn(min = 180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(color.copy(alpha = 0.3f))
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header
            Text(
                text = quadrant.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = quadrant.description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            if (tasks.isEmpty()) {
                Text(
                    text = "No tasks",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                tasks.forEach { task ->
                    QuadrantTaskItem(
                        task = task,
                        color = color,
                        onComplete = { onComplete(task) },
                        onDelete = { onDelete(task) }
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun QuadrantTaskItem(
    task: QuickTask,
    color: Color,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onComplete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Complete",
                    tint = SuccessGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
