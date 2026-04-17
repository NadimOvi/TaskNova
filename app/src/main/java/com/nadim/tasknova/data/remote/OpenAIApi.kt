package com.nadim.tasknova.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

data class OpenAIRequest(
    val model: String = "gpt-4o",
    val messages: List<OpenAIMessage>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 1000
)

data class OpenAIMessage(
    val role: String,   // system / user / assistant
    val content: String
)

data class OpenAIResponse(
    val choices: List<Choice>
) {
    data class Choice(val message: OpenAIMessage)
}

interface OpenAIApi {
    @POST("v1/chat/completions")
    suspend fun chat(@Body request: OpenAIRequest): OpenAIResponse
}