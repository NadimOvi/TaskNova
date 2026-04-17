package com.nadim.tasknova.repository

import com.nadim.tasknova.data.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpenses(userId: String): Flow<List<Expense>>
    fun getExpensesByCategory(userId: String, category: String): Flow<List<Expense>>
    fun getTotalExpenses(userId: String): Flow<Double?>
    suspend fun saveExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
}