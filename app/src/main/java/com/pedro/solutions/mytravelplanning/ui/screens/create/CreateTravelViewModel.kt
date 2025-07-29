package com.pedro.solutions.mytravelplanning.ui.screens.create

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pedro.solutions.mytravelplanning.data.models.ChatRequest
import com.pedro.solutions.mytravelplanning.data.models.Message
import com.pedro.solutions.mytravelplanning.data.models.openai.ErrorMessage
import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide
import com.pedro.solutions.mytravelplanning.data.repository.TravelsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateTravelViewModel(private val repository: TravelsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateTravelUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<CreateTravelEvents>()
    val uiEvents: SharedFlow<CreateTravelEvents> = _uiEvents.asSharedFlow()

    private fun setLoading() = _uiState.update { it.copy(isLoading = true) }
    private fun hideLoading() = _uiState.update { it.copy(isLoading = false) }


    fun createTravel() {
        val startingPoint = _uiState.value.travel.startingPoint
        val endingPoint = _uiState.value.travel.endingPoint
        val duration = _uiState.value.travel.durationInDays
        val request = ChatRequest(
            messages = listOf(
                Message(
                    "system", """
                You are a helpful travel assistant specialized in generating car trip guides.
                
                You will receive a request in the format: 
                "Create a travel guide that starts from [STARTING LOCATION] to [ENDING LOCATION] and must be completed in [DURATION] day(s)."
                
                Your response must be a **valid JSON object** that strictly follows this schema:
                
                {
                  "days": [
                    {
                      "title": "Dia 1 – Brasília → Cavalcante (320 km, ~4h30 de viagem)",
                      "activities": [
                        "Saída cedo de Brasília (6h-7h da manhã).",
                        "Parada em Alto Paraíso para um café e lanche no Ateliê da Pizza.",
                        "Chegada em Cavalcante por volta do meio-dia."
                      ]
                    },
                    {
                      "title": "Dia 2 – Cavalcante → Terra Ronca (180 km, ~4h de viagem, estradas de terra)",
                      "activities": [
                        "Café da manhã reforçado e saída cedo (~7h).",
                        "Viagem até São Domingos (GO), base para o Parque Estadual Terra Ronca."
                      ]
                    }
                  ]
                }

                If it is not possible to generate a guide (e.g. too short time or impossible route), return an error message
                in the format:
                {
                  "message": "Concise explanation for WHY it is not possible to generate a guide."
                }

                Keep responses concise and useful. Do not include explanations or additional text.
            """.trimIndent()
                ),
                Message(
                    "user",
                    "Create a travel guide that starts from $startingPoint to $endingPoint and must be completed in $duration day ${if (duration != "1") "s" else ""}."
                )
            )
        )

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val response = repository.getChatResponse(request)
                val content = response.choices[0].message?.content
                val travelGuide = Gson().fromJson(content, TravelGuide::class.java)
                Log.d("PEDRO123", travelGuide.toString())
                if(travelGuide.days == null) {
                    val errorMessage = Gson().fromJson(content, ErrorMessage::class.java)
                    _uiState.update { it.copy(showErrorScreen = false, errorMessage = errorMessage.message   ) }
                } else {
                    _uiState.update { it.copy(showErrorScreen = false, errorMessage = "") }
                    _uiEvents.emit(CreateTravelEvents.GoToListing(travelGuide))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(showErrorScreen = true) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateStartingPoint(name: String) {
        _uiState.update { it.copy(travel = it.travel.copy(startingPoint = name)) }
    }

    fun updateEndingPoint(name: String) {
        _uiState.update { it.copy(travel = it.travel.copy(endingPoint = name)) }
    }

    fun updateDuration(duration: String) {
        _uiState.update { it.copy(travel = it.travel.copy(durationInDays = duration)) }
    }

    fun updateDetails(details: String) {
        _uiState.update { it.copy(it.travel.copy(details = details)) }
    }

}