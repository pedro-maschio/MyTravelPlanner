package com.pedro.solutions.mytravelplanning.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pedro.solutions.mytravelplanning.data.repository.TravelRepository
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelScreen
import com.pedro.solutions.mytravelplanning.ui.screens.detail.TravelDetailScreen
import com.pedro.solutions.mytravelplanning.ui.screens.generate.GenerateTravelScreen
import com.pedro.solutions.mytravelplanning.ui.screens.intro.IntroScreen
import com.pedro.solutions.mytravelplanning.ui.screens.main.MainScreen
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject

@Composable
fun AppNavHost(modifier: Modifier = Modifier, repository: TravelRepository = koinInject()) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = if (repository.isShownIntroduction()) TravelRoutes.MainScreen else TravelRoutes.IntroScreen,
    ) {
        composable<TravelRoutes.IntroScreen> { navBackStackEntry ->
            IntroScreen {
                navController.popBackStack()
                navController.navigate(TravelRoutes.MainScreen)
            }
        }

        composable<TravelRoutes.MainScreen> { backStackEntry ->
            MainScreen(onClickFloatingButton = {
                navController.navigate(TravelRoutes.CreateTravelScreen())
            }, onClickTravelItem = { travelId ->
                navController.navigate(TravelRoutes.CreateTravelScreen(travelId = travelId))
            })
        }

        composable<TravelRoutes.CreateTravelScreen> { navBackStackEntry ->
            val travelId =
                navBackStackEntry.toRoute<TravelRoutes.CreateTravelScreen>().travelId

            CreateTravelScreen(
                travelId = travelId,
                onTravelCreated = {
                    navController.popBackStack()
                }, onTravelDeleted = {
                    navController.popBackStack()
                })
        }

        composable<TravelRoutes.GenerateTravelScreen> { backStackEntry ->
            val travelId: TravelRoutes.GenerateTravelScreen = backStackEntry.toRoute()
            GenerateTravelScreen(
                travelId = travelId.id,
                onTravelGenerated = {
                    navController.navigate(
                        TravelRoutes.TravelDetailScreen(
                            travelGuideJson = Json.encodeToString(
                                it
                            )
                        )
                    )
                })
        }

        composable<TravelRoutes.TravelDetailScreen> { backStackEntry ->
            val travelData = backStackEntry.toRoute<TravelRoutes.TravelDetailScreen>()
            TravelDetailScreen(
                travelData = travelData
            )
        }
    }
}