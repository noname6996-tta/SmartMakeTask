package com.tta.smartmaketask.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.tta.smartmaketask.db.AppDatabase
import com.tta.smartmaketask.domain.model.EisenhowerQuadrant
import com.tta.smartmaketask.domain.model.QuickTask
import com.tta.smartmaketask.domain.model.TaskStatus
import com.tta.smartmaketask.domain.repository.QuickTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class QuickTaskRepositoryImpl(
    private val database: AppDatabase
) : QuickTaskRepository {

    private val queries get() = database.quickTaskQueries

    override fun getAllQuickTasks(): Flow<List<QuickTask>> {
        return queries.getAllQuickTasks().asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getQuickTaskById(id: Long): Flow<QuickTask?> {
        return queries.getQuickTaskById(id).asFlow().mapToOneOrNull(Dispatchers.Default).map { it?.toDomain() }
    }

    override fun getQuickTasksByQuadrant(quadrant: EisenhowerQuadrant): Flow<List<QuickTask>> {
        return queries.getQuickTasksByQuadrant(quadrant.name).asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getPendingCount(): Flow<Long> {
        return queries.getPendingQuickTaskCount().asFlow().mapToOne(Dispatchers.Default)
    }

    override fun getCompletedTodayCount(todayStartMillis: Long, todayEndMillis: Long): Flow<Long> {
        return queries.getCompletedTodayQuickTaskCount(todayStartMillis, todayEndMillis)
            .asFlow().mapToOne(Dispatchers.Default)
    }

    override suspend fun insertQuickTask(task: QuickTask): Long {
        queries.insertQuickTask(
            title = task.title,
            description = task.description,
            isUrgent = if (task.isUrgent) 1L else 0L,
            isImportant = if (task.isImportant) 1L else 0L,
            quadrant = task.quadrant.name,
            dueDate = task.dueDate?.toString(),
            status = task.status.name,
            createdAt = task.createdAt,
            completedAt = task.completedAt
        )
        return database.goalTaskQueries.getLastInsertId().executeAsOne()
    }

    override suspend fun updateQuickTask(task: QuickTask) {
        queries.updateQuickTask(
            title = task.title,
            description = task.description,
            isUrgent = if (task.isUrgent) 1L else 0L,
            isImportant = if (task.isImportant) 1L else 0L,
            quadrant = task.quadrant.name,
            dueDate = task.dueDate?.toString(),
            status = task.status.name,
            id = task.id
        )
    }

    override suspend fun completeQuickTask(id: Long, completedAtMillis: Long) {
        queries.completeQuickTask(completedAtMillis, id)
    }

    override suspend fun deleteQuickTask(id: Long) {
        queries.deleteQuickTask(id)
    }

    private fun com.tta.smartmaketask.db.QuickTask.toDomain(): QuickTask {
        return QuickTask(
            id = id,
            title = title,
            description = description,
            isUrgent = isUrgent != 0L,
            isImportant = isImportant != 0L,
            quadrant = runCatching { EisenhowerQuadrant.valueOf(quadrant) }.getOrDefault(EisenhowerQuadrant.ELIMINATE),
            dueDate = dueDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            status = runCatching { TaskStatus.valueOf(status) }.getOrDefault(TaskStatus.TODO),
            createdAt = createdAt,
            completedAt = completedAt
        )
    }
}
