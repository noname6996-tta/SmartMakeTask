package com.tta.smartmaketask.domain.repository

import com.tta.smartmaketask.domain.model.GoalTask
import com.tta.smartmaketask.domain.model.Milestone
import kotlinx.coroutines.flow.Flow

interface GoalTaskRepository {
    fun getAllGoalTasks(): Flow<List<GoalTask>>
    fun getGoalTaskById(id: Long): Flow<GoalTask?>
    fun getGoalTasksByStatus(status: String): Flow<List<GoalTask>>
    fun getGoalTaskCount(): Flow<Long>
    fun getCompletedGoalTaskCount(): Flow<Long>
    suspend fun insertGoalTask(task: GoalTask): Long
    suspend fun updateGoalTask(task: GoalTask)
    suspend fun deleteGoalTask(id: Long)
    
    // Milestones
    fun getMilestonesForGoal(goalId: Long): Flow<List<Milestone>>
    suspend fun insertMilestone(milestone: Milestone)
    suspend fun updateMilestoneCompletion(id: Long, isCompleted: Boolean)
    suspend fun deleteMilestone(id: Long)
}
