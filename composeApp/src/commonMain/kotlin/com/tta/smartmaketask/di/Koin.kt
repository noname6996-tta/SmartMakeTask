package com.tta.smartmaketask.di

import com.tta.smartmaketask.data.repository.DailyTaskRepositoryImpl
import com.tta.smartmaketask.data.repository.GoalTaskRepositoryImpl
import com.tta.smartmaketask.data.repository.QuickTaskRepositoryImpl
import com.tta.smartmaketask.data.repository.StreakRepositoryImpl
import com.tta.smartmaketask.domain.repository.DailyTaskRepository
import com.tta.smartmaketask.domain.repository.GoalTaskRepository
import com.tta.smartmaketask.domain.repository.QuickTaskRepository
import com.tta.smartmaketask.domain.repository.StreakRepository
import com.tta.smartmaketask.screen.main.daily.DailyTaskViewModel
import com.tta.smartmaketask.screen.main.goal.GoalTaskViewModel
import com.tta.smartmaketask.screen.main.home.HomeViewModel
import com.tta.smartmaketask.screen.main.profile.ProfileViewModel
import com.tta.smartmaketask.screen.main.quick.QuickTaskViewModel
import com.tta.smartmaketask.db.AppDatabase
import com.tta.smartmaketask.db.DatabaseDriverFactory
import org.koin.dsl.module

val appModule = module {
    // Database
    single<AppDatabase> { 
        val driverFactory = get<DatabaseDriverFactory>()
        AppDatabase(driverFactory.createDriver()) 
    }

    // Repositories
    single<GoalTaskRepository> { GoalTaskRepositoryImpl(get()) }
    single<DailyTaskRepository> { DailyTaskRepositoryImpl(get()) }
    single<QuickTaskRepository> { QuickTaskRepositoryImpl(get()) }
    single<StreakRepository> { StreakRepositoryImpl(get()) }

    // ViewModels
    factory { HomeViewModel(get(), get(), get(), get()) }
    factory { GoalTaskViewModel(get()) }
    factory { DailyTaskViewModel(get(), get()) }
    factory { QuickTaskViewModel(get(), get()) }
    factory { ProfileViewModel(get(), get(), get(), get()) }
}