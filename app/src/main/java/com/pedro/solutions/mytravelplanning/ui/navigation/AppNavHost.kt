package com.pedro.solutions.mytravelplanning.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelScreen
import com.pedro.solutions.mytravelplanning.ui.screens.detail.TravelDetailScreen
import com.pedro.solutions.mytravelplanning.ui.screens.generate.GenerateTravelScreen
import com.pedro.solutions.mytravelplanning.ui.screens.intro.IntroScreen
import com.pedro.solutions.mytravelplanning.ui.screens.main.MainScreen
import kotlinx.serialization.json.Json

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = TravelsRoutes.MainScreen,
    ) {
        composable<TravelsRoutes.IntroScreen> { navBackStackEntry ->
            IntroScreen {
                navController.popBackStack()
                navController.navigate(TravelsRoutes.GenerateTravelScreen())
            }
        }

        composable<TravelsRoutes.MainScreen> { backStackEntry ->
            MainScreen(onClickFloatingButton = {
                navController.navigate(TravelsRoutes.CreateTravelScreen())
            }, onClickTravelItem = { travelId ->
                navController.navigate(TravelsRoutes.CreateTravelScreen(travelId = travelId))
            })
        }

        composable<TravelsRoutes.CreateTravelScreen> { navBackStackEntry ->
            val travelId =
                navBackStackEntry.toRoute<TravelsRoutes.CreateTravelScreen>().travelId

            CreateTravelScreen(
                travelId = travelId,
                onTravelCreated = {
                    navController.popBackStack()
                }, onTravelDeleted = {
                    navController.popBackStack()
                })
        }

        composable<TravelsRoutes.GenerateTravelScreen> { backStackEntry ->
            val travelId: TravelsRoutes.GenerateTravelScreen = backStackEntry.toRoute()
            GenerateTravelScreen(
                travelId = travelId.id,
                onTravelGenerated = {
                    navController.navigate(
                        TravelsRoutes.TravelDetailScreen(
                            travelGuideJson = Json.encodeToString(
                                it
                            )
                        )
                    )
                })
        }

        composable<TravelsRoutes.TravelDetailScreen> { backStackEntry ->
            val travelData = backStackEntry.toRoute<TravelsRoutes.TravelDetailScreen>()
            TravelDetailScreen(
                travelData = travelData
            )
        }
    }
}