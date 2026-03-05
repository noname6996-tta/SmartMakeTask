package com.tta.smartmaketask.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.coroutines.mapToOne
import com.tta.smartmaketask.db.AppDatabase
import com.tta.smartmaketask.domain.model.GoalTask
import com.tta.smartmaketask.domain.model.Milestone
import com.tta.smartmaketask.domain.model.TaskPriority
import com.tta.smartmaketask.domain.model.TaskStatus
import com.tta.smartmaketask.domain.repository.GoalTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class GoalTaskRepositoryImpl(
    private val database: AppDatabase
) : GoalTaskRepository {

    private val queries get() = database.goalTaskQueries

    override fun getAllGoalTasks(): Flow<List<GoalTask>> {
        return queries.getAllGoalTasks().asFlow().mapToList(Dispatchers.Default).map { dbList ->
            dbList.map { it.toDomain() }
        }
    }

    override fun getGoalTaskById(id: Long): Flow<GoalTask?> {
        return queries.getGoalTaskById(id).asFlow().mapToOneOrNull(Dispatchers.Default).map { it?.toDomain() }
    }

    override fun getGoalTasksByStatus(status: String): Flow<List<GoalTask>> {
        return queries.getGoalTasksByStatus(status).asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getGoalTaskCount(): Flow<Long> {
        return queries.getGoalTaskCount().asFlow().mapToOne(Dispatchers.Default)
    }

    override fun getCompletedGoalTaskCount(): Flow<Long> {
        return queries.getCompletedGoalTaskCount().asFlow().mapToOne(Dispatchers.Default)
    }

    override suspend fun insertGoalTask(task: GoalTask): Long {
        queries.insertGoalTask(
            title = task.title,
            description = task.description,
            deadline = task.deadline?.toString(),
            progressPercent = task.progressPercent.toLong(),
            priority = task.priority.name,
            status = task.status.name,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt
        )
        return queries.getLastInsertId().executeAsOne()
    }

    override suspend fun updateGoalTask(task: GoalTask) {
        queries.updateGoalTask(
            title = task.title,
            description = task.description,
            deadline = task.deadline?.toString(),
            progressPercent = task.progressPercent.toLong(),
            priority = task.priority.name,
            status = task.status.name,
            updatedAt = task.updatedAt,
            id = task.id
        )
    }

    override suspend fun deleteGoalTask(id: Long) {
        queries.deleteMilestonesForGoal(id)
        queries.deleteGoalTask(id)
    }

    // Milestones

    override fun getMilestonesForGoal(goalId: Long): Flow<List<Milestone>> {
        return queries.getMilestonesForGoal(goalId).asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { it.toMilestoneDomain() }
        }
    }

    override suspend fun insertMilestone(milestone: Milestone) {
        queries.insertMilestone(
            goalTaskId = milestone.goalTaskId,
            title = milestone.title,
            isCompleted = if (milestone.isCompleted) 1L else 0L,
            sortOrder = milestone.order.toLong()
        )
    }

    override suspend fun updateMilestoneCompletion(id: Long, isCompleted: Boolean) {
        queries.updateMilestoneCompletion(
            isCompleted = if (isCompleted) 1L else 0L,
            id = id
        )
    }

    override suspend fun deleteMilestone(id: Long) {
        queries.deleteMilestone(id)
    }

    // Mappers

    private fun com.tta.smartmaketask.db.GoalTask.toDomain(): GoalTask {
        return GoalTask(
            id = id,
            title = title,
            description = description,
            deadline = deadline?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            progressPercent = progressPercent.toInt(),
            priority = runCatching { TaskPriority.valueOf(priority) }.getOrDefault(TaskPriority.MEDIUM),
            status = runCatching { TaskStatus.valueOf(status) }.getOrDefault(TaskStatus.TODO),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun com.tta.smartmaketask.db.Milestone.toMilestoneDomain(): Milestone {
        return Milestone(
            id = id,
            goalTaskId = goalTaskId,
            title = title,
            isCompleted = isCompleted != 0L,
            order = sortOrder.toInt()
        )
    }
}
