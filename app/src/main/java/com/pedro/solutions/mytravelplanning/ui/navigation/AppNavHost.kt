package com.pedro.solutions.mytravelplanning.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelScreen
import com.pedro.solutions.mytravelplanning.ui.screens.detail.TravelDetailScreen
import com.pedro.solutions.mytravelplanning.ui.screens.generate.GenerateTravelScreen
import com.pedro.solutions.mytravelplanning.ui.screens.intro.IntroScreen
import com.pedro.solutions.mytravelplanning.ui.screens.main.MainScreen
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RestrictedApi")
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isDropDownMenuShowing = remember { mutableStateOf(false) }
    val isEditing = remember { mutableStateOf(false) }
    val createTravel = remember { mutableStateOf(false) }
    val deleteTravel = remember { mutableStateOf(false) }
    val isOnCreateTravelScreen =
        navBackStackEntry?.destination?.hasRoute(TravelsRoutes.CreateTravelScreen::class) == true
    val isOnMainScreen =
        navBackStackEntry?.destination?.hasRoute(TravelsRoutes.MainScreen::class) == true


    @Composable
    fun TravelAppBar(modifier: Modifier = Modifier) {
        TopAppBar(
            modifier = modifier,
            title = {
                val topBarTitle = when {
                    navBackStackEntry?.destination?.hasRoute(TravelsRoutes.MainScreen::class) == true ->
                        stringResource(R.string.travels_listing_title)

                    navBackStackEntry?.destination?.hasRoute(TravelsRoutes.CreateTravelScreen::class) == true ->
                        stringResource(R.string.create_travel_title)

                    navBackStackEntry?.destination?.hasRoute(TravelsRoutes.GenerateTravelScreen::class) == true -> stringResource(
                        R.string.generate_travel_title
                    )

                    navBackStackEntry?.destination?.hasRoute(TravelsRoutes.TravelDetailScreen::class) == true ->
                        "TODO"

                    else -> "TODO"
                }
                Text(text = topBarTitle)
            }, navigationIcon = {
                if (!isOnMainScreen) {
                    IconButton(onClick = {
                        if (isOnCreateTravelScreen) {
                            // Pop from backstack is handled in onTravelCreated callback
                            createTravel.value = true
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            }, actions = {
                if (isOnCreateTravelScreen) {
                    IconButton(onClick = {
                        isDropDownMenuShowing.value = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert, contentDescription = null
                        )
                        Box(contentAlignment = Alignment.TopEnd) {
                            DropdownMenu(
                                expanded = isDropDownMenuShowing.value, onDismissRequest = {
                                    isDropDownMenuShowing.value = false
                                }) {
                                DropdownMenuItem(text = {
                                    Text(text = stringResource(R.string.create_travel_edit))
                                }, onClick = {
                                    isEditing.value = true
                                    isDropDownMenuShowing.value = false
                                })
                                DropdownMenuItem(text = {
                                    Text(text = stringResource(R.string.create_travel_add_date))
                                }, onClick = {
                                    isDropDownMenuShowing.value = false
                                })

                                DropdownMenuItem(text = {
                                    Text(text = stringResource(R.string.create_travel_delete_travel))
                                }, onClick = {
                                    deleteTravel.value = true
                                    isDropDownMenuShowing.value = false
                                })
                            }
                        }
                    }
                }
            })
    }

    Scaffold(
        modifier = modifier, topBar = { TravelAppBar() },
        floatingActionButton = {
            if (isOnMainScreen) {
                FloatingActionButton(onClick = {
                    navController.navigate(TravelsRoutes.CreateTravelScreen())
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            }
        }) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = TravelsRoutes.MainScreen,
        ) {

            composable<TravelsRoutes.IntroScreen>{ navBackStackEntry ->
                IntroScreen {
                    navController.popBackStack()
                    navController.navigate(TravelsRoutes.GenerateTravelScreen())
                }
            }

            composable<TravelsRoutes.CreateTravelScreen> { navBackStackEntry ->
                val travelId =
                    navBackStackEntry.toRoute<TravelsRoutes.CreateTravelScreen>().travelId

                CreateTravelScreen(
                    travelId = travelId,
                    isEditing = isEditing.value,
                    deleteTravel = deleteTravel.value,
                    createTravel = createTravel.value,
                    onFinishEditing = {
                        isEditing.value = false
                    },
                    onTravelCreated = {
                        createTravel.value = false
                        navController.popBackStack()
                    }, onTravelDeleted = {
                        deleteTravel.value = false
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
            composable<TravelsRoutes.MainScreen> { backStackEntry ->
                MainScreen { travelId ->
                    navController.navigate(TravelsRoutes.CreateTravelScreen(travelId = travelId))
                }
            }
        }
    }

}