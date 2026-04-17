package com.nadim.tasknova.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class GmailMessage(
    val raw: String  // base64 encoded email
)

data class GmailResponse(
    val id: String,
    val threadId: String
)

interface GmailApi {
    @POST("gmail/v1/users/{userId}/messages/send")
    suspend fun sendEmail(
        @Path("userId") userId: String = "me",
        @Header("Authorization") token: String,
        @Body message: GmailMessage
    ): GmailResponse
}