package com.tta.smartmaketask.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.tta.smartmaketask.db.AppDatabase
import com.tta.smartmaketask.domain.model.DailyTask
import com.tta.smartmaketask.domain.repository.DailyTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

class DailyTaskRepositoryImpl(
    private val database: AppDatabase
) : DailyTaskRepository {

    private val taskQueries get() = database.dailyTaskQueries
    private val completionQueries get() = database.dailyCompletionQueries

    override fun getAllDailyTasks(): Flow<List<DailyTask>> {
        return taskQueries.getAllDailyTasks().asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getDailyTaskById(id: Long): Flow<DailyTask?> {
        return taskQueries.getDailyTaskById(id).asFlow().mapToOneOrNull(Dispatchers.Default).map { it?.toDomain() }
    }

    override fun getDailyTaskCount(): Flow<Long> {
        return taskQueries.getDailyTaskCount().asFlow().mapToOne(Dispatchers.Default)
    }

    override fun getCompletedTodayCount(today: LocalDate): Flow<Long> {
        return completionQueries.getCompletedTodayCount(today.toString()).asFlow().mapToOne(Dispatchers.Default)
    }

    override suspend fun insertDailyTask(task: DailyTask): Long {
        taskQueries.insertDailyTask(
            title = task.title,
            description = task.description,
            reminderHour = task.reminderHour?.toLong(),
            reminderMinute = task.reminderMinute?.toLong(),
            currentStreak = task.currentStreak.toLong(),
            longestStreak = task.longestStreak.toLong(),
            lastCompletedDate = task.lastCompletedDate?.toString(),
            createdAt = task.createdAt
        )
        return database.goalTaskQueries.getLastInsertId().executeAsOne()
    }

    override suspend fun updateDailyTask(task: DailyTask) {
        taskQueries.updateDailyTask(
            title = task.title,
            description = task.description,
            reminderHour = task.reminderHour?.toLong(),
            reminderMinute = task.reminderMinute?.toLong(),
            id = task.id
        )
    }

    override suspend fun deleteDailyTask(id: Long) {
        completionQueries.deleteCompletionsForTask(id)
        taskQueries.deleteDailyTask(id)
    }

    override suspend fun markCompleted(taskId: Long, date: LocalDate) {
        val dateStr = date.toString()
        // Check if already completed today
        val existing = completionQueries.getCompletionForDate(taskId, dateStr).executeAsOneOrNull()
        if (existing == null) {
            completionQueries.insertCompletion(taskId, dateStr)

            // Update streak
            val task = taskQueries.getDailyTaskById(taskId).executeAsOneOrNull() ?: return
            val lastDate = task.lastCompletedDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            val yesterday = date.minus(DatePeriod(days = 1))

            val newStreak = if (lastDate == yesterday || lastDate == null) {
                task.currentStreak + 1
            } else if (lastDate == date) {
                task.currentStreak
            } else {
                1
            }

            val newLongest = maxOf(newStreak, task.longestStreak)
            taskQueries.updateDailyTaskStreak(
                currentStreak = newStreak,
                longestStreak = newLongest,
                lastCompletedDate = dateStr,
                id = taskId
            )
        }
    }

    override suspend fun markUncompleted(taskId: Long, date: LocalDate) {
        completionQueries.deleteCompletion(taskId, date.toString())
    }

    override fun isCompletedOnDate(taskId: Long, date: LocalDate): Flow<Boolean> {
        return completionQueries.getCompletionForDate(taskId, date.toString())
            .asFlow().mapToOneOrNull(Dispatchers.Default).map { it != null }
    }

    private fun com.tta.smartmaketask.db.DailyTask.toDomain(): DailyTask {
        return DailyTask(
            id = id,
            title = title,
            description = description,
            reminderHour = reminderHour?.toInt(),
            reminderMinute = reminderMinute?.toInt(),
            currentStreak = currentStreak.toInt(),
            longestStreak = longestStreak.toInt(),
            lastCompletedDate = lastCompletedDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            createdAt = createdAt
        )
    }
}
