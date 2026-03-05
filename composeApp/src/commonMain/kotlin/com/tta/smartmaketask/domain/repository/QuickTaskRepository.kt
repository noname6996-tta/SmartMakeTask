package com.tta.smartmaketask.domain.repository

import com.tta.smartmaketask.domain.model.QuickTask
import com.tta.smartmaketask.domain.model.EisenhowerQuadrant
import kotlinx.coroutines.flow.Flow

interface QuickTaskRepository {
    fun getAllQuickTasks(): Flow<List<QuickTask>>
    fun getQuickTaskById(id: Long): Flow<QuickTask?>
    fun getQuickTasksByQuadrant(quadrant: EisenhowerQuadrant): Flow<List<QuickTask>>
    fun getPendingCount(): Flow<Long>
    fun getCompletedTodayCount(todayStartMillis: Long, todayEndMillis: Long): Flow<Long>
    suspend fun insertQuickTask(task: QuickTask): Long
    suspend fun updateQuickTask(task: QuickTask)
    suspend fun completeQuickTask(id: Long, completedAtMillis: Long)
    suspend fun deleteQuickTask(id: Long)
}
