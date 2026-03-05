package com.tta.smartmaketask.screen.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tta.smartmaketask.domain.model.DashboardSummary
import com.tta.smartmaketask.domain.model.UserStreak
import com.tta.smartmaketask.domain.repository.DailyTaskRepository
import com.tta.smartmaketask.domain.repository.GoalTaskRepository
import com.tta.smartmaketask.domain.repository.QuickTaskRepository
import com.tta.smartmaketask.domain.repository.StreakRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeViewModel(
    private val goalTaskRepository: GoalTaskRepository,
    private val dailyTaskRepository: DailyTaskRepository,
    private val quickTaskRepository: QuickTaskRepository,
    private val streakRepository: StreakRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            streakRepository.initStreak()
        }
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

            val streakFlow = streakRepository.getStreak()
            val goalCountFlow = goalTaskRepository.getGoalTaskCount()
            val goalCompletedFlow = goalTaskRepository.getCompletedGoalTaskCount()
            val dailyCountFlow = dailyTaskRepository.getDailyTaskCount()
            val dailyCompletedFlow = dailyTaskRepository.getCompletedTodayCount(today)
            val quickPendingFlow = quickTaskRepository.getPendingCount()

            combine(
                combine(streakFlow, goalCountFlow, goalCompletedFlow) { streak, goalCount, goalCompleted ->
                    Triple(streak, goalCount, goalCompleted)
                },
                dailyCountFlow,
                dailyCompletedFlow,
                quickPendingFlow
            ) { (streak, goalCount, goalCompleted), dailyCount, dailyCompleted, quickPending ->
                DashboardSummary(
                    streak = streak,
                    goalTasksCount = goalCount.toInt(),
                    goalTasksCompleted = goalCompleted.toInt(),
                    dailyTasksCount = dailyCount.toInt(),
                    dailyTasksCompletedToday = dailyCompleted.toInt(),
                    quickTasksPending = quickPending.toInt()
                )
            }.collect { summary ->
                _uiState.update { it.copy(summary = summary, isLoading = false) }
            }
        }
    }

    fun getGreeting(): String {
        val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
        return when {
            hour < 5 -> "Good night 🌙"
            hour < 12 -> "Good morning ☀️"
            hour < 17 -> "Good afternoon 🌤️"
            hour < 21 -> "Good evening 🌅"
            else -> "Good night 🌙"
        }
    }
}

data class HomeUiState(
    val summary: DashboardSummary = DashboardSummary(streak = UserStreak()),
    val isLoading: Boolean = true
)