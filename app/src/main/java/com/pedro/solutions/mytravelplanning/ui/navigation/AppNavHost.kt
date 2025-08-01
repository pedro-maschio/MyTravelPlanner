package com.pedro.solutions.mytravelplanning.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide
import com.pedro.solutions.mytravelplanning.data.repository.TravelsRepository
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelScreen
import com.pedro.solutions.mytravelplanning.ui.screens.generate.GenerateTravelScreen
import com.pedro.solutions.mytravelplanning.ui.screens.detail.TravelDetailScreen
import com.pedro.solutions.mytravelplanning.ui.screens.detail.TravelItem
import com.pedro.solutions.mytravelplanning.ui.screens.intro.IntroScreen
import com.pedro.solutions.mytravelplanning.ui.screens.main.MainScreen
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RestrictedApi")
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntries by navController.currentBackStack.collectAsState()
    val isOnMainScreen = false
    val repository: TravelsRepository = koinInject()
    val startDestination = TravelsRoutes.CreateTravelScreen
        //if (repository.isShownIntroduction()) TravelsRoutes.GenerateTravelScreen() else TravelsRoutes.IntroScreen

    Scaffold(modifier = modifier, topBar = {
        CenterAlignedTopAppBar(title = {
            if (isOnMainScreen) {
                Text(text = stringResource(R.string.travels_listing_title))
            }
        }, navigationIcon = {
            if (!isOnMainScreen) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        }
        )
    }, floatingActionButton = {
        if (isOnMainScreen) {
            FloatingActionButton(onClick = {
                navController.navigate(TravelsRoutes.GenerateTravelScreen())
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = startDestination,
        ) {

            composable<TravelsRoutes.IntroScreen> { navBackStackEntry ->
                IntroScreen {
                    navController.popBackStack()
                    navController.navigate(TravelsRoutes.GenerateTravelScreen())
                }
            }

            composable<TravelsRoutes.CreateTravelScreen> { navBackStackEntry ->
                CreateTravelScreen() {

                }
            }

            composable<TravelsRoutes.GenerateTravelScreen> { backStackEntry ->
                val travelId: TravelsRoutes.GenerateTravelScreen = backStackEntry.toRoute()
                GenerateTravelScreen(travelId = travelId.id, onTravelGenerated = {
                    navController.navigate(TravelsRoutes.TravelDetailScreen(Json.encodeToString(it)))
                })
            }

            composable<TravelsRoutes.TravelDetailScreen> { backStackEntry ->
                val travelGuide = backStackEntry.toRoute<TravelsRoutes.TravelDetailScreen>().travelGuideJson
                val travelItems = mutableListOf<TravelItem>()
                try {
                    Json.decodeFromString<TravelGuide>(travelGuide).days?.forEachIndexed { index, item ->
                        travelItems.add(
                            TravelItem.Day(
                                index = index,
                                title = item?.title.orEmpty()
                            )
                        )
                        item?.activities?.forEach { activity ->
                            travelItems.add(TravelItem.Activity(activity))
                        }
                    }
                } catch (e: Exception) {
                    // TODO: Log this, this should never happen
                }
                val mockedTravelItems = listOf(
                    TravelItem.Day(index = 0, title = "Dia 1: Chegada e Centro Histórico"),
                    TravelItem.Activity("Saída de Brasília cedo para chegar antes do almoço."),
                    TravelItem.Activity("Check-in em uma pousada charmosa, como Pousada dos Pirineus ou Casarão Villa do Império."),
                    TravelItem.Day(index = 1, title = "Dia 2: Cachoeiras e Serra dos Pireneus"),
                    TravelItem.Activity("Café da manhã reforçado na pousada."),
                    TravelItem.Activity("Visita à Cachoeira do Abade (trilha leve, ótima para banho)"),
                    TravelItem.Activity("Alternativa: Cachoeira Meia Lua (mais próxima do centro)."),
                    TravelItem.Activity("Restaurante Fazenda Babilônia (famoso por seu café colonial e comida típica)."),
                    TravelItem.Day(index = 2, title = "Dia 3: Últimas Cachoeiras e Despedida"),
                    TravelItem.Activity("Café da manhã e checkout da pousada."),
                    TravelItem.Activity("Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."),
                    TravelItem.Activity("Visita à Cachoeira Bonsucesso (conjunto de quedas d’água com trilhas fáceis)."),
                    TravelItem.Activity("Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."),
                    TravelItem.Activity("Caminhada leve pelo centro para últimas compras (artesanato, licores e doces)."),
                    TravelItem.Day(index = 3, title = "Dia 1: Chegada e Centro Histórico"),
                    TravelItem.Activity("Saída de Brasília cedo para chegar antes do almoço."),
                    TravelItem.Activity("Check-in em uma pousada charmosa, como Pousada dos Pirineus ou Casarão Villa do Império."),
                    TravelItem.Day(index = 4, title = "Dia 2: Cachoeiras e Serra dos Pireneus"),
                    TravelItem.Activity("Café da manhã reforçado na pousada."),
                    TravelItem.Activity("Visita à Cachoeira do Abade (trilha leve, ótima para banho)"),
                    TravelItem.Activity("Alternativa: Cachoeira Meia Lua (mais próxima do centro)."),
                    TravelItem.Activity("Restaurante Fazenda Babilônia (famoso por seu café colonial e comida típica)."),
                    TravelItem.Day(index = 5, title = "Dia 3: Últimas Cachoeiras e Despedida"),
                    TravelItem.Activity("Café da manhã e checkout da pousada."),
                    TravelItem.Activity("Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."),
                    TravelItem.Activity("Visita à Cachoeira Bonsucesso (conjunto de quedas d’água com trilhas fáceis)."),
                    TravelItem.Activity("Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."),
                    TravelItem.Activity("Caminhada leve pelo centro para últimas compras (artesanato, licores e doces)."),
                )
                TravelDetailScreen(travelItems = travelItems)
            }
            composable<TravelsRoutes.MainScreen> { backStackEntry ->

                MainScreen()
            }
        }
    }

}