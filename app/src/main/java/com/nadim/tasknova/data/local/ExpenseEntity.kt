package com.nadim.tasknova.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Double,
    val currency: String = "EUR",
    val category: String? = null,      // food / travel / work / health / other
    val description: String? = null,
    val spentAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)