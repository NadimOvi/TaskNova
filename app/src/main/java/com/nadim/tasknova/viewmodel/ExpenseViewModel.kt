package com.nadim.tasknova.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadim.tasknova.data.model.Expense
import com.nadim.tasknova.repository.AuthRepository
import com.nadim.tasknova.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount: StateFlow<Double> = _totalAmount.asStateFlow()

    private val _selectedCategory = MutableStateFlow("all")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init {
        loadExpenses()
    }

    fun loadExpenses() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            if (_selectedCategory.value == "all") {
                expenseRepository.getAllExpenses(userId).collect {
                    _expenses.value = it
                }
            } else {
                expenseRepository.getExpensesByCategory(
                    userId,
                    _selectedCategory.value
                ).collect {
                    _expenses.value = it
                }
            }
        }
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            expenseRepository.getTotalExpenses(userId).collect {
                _totalAmount.value = it ?: 0.0
            }
        }
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
        loadExpenses()
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
        }
    }
}