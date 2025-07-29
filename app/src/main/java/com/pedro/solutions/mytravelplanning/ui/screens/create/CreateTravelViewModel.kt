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

    private fun showLoading() = _uiState.update { it.copy(isLoading = true) }
    private fun hideLoading() = _uiState.update { it.copy(isLoading = false) }
    fun createTravel() {
        val startingPoint = _uiState.value.travel.startingPoint
        val endingPoint = _uiState.value.travel.endingPoint
        val duration = _uiState.value.travel.durationInDays
        val request = ChatRequest(
            messages = listOf(
                Message(
                    "system", """
                Você é um assistente de viagens que cria roteiros detalhados saindo de $startingPoint. Gere um roteiro em formato JSON, sem explicações adicionais, seguindo estritamente esta estrutura:
                 {
                   "days": [
                     {
                       "title": "Dia 1: <resumo do dia>",
                       "activities": [
                         "<atividade 1>",
                         "<atividade 2>",
                         "<atividade 3>"
                       ]
                     }
                   ]
                 }
                Regras:
                Cada dia deve ter um título resumido.
                Cada dia deve ter entre 3 e 6 atividades.
                O roteiro deve ser para uma viagem de carro saindo de $startingPoint.
                Personalize conforme o destino, incluindo restaurantes, hospedagens e pontos turísticos relevantes.
                Não adicione texto fora do JSON.

                Exemplo de entrada:
                "Gerar roteiro de 3 dias para Pirenópolis para um casal que gosta de natureza e boa gastronomia."
                Se a duracao for maior do que o necessário, crie um roteiro de viagem ainda assim, adicionando mais atividades, destinos próximos ou dias livres para descansar. Apenas retorne erro se a viagem é fisicamente impossível (por exemplo, não há rotas de carro)
                Em caso de erro, retorne um JSON estritamente neste formato:
                {
                  "message": "Explicacao concisa do PORQUE não ser possível gerar o roteiro."
                }
            """.trimIndent()
                ),
                Message(
                    "user",
                    "Crie um roteiro de viagem que comeca em $startingPoint até $endingPoint e deve ser completada em $duration dia${if (duration != "1") "s" else ""}."
                )
            )
        )

        viewModelScope.launch {
            try {
                showLoading()
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
                hideLoading()
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