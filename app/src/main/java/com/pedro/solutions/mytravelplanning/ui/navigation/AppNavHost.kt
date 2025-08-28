package com.pedro.solutions.mytravelplanning.ui.navigation

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pedro.solutions.mytravelplanning.data.repository.TravelRepository
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelScreen
import com.pedro.solutions.mytravelplanning.ui.screens.detail.TravelDetailScreen
import com.pedro.solutions.mytravelplanning.ui.screens.main.MainScreen
import org.koin.compose.koinInject

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(modifier: Modifier = Modifier, repository: TravelRepository = koinInject()) {
    val navController = rememberNavController()
    val activity = LocalActivity.current
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = TravelRoutes.MainScreen
    ) {
        composable<TravelRoutes.MainScreen> { backStackEntry ->
            MainScreen(onClickFloatingButton = {
                navController.navigate(TravelRoutes.CreateTravelScreen())
            }, onClickTravelItem = { travelId ->
                navController.navigate(TravelRoutes.CreateTravelScreen(travelId = travelId))
            }, onGoBack = {
                if (!navController.popBackStack()) {
                    activity?.finish()
                }
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

        composable<TravelRoutes.TravelDetailScreen> { backStackEntry ->
            val travelData = backStackEntry.toRoute<TravelRoutes.TravelDetailScreen>()
            TravelDetailScreen(
                travelData = travelData
            )
        }
    }
}