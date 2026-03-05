package com.tta.smartmaketask.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

// ── Enums ──

enum class TaskPriority(val label: String) {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low")
}

enum class EisenhowerQuadrant(val label: String, val description: String) {
    DO("Do First", "Urgent & Important"),
    SCHEDULE("Schedule", "Not Urgent & Important"),
    DELEGATE("Delegate", "Urgent & Not Important"),
    ELIMINATE("Eliminate", "Not Urgent & Not Important")
}

enum class TaskStatus(val label: String) {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed")
}

// ── Goal Task ──

data class GoalTask(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val deadline: LocalDate? = null,
    val progressPercent: Int = 0,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.TODO,
    val milestones: List<Milestone> = emptyList(),
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

data class Milestone(
    val id: Long = 0,
    val goalTaskId: Long = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val order: Int = 0
)

// ── Daily Task ──

data class DailyTask(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCompletedDate: LocalDate? = null,
    val isCompletedToday: Boolean = false,
    val createdAt: Long = 0L
)

// ── Quick Task ──

data class QuickTask(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val isUrgent: Boolean = false,
    val isImportant: Boolean = false,
    val quadrant: EisenhowerQuadrant = EisenhowerQuadrant.ELIMINATE,
    val dueDate: LocalDate? = null,
    val status: TaskStatus = TaskStatus.TODO,
    val createdAt: Long = 0L,
    val completedAt: Long? = null
)

// ── User Streak ──

data class UserStreak(
    val id: Long = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: LocalDate? = null,
    val totalCompleted: Long = 0
)

// ── Daily Completion ──

data class DailyCompletion(
    val id: Long = 0,
    val dailyTaskId: Long,
    val completedDate: LocalDate
)

// ── Dashboard Summary ──

data class DashboardSummary(
    val streak: UserStreak,
    val goalTasksCount: Int = 0,
    val goalTasksCompleted: Int = 0,
    val dailyTasksCount: Int = 0,
    val dailyTasksCompletedToday: Int = 0,
    val quickTasksPending: Int = 0,
    val quickTasksCompletedToday: Int = 0
)
