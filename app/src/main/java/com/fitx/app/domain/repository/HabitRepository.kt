package com.fitx.app.domain.repository

import com.fitx.app.domain.model.Habit
import com.fitx.app.domain.model.HabitProgress
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun observeHabitsWithProgress(dateEpochDay: Long): Flow<List<HabitProgress>>
    suspend fun addHabit(habit: Habit)
    suspend fun incrementHabit(habitId: Long, dateEpochDay: Long)
}

