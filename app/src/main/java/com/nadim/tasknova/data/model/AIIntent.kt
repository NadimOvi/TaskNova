package com.nadim.tasknova.data.model

data class AIIntent(
    val type: String,           // task / reminder / note / expense / email / chat
    val title: String? = null,
    val description: String? = null,
    val priority: String? = null,
    val dueDate: String? = null,
    val remindAt: String? = null,
    val recurrence: String? = null,
    val amount: Double? = null,
    val category: String? = null,
    val emailTone: String? = null,
    val emailBody: String? = null,
    val rawResponse: String? = null,
    val needsConfirmation: Boolean = true
)