package com.tta.smartmaketask.screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class AddTaskType(val label: String, val emoji: String) {
    GOAL("Goal Task", "🎯"),
    DAILY("Daily Task", "📅"),
    QUICK("Quick Task", "⚡")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    initialType: AddTaskType = AddTaskType.QUICK,
    onAddGoalTask: (title: String, description: String) -> Unit = { _, _ -> },
    onAddDailyTask: (title: String, description: String) -> Unit = { _, _ -> },
    onAddQuickTask: (title: String, description: String, isUrgent: Boolean, isImportant: Boolean) -> Unit = { _, _, _, _ -> }
) {
    var selectedType by remember(visible) { mutableStateOf(initialType) }
    var title by remember(visible) { mutableStateOf("") }
    var description by remember(visible) { mutableStateOf("") }
    var isUrgent by remember(visible) { mutableStateOf(false) }
    var isImportant by remember(visible) { mutableStateOf(false) }

    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add New Task",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Task type selector
                Text(
                    text = "Task Type",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AddTaskType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text("${type.emoji} ${type.label}") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("What do you want to accomplish?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )

                // Quick task specific: urgency/importance
                AnimatedVisibility(
                    visible = selectedType == AddTaskType.QUICK,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Column {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Eisenhower Priority",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isUrgent, onCheckedChange = { isUrgent = it })
                            Text("Urgent", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.width(24.dp))
                            Checkbox(checked = isImportant, onCheckedChange = { isImportant = it })
                            Text("Important", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Add button
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            when (selectedType) {
                                AddTaskType.GOAL -> onAddGoalTask(title.trim(), description.trim())
                                AddTaskType.DAILY -> onAddDailyTask(title.trim(), description.trim())
                                AddTaskType.QUICK -> onAddQuickTask(title.trim(), description.trim(), isUrgent, isImportant)
                            }
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = title.isNotBlank()
                ) {
                    Text(
                        text = "Add ${selectedType.emoji} ${selectedType.label}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
