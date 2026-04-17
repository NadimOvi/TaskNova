package com.nadim.tasknova.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadim.tasknova.data.model.Reminder
import com.nadim.tasknova.repository.AuthRepository
import com.nadim.tasknova.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    private val _selectedFilter = MutableStateFlow("all")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    init {
        loadReminders()
    }

    fun loadReminders() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            val filter = _selectedFilter.value
            if (filter == "all") {
                reminderRepository.getAllReminders(userId).collect {
                    _reminders.value = it
                }
            } else {
                reminderRepository.getRemindersByStatus(userId, filter).collect {
                    _reminders.value = it
                }
            }
        }
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
        loadReminders()
    }

    fun markDone(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.updateReminderStatus(reminder.id, "done")
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(reminder)
        }
    }
}