package com.nadim.tasknova.data.model

data class Expense(
    val id: String,
    val userId: String,
    val amount: Double,
    val currency: String = "EUR",
    val category: String? = null,
    val description: String? = null,
    val spentAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

fun com.nadim.tasknova.data.local.ExpenseEntity.toModel() = Expense(
    id = id, userId = userId, amount = amount,
    currency = currency, category = category,
    description = description, spentAt = spentAt,
    createdAt = createdAt
)

fun Expense.toEntity() = com.nadim.tasknova.data.local.ExpenseEntity(
    id = id, userId = userId, amount = amount,
    currency = currency, category = category,
    description = description, spentAt = spentAt,
    createdAt = createdAt
)