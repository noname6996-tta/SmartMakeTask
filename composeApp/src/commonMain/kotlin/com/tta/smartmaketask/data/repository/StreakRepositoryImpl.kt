package com.tta.smartmaketask.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.tta.smartmaketask.db.AppDatabase
import com.tta.smartmaketask.domain.model.UserStreak
import com.tta.smartmaketask.domain.repository.StreakRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

class StreakRepositoryImpl(
    private val database: AppDatabase
) : StreakRepository {

    private val queries get() = database.userStreakQueries

    override fun getStreak(): Flow<UserStreak> {
        return queries.getStreak().asFlow().mapToOneOrNull(Dispatchers.Default).map { row ->
            row?.toDomain() ?: UserStreak()
        }
    }

    override suspend fun initStreak() {
        queries.insertOrInitStreak()
    }

    override suspend fun updateStreak(streak: UserStreak) {
        queries.updateStreak(
            currentStreak = streak.currentStreak.toLong(),
            longestStreak = streak.longestStreak.toLong(),
            lastActiveDate = streak.lastActiveDate?.toString(),
            totalCompleted = streak.totalCompleted
        )
    }

    override suspend fun recordTaskCompletion(today: LocalDate) {
        initStreak()
        val current = queries.getStreak().executeAsOneOrNull()?.toDomain() ?: UserStreak()

        val yesterday = today.minus(DatePeriod(days = 1))
        val newStreak = when (current.lastActiveDate) {
            today -> current.currentStreak // Already counted today
            yesterday -> current.currentStreak + 1
            else -> 1 // Reset streak
        }
        val newLongest = maxOf(newStreak, current.longestStreak)

        queries.updateStreak(
            currentStreak = newStreak.toLong(),
            longestStreak = newLongest.toLong(),
            lastActiveDate = today.toString(),
            totalCompleted = current.totalCompleted + 1
        )
    }

    private fun com.tta.smartmaketask.db.UserStreak.toDomain(): UserStreak {
        return UserStreak(
            id = id,
            currentStreak = currentStreak.toInt(),
            longestStreak = longestStreak.toInt(),
            lastActiveDate = lastActiveDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            totalCompleted = totalCompleted
        )
    }
}
