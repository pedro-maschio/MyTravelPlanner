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
import com.pedro.solutions.mytravelplanning.data.models.TravelType
import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide
import com.pedro.solutions.mytravelplanning.data.repository.TravelsRepository
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelScreen
import com.pedro.solutions.mytravelplanning.ui.screens.generate.GenerateTravelScreen
import com.pedro.solutions.mytravelplanning.ui.screens.detail.TravelDetailScreen
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
                val travelGuide =
                    backStackEntry.toRoute<TravelsRoutes.TravelDetailScreen>().travelGuideJson
                val travelItems = mutableListOf<TravelType>()
                try {
                    Json.decodeFromString<TravelGuide>(travelGuide).days.forEachIndexed { index, item ->
                        travelItems.add(
                            TravelType.Day(
                                index = index,
                                title = item?.title.orEmpty()
                            )
                        )
                        item?.activities?.forEachIndexed { activityIndex, activity ->
                            travelItems.add(
                                TravelType.Activity(
                                    index = activityIndex,
                                    dayIndex = index,
                                    title = activity
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    // TODO: Log this, this should never happen
                }
//                val mockedTravelItems = listOf(
//                    TravelType.Day(index = 0, title = "Dia 1: Chegada e Centro Histórico"),
//                    TravelType.Activity(dayIndex = 0, "Saída de Brasília cedo para chegar antes do almoço."),
//                    TravelType.Activity(dayIndex = 0,"Check-in em uma pousada charmosa, como Pousada dos Pirineus ou Casarão Villa do Império."),
//                    TravelType.Day(index = 1, title = "Dia 2: Cachoeiras e Serra dos Pireneus"),
//                    TravelType.Activity(dayIndex = 1,"Café da manhã reforçado na pousada."),
//                    TravelType.Activity(dayIndex = 1,"Visita à Cachoeira do Abade (trilha leve, ótima para banho)"),
//                    TravelType.Activity(dayIndex = 1,"Alternativa: Cachoeira Meia Lua (mais próxima do centro)."),
//                    TravelType.Activity(dayIndex = 1,"Restaurante Fazenda Babilônia (famoso por seu café colonial e comida típica)."),
//                    TravelType.Day(index = 2, title = "Dia 3: Últimas Cachoeiras e Despedida"),
//                    TravelType.Activity(dayIndex = 2,"Café da manhã e checkout da pousada."),
//                    TravelType.Activity(dayIndex = 2,"Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."),
//                    TravelType.Activity(dayIndex = 2,"Visita à Cachoeira Bonsucesso (conjunto de quedas d’água com trilhas fáceis)."),
//                    TravelType.Activity(dayIndex = 2,"Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."),
//                    TravelType.Activity(dayIndex = 2,"Caminhada leve pelo centro para últimas compras (artesanato, licores e doces)."),
//                    TravelType.Day(index = 3, title = "Dia 1: Chegada e Centro Histórico"),
//                    TravelType.Activity(dayIndex = 3,"Saída de Brasília cedo para chegar antes do almoço."),
//                    TravelType.Activity(dayIndex = 3,"Check-in em uma pousada charmosa, como Pousada dos Pirineus ou Casarão Villa do Império."),
//                    TravelType.Day(index = 4, title = "Dia 2: Cachoeiras e Serra dos Pireneus"),
//                    TravelType.Activity(dayIndex = 4,"Café da manhã reforçado na pousada."),
//                    TravelType.Activity(dayIndex = 4,"Visita à Cachoeira do Abade (trilha leve, ótima para banho)"),
//                    TravelType.Activity(dayIndex = 4,"Alternativa: Cachoeira Meia Lua (mais próxima do centro)."),
//                    TravelType.Activity(dayIndex = 4,"Restaurante Fazenda Babilônia (famoso por seu café colonial e comida típica)."),
//                    TravelType.Day(index = 5, title = "Dia 3: Últimas Cachoeiras e Despedida"),
//                    TravelType.Activity(dayIndex = 5,"Café da manhã e checkout da pousada."),
//                    TravelType.Activity(dayIndex = 5,"Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."),
//                    TravelType.Activity(dayIndex = 5,"Visita à Cachoeira Bonsucesso (conjunto de quedas d’água com trilhas fáceis)."),
//                    TravelType.Activity(dayIndex = 5,"Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."),
//                    TravelType.Activity(dayIndex = 5,"Caminhada leve pelo centro para últimas compras (artesanato, licores e doces)."),
//                )
                TravelDetailScreen(travelItems = travelItems)
            }
            composable<TravelsRoutes.MainScreen> { backStackEntry ->

                MainScreen()
            }
        }
    }

}