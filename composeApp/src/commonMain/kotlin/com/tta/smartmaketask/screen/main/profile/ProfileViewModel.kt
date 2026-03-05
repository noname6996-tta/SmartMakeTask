package com.tta.smartmaketask.screen.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tta.smartmaketask.domain.model.UserStreak
import com.tta.smartmaketask.domain.repository.DailyTaskRepository
import com.tta.smartmaketask.domain.repository.GoalTaskRepository
import com.tta.smartmaketask.domain.repository.QuickTaskRepository
import com.tta.smartmaketask.domain.repository.StreakRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val streakRepository: StreakRepository,
    private val goalTaskRepository: GoalTaskRepository,
    private val dailyTaskRepository: DailyTaskRepository,
    private val quickTaskRepository: QuickTaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            combine(
                streakRepository.getStreak(),
                goalTaskRepository.getGoalTaskCount(),
                goalTaskRepository.getCompletedGoalTaskCount(),
                dailyTaskRepository.getDailyTaskCount(),
                quickTaskRepository.getPendingCount()
            ) { streak, goalCount, goalCompleted, dailyCount, quickPending ->
                ProfileUiState(
                    streak = streak,
                    totalGoals = goalCount.toInt(),
                    completedGoals = goalCompleted.toInt(),
                    totalDailyHabits = dailyCount.toInt(),
                    pendingQuickTasks = quickPending.toInt(),
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}

data class ProfileUiState(
    val streak: UserStreak = UserStreak(),
    val totalGoals: Int = 0,
    val completedGoals: Int = 0,
    val totalDailyHabits: Int = 0,
    val pendingQuickTasks: Int = 0,
    val isLoading: Boolean = true
)