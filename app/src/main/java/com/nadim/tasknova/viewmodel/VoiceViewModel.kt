package com.nadim.tasknova.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadim.tasknova.data.local.ConversationEntity
import com.nadim.tasknova.data.model.AIIntent
import com.nadim.tasknova.data.model.Expense
import com.nadim.tasknova.data.model.Note
import com.nadim.tasknova.data.model.Reminder
import com.nadim.tasknova.data.model.Task
import com.nadim.tasknova.repository.AuthRepository
import com.nadim.tasknova.repository.ExpenseRepository
import com.nadim.tasknova.repository.NoteRepository
import com.nadim.tasknova.repository.ReminderRepository
import com.nadim.tasknova.repository.TaskRepository
import com.nadim.tasknova.service.AIConversationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class VoiceState {
    object Idle : VoiceState()
    object Listening : VoiceState()
    object Processing : VoiceState()
    data class Speaking(val text: String) : VoiceState()
    data class AwaitingConfirmation(
        val intent: AIIntent,
        val confirmationText: String
    ) : VoiceState()
    data class ActionSaved(val message: String) : VoiceState()
    data class Error(val message: String) : VoiceState()
}

@HiltViewModel
class VoiceViewModel @Inject constructor(
    private val aiConversationService: AIConversationService,
    private val taskRepository: TaskRepository,
    private val reminderRepository: ReminderRepository,
    private val noteRepository: NoteRepository,
    private val expenseRepository: ExpenseRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _voiceState = MutableStateFlow<VoiceState>(VoiceState.Idle)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _transcript = MutableStateFlow("")
    val transcript: StateFlow<String> = _transcript.asStateFlow()

    private val _ariaResponse = MutableStateFlow("")
    val ariaResponse: StateFlow<String> = _ariaResponse.asStateFlow()

    private val _conversationHistory = MutableStateFlow<List<ConversationEntity>>(emptyList())
    val conversationHistory: StateFlow<List<ConversationEntity>> = _conversationHistory.asStateFlow()

    private var pendingIntent: AIIntent? = null

    // Called when user speaks
    fun onUserSpoke(text: String) {
        _transcript.value = text
        viewModelScope.launch {
            _voiceState.value = VoiceState.Processing
            val userId = authRepository.currentUserId ?: return@launch

            // Send to AI and get intent back
            val result = aiConversationService.processUserMessage(
                userId = userId,
                userMessage = text
            )

            result.fold(
                onSuccess = { intent ->
                    when {
                        intent.needsConfirmation -> {
                            pendingIntent = intent
                            val confirmText = buildConfirmationText(intent)
                            _ariaResponse.value = confirmText
                            _voiceState.value = VoiceState.AwaitingConfirmation(
                                intent = intent,
                                confirmationText = confirmText
                            )
                        }
                        else -> {
                            val response = aiConversationService.getChatResponse(
                                userId = userId,
                                userMessage = text
                            )
                            _ariaResponse.value = response
                            _voiceState.value = VoiceState.Speaking(response)
                        }
                    }
                },
                onFailure = {
                    _voiceState.value = VoiceState.Error(it.message ?: "Something went wrong")
                }
            )
        }
    }

    // Called when user confirms
    fun onConfirmed() {
        val intent = pendingIntent ?: return
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            saveIntent(userId, intent)
            pendingIntent = null
        }
    }

    // Called when user rejects
    fun onRejected() {
        pendingIntent = null
        _ariaResponse.value = "No problem! What else can I help you with?"
        _voiceState.value = VoiceState.Speaking("No problem! What else can I help you with?")
    }

    private suspend fun saveIntent(userId: String, intent: AIIntent) {
        when (intent.type) {
            "task" -> {
                val task = Task(
                    id          = UUID.randomUUID().toString(),
                    userId      = userId,
                    title       = intent.title ?: "New Task",
                    description = intent.description,
                    priority    = intent.priority ?: "medium",
                    status      = "pending"
                )
                taskRepository.saveTask(task)
                val msg = "Task saved! '${task.title}'"
                _ariaResponse.value = msg
                _voiceState.value = VoiceState.ActionSaved(msg)
            }
            "reminder" -> {
                val reminder = Reminder(
                    id         = UUID.randomUUID().toString(),
                    userId     = userId,
                    title      = intent.title ?: "New Reminder",
                    type       = "time",
                    status     = "pending",
                    recurrence = intent.recurrence
                )
                reminderRepository.saveReminder(reminder)
                val msg = "Reminder set! '${reminder.title}'"
                _ariaResponse.value = msg
                _voiceState.value = VoiceState.ActionSaved(msg)
            }
            "note" -> {
                val note = Note(
                    id      = UUID.randomUUID().toString(),
                    userId  = userId,
                    title   = intent.title,
                    content = intent.description ?: ""
                )
                noteRepository.saveNote(note)
                val msg = "Note saved!"
                _ariaResponse.value = msg
                _voiceState.value = VoiceState.ActionSaved(msg)
            }
            "expense" -> {
                val expense = Expense(
                    id          = UUID.randomUUID().toString(),
                    userId      = userId,
                    amount      = intent.amount ?: 0.0,
                    category    = intent.category,
                    description = intent.description
                )
                expenseRepository.saveExpense(expense)
                val msg = "Expense logged! ${intent.amount} ${intent.category ?: ""}"
                _ariaResponse.value = msg
                _voiceState.value = VoiceState.ActionSaved(msg)
            }
            else -> {
                _voiceState.value = VoiceState.Speaking(_ariaResponse.value)
            }
        }
    }

    private fun buildConfirmationText(intent: AIIntent): String {
        return when (intent.type) {
            "task"     -> "Got it! Should I save a task: '${intent.title}'?"
            "reminder" -> "Should I set a reminder: '${intent.title}'?"
            "note"     -> "Should I save this note: '${intent.title ?: intent.description}'?"
            "expense"  -> "Should I log ${intent.amount} for ${intent.category}?"
            else       -> "Should I do that?"
        }
    }

    fun resetToIdle() {
        _voiceState.value = VoiceState.Idle
        _transcript.value = ""
    }
}