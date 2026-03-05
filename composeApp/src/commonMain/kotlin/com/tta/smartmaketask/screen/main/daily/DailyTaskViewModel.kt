package com.tta.smartmaketask.screen.main.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tta.smartmaketask.domain.model.DailyTask
import com.tta.smartmaketask.domain.repository.DailyTaskRepository
import com.tta.smartmaketask.domain.repository.StreakRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class DailyTaskViewModel(
    private val repository: DailyTaskRepository,
    private val streakRepository: StreakRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyUiState())
    val uiState: StateFlow<DailyUiState> = _uiState.asStateFlow()

    private val today get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    init {
        loadDailyTasks()
    }

    private fun loadDailyTasks() {
        viewModelScope.launch {
            repository.getAllDailyTasks().collect { tasks ->
                // Check completion status for today
                val tasksWithStatus = tasks.map { task ->
                    val isCompleted = task.lastCompletedDate == today
                    task.copy(isCompletedToday = isCompleted)
                }
                _uiState.update { it.copy(tasks = tasksWithStatus, isLoading = false) }
            }
        }
    }

    fun addDailyTask(title: String, description: String) {
        viewModelScope.launch {
            repository.insertDailyTask(
                DailyTask(
                    title = title,
                    description = description,
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )
            )
        }
    }

    fun toggleCompletion(task: DailyTask) {
        viewModelScope.launch {
            if (task.isCompletedToday) {
                repository.markUncompleted(task.id, today)
            } else {
                repository.markCompleted(task.id, today)
                streakRepository.recordTaskCompletion(today)
            }
        }
    }

    fun deleteDailyTask(id: Long) {
        viewModelScope.launch {
            repository.deleteDailyTask(id)
        }
    }
}

data class DailyUiState(
    val tasks: List<DailyTask> = emptyList(),
    val isLoading: Boolean = true
)
