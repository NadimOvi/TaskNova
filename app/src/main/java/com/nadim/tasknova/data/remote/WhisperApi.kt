package com.nadim.tasknova.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class WhisperResponse(
    val text: String
)

interface WhisperApi {
    @Multipart
    @POST("v1/audio/transcriptions")
    suspend fun transcribe(
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody
    ): WhisperResponse
}