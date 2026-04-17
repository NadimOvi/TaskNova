package com.nadim.tasknova.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadim.tasknova.data.model.Task
import com.nadim.tasknova.repository.AuthRepository
import com.nadim.tasknova.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _selectedFilter = MutableStateFlow("all")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            val filter = _selectedFilter.value
            if (filter == "all") {
                taskRepository.getAllTasks(userId).collect {
                    _tasks.value = it
                }
            } else {
                taskRepository.getTasksByStatus(userId, filter).collect {
                    _tasks.value = it
                }
            }
        }
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
        loadTasks()
    }

    fun markDone(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTaskStatus(task.id, "done")
        }
    }

    fun markMissed(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTaskStatus(task.id, "missed")
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}