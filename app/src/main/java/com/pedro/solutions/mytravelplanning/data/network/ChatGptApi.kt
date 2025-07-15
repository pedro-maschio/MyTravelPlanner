package com.pedro.solutions.mytravelplanning.data.network

import com.pedro.solutions.mytravelplanning.data.models.ChatRequest
import com.pedro.solutions.mytravelplanning.data.models.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatGptApi {
    @POST("chat/completions")
    suspend fun getChatResponse(
        @Header("Authorization") auth: String,
        @Body request: ChatRequest
    ): ChatResponse

}