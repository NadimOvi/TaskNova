package com.nadim.tasknova.service

import com.google.gson.Gson
import com.nadim.tasknova.data.local.ConversationDao
import com.nadim.tasknova.data.local.ConversationEntity
import com.nadim.tasknova.data.model.AIIntent
import com.nadim.tasknova.data.remote.OpenAIApi
import com.nadim.tasknova.data.remote.OpenAIMessage
import com.nadim.tasknova.data.remote.OpenAIRequest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIConversationService @Inject constructor(
    private val openAIApi: OpenAIApi,
    private val conversationDao: ConversationDao,
    private val gson: Gson
) {

    private val systemPrompt = """
        You are Aria, a smart AI productivity assistant inside TaskNova app.
        Your job is to understand what the user wants and extract structured data.
        
        When user wants to create a task, reminder, note, or log an expense — respond ONLY with JSON.
        When user is just chatting — respond naturally in plain text.
        
        JSON format for actions:
        {
          "type": "task|reminder|note|expense|email|chat",
          "title": "...",
          "description": "...",
          "priority": "low|medium|high",
          "due_date": "YYYY-MM-DD HH:mm or null",
          "remind_at": "YYYY-MM-DD HH:mm or null",
          "recurrence": "daily|weekly|null",
          "amount": 0.0,
          "category": "food|travel|work|health|other|null",
          "email_tone": "formal|casual|professional|null",
          "needs_confirmation": true
        }
        
        Always confirm before saving. Be friendly and conversational.
        Keep responses short and natural.
    """.trimIndent()

    suspend fun processUserMessage(
        userId: String,
        userMessage: String
    ): Result<AIIntent> {
        return try {
            // Save user message to local DB
            conversationDao.insertMessage(
                ConversationEntity(
                    id        = UUID.randomUUID().toString(),
                    userId    = userId,
                    role      = "user",
                    content   = userMessage,
                    createdAt = System.currentTimeMillis()
                )
            )

            // Get recent history for context
            val history = conversationDao.getRecentMessages(userId)

            // Build messages list
            val messages = mutableListOf(
                OpenAIMessage(role = "system", content = systemPrompt)
            )
            history.reversed().forEach { msg ->
                messages.add(OpenAIMessage(role = msg.role, content = msg.content))
            }

            // Call OpenAI
            val response = openAIApi.chat(
                OpenAIRequest(
                    model    = "gpt-4o",
                    messages = messages
                )
            )

            val aiText = response.choices.firstOrNull()?.message?.content ?: ""

            // Save AI response
            conversationDao.insertMessage(
                ConversationEntity(
                    id        = UUID.randomUUID().toString(),
                    userId    = userId,
                    role      = "assistant",
                    content   = aiText,
                    createdAt = System.currentTimeMillis()
                )
            )

            // Try to parse as JSON intent
            val intent = tryParseIntent(aiText)
            Result.success(intent)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChatResponse(
        userId: String,
        userMessage: String
    ): String {
        return try {
            val history = conversationDao.getRecentMessages(userId)
            val messages = mutableListOf(
                OpenAIMessage(role = "system", content = systemPrompt)
            )
            history.reversed().forEach {
                messages.add(OpenAIMessage(role = it.role, content = it.content))
            }
            val response = openAIApi.chat(OpenAIRequest(messages = messages))
            response.choices.firstOrNull()?.message?.content ?: "I didn't catch that."
        } catch (e: Exception) {
            "Sorry, I'm having trouble connecting right now."
        }
    }

    private fun tryParseIntent(text: String): AIIntent {
        return try {
            // Clean JSON from markdown if present
            val clean = text
                .replace("```json", "")
                .replace("```", "")
                .trim()

            if (clean.startsWith("{")) {
                val map = gson.fromJson(clean, Map::class.java)
                AIIntent(
                    type             = map["type"]?.toString() ?: "chat",
                    title            = map["title"]?.toString(),
                    description      = map["description"]?.toString(),
                    priority         = map["priority"]?.toString(),
                    dueDate          = map["due_date"]?.toString(),
                    remindAt         = map["remind_at"]?.toString(),
                    recurrence       = map["recurrence"]?.toString(),
                    amount           = (map["amount"] as? Double),
                    category         = map["category"]?.toString(),
                    emailTone        = map["email_tone"]?.toString(),
                    rawResponse      = text,
                    needsConfirmation = map["needs_confirmation"] as? Boolean ?: true
                )
            } else {
                // Plain chat response
                AIIntent(
                    type             = "chat",
                    rawResponse      = text,
                    needsConfirmation = false
                )
            }
        } catch (e: Exception) {
            AIIntent(
                type             = "chat",
                rawResponse      = text,
                needsConfirmation = false
            )
        }
    }
}