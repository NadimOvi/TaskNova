package com.nadim.tasknova.repository

import com.nadim.tasknova.data.local.ExpenseDao
import com.nadim.tasknova.data.model.Expense
import com.nadim.tasknova.data.model.toEntity
import com.nadim.tasknova.data.model.toModel
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val postgrest: Postgrest
) : ExpenseRepository {

    override fun getAllExpenses(userId: String): Flow<List<Expense>> =
        expenseDao.getAllExpenses(userId).map { it.map { e -> e.toModel() } }

    override fun getExpensesByCategory(userId: String, category: String): Flow<List<Expense>> =
        expenseDao.getExpensesByCategory(userId, category).map { it.map { e -> e.toModel() } }

    override fun getTotalExpenses(userId: String): Flow<Double?> =
        expenseDao.getTotalExpenses(userId)

    override suspend fun saveExpense(expense: Expense) {
        val entity = expense.copy(
            id = expense.id.ifEmpty { UUID.randomUUID().toString() }
        ).toEntity()
        expenseDao.insertExpense(entity)
        try {
            postgrest.from("expenses").upsert(
                mapOf(
                    "id"          to entity.id,
                    "user_id"     to entity.userId,
                    "amount"      to entity.amount,
                    "currency"    to entity.currency,
                    "category"    to entity.category,
                    "description" to entity.description,
                    "spent_at"    to entity.spentAt
                )
            )
        } catch (e: Exception) { }
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense.toEntity())
        try {
            postgrest.from("expenses").delete { filter { eq("id", expense.id) } }
        } catch (e: Exception) { }
    }
}