package com.tta.smartmaketask.domain.repository

import com.tta.smartmaketask.domain.model.UserStreak
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface StreakRepository {
    fun getStreak(): Flow<UserStreak>
    suspend fun initStreak()
    suspend fun updateStreak(streak: UserStreak)
    suspend fun recordTaskCompletion(today: LocalDate)
}
