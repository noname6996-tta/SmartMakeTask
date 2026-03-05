package com.tta.smartmaketask.screen.main.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tta.smartmaketask.domain.model.GoalTask
import com.tta.smartmaketask.domain.model.TaskPriority
import com.tta.smartmaketask.domain.model.TaskStatus
import com.tta.smartmaketask.domain.repository.GoalTaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Clock

class GoalTaskViewModel(
    private val repository: GoalTaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    init {
        loadGoalTasks()
    }

    private fun loadGoalTasks() {
        viewModelScope.launch {
            repository.getAllGoalTasks().collect { tasks ->
                _uiState.update { it.copy(tasks = tasks, isLoading = false) }
            }
        }
    }

    fun addGoalTask(title: String, description: String) {
        viewModelScope.launch {
            val now = Clock.System.now().toEpochMilliseconds()
            repository.insertGoalTask(
                GoalTask(
                    title = title,
                    description = description,
                    priority = TaskPriority.MEDIUM,
                    status = TaskStatus.TODO,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    fun updateProgress(task: GoalTask, newProgress: Int) {
        viewModelScope.launch {
            val status = if (newProgress >= 100) TaskStatus.COMPLETED else TaskStatus.IN_PROGRESS
            repository.updateGoalTask(
                task.copy(
                    progressPercent = newProgress.coerceIn(0, 100),
                    status = status,
                    updatedAt = Clock.System.now().toEpochMilliseconds()
                )
            )
        }
    }

    fun deleteGoalTask(id: Long) {
        viewModelScope.launch {
            repository.deleteGoalTask(id)
        }
    }

    fun toggleStatus(task: GoalTask) {
        viewModelScope.launch {
            val newStatus = when (task.status) {
                TaskStatus.TODO -> TaskStatus.IN_PROGRESS
                TaskStatus.IN_PROGRESS -> TaskStatus.COMPLETED
                TaskStatus.COMPLETED -> TaskStatus.TODO
            }
            val progress = if (newStatus == TaskStatus.COMPLETED) 100 else task.progressPercent
            repository.updateGoalTask(
                task.copy(
                    status = newStatus,
                    progressPercent = progress,
                    updatedAt = Clock.System.now().toEpochMilliseconds()
                )
            )
        }
    }
}

data class GoalUiState(
    val tasks: List<GoalTask> = emptyList(),
    val isLoading: Boolean = true
)
