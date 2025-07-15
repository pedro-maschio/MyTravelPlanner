package com.pedro.solutions.mytravelplanning.data.repository

import com.pedro.solutions.mytravelplanning.BuildConfig
import com.pedro.solutions.mytravelplanning.data.models.ChatRequest
import com.pedro.solutions.mytravelplanning.data.models.ChatResponse
import com.pedro.solutions.mytravelplanning.data.network.ChatGptApi

class TravelsRepository(private val api: ChatGptApi) {
    suspend fun getChatResponse(chatRequest: ChatRequest): ChatResponse =
        api.getChatResponse("Bearer ${BuildConfig.CHAT_GPT_API_KEY}", chatRequest)

}