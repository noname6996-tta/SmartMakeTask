package com.tta.smartmaketask.screen.main.quick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tta.smartmaketask.domain.model.EisenhowerQuadrant
import com.tta.smartmaketask.domain.model.QuickTask
import com.tta.smartmaketask.domain.model.TaskStatus
import com.tta.smartmaketask.domain.repository.QuickTaskRepository
import com.tta.smartmaketask.domain.repository.StreakRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class QuickTaskViewModel(
    private val repository: QuickTaskRepository,
    private val streakRepository: StreakRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickUiState())
    val uiState: StateFlow<QuickUiState> = _uiState.asStateFlow()

    init {
        loadQuickTasks()
    }

    private fun loadQuickTasks() {
        viewModelScope.launch {
            repository.getAllQuickTasks().collect { tasks ->
                val grouped = tasks
                    .filter { it.status != TaskStatus.COMPLETED }
                    .groupBy { it.quadrant }
                _uiState.update {
                    it.copy(
                        allTasks = tasks,
                        tasksByQuadrant = grouped,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun addQuickTask(title: String, description: String, isUrgent: Boolean, isImportant: Boolean) {
        viewModelScope.launch {
            val quadrant = when {
                isUrgent && isImportant -> EisenhowerQuadrant.DO
                !isUrgent && isImportant -> EisenhowerQuadrant.SCHEDULE
                isUrgent && !isImportant -> EisenhowerQuadrant.DELEGATE
                else -> EisenhowerQuadrant.ELIMINATE
            }
            repository.insertQuickTask(
                QuickTask(
                    title = title,
                    description = description,
                    isUrgent = isUrgent,
                    isImportant = isImportant,
                    quadrant = quadrant,
                    status = TaskStatus.TODO,
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )
            )
        }
    }

    fun completeTask(task: QuickTask) {
        viewModelScope.launch {
            val now = Clock.System.now()
            repository.completeQuickTask(task.id, now.toEpochMilliseconds())
            val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
            streakRepository.recordTaskCompletion(today)
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            repository.deleteQuickTask(id)
        }
    }
}

data class QuickUiState(
    val allTasks: List<QuickTask> = emptyList(),
    val tasksByQuadrant: Map<EisenhowerQuadrant, List<QuickTask>> = emptyMap(),
    val isLoading: Boolean = true
)
