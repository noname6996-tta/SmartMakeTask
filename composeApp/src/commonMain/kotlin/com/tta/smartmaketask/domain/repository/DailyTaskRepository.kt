package com.tta.smartmaketask.domain.repository

import com.tta.smartmaketask.domain.model.DailyTask
import com.tta.smartmaketask.domain.model.DailyCompletion
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface DailyTaskRepository {
    fun getAllDailyTasks(): Flow<List<DailyTask>>
    fun getDailyTaskById(id: Long): Flow<DailyTask?>
    fun getDailyTaskCount(): Flow<Long>
    fun getCompletedTodayCount(today: LocalDate): Flow<Long>
    suspend fun insertDailyTask(task: DailyTask): Long
    suspend fun updateDailyTask(task: DailyTask)
    suspend fun deleteDailyTask(id: Long)
    
    // Completion tracking
    suspend fun markCompleted(taskId: Long, date: LocalDate)
    suspend fun markUncompleted(taskId: Long, date: LocalDate)
    fun isCompletedOnDate(taskId: Long, date: LocalDate): Flow<Boolean>
}
