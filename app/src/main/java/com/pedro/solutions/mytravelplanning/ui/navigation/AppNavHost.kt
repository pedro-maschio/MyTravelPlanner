package com.pedro.solutions.mytravelplanning.ui.navigation

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelScreen
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelUiEvent
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelViewModel
import com.pedro.solutions.mytravelplanning.ui.screens.detail.TravelDetailScreen
import com.pedro.solutions.mytravelplanning.ui.screens.main.MainScreen
import com.pedro.solutions.mytravelplanning.ui.screens.main.MainScreenUiEvent
import com.pedro.solutions.mytravelplanning.ui.screens.main.MainScreenViewModel
import org.koin.compose.koinInject

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val activity = LocalActivity.current

    val mainScreenViewModel: MainScreenViewModel = koinInject()
    val mainScreenUiState = mainScreenViewModel.uiState.collectAsStateWithLifecycle()

    val createTravelViewModel: CreateTravelViewModel = koinInject()
    val createUiState = createTravelViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = TravelRoutes.MainScreen
    ) {
        composable<TravelRoutes.MainScreen> { backStackEntry ->
            MainScreen(
                uiState =
                    mainScreenUiState.value, onEvent = { event ->
                    when (event) {
                        is MainScreenUiEvent.OnSelectionModeChanged -> mainScreenViewModel.setOnSelectionMode(
                            event.isOnSelectionMode
                        )

                        is MainScreenUiEvent.GoBack -> {
                            if (!navController.popBackStack()) {
                                activity?.finish()
                            }
                        }

                        is MainScreenUiEvent.HideDeleteDialog -> mainScreenViewModel.hideDeleteDialog()
                        is MainScreenUiEvent.OpenCreateTravelScreen ->
                            navController.navigate(TravelRoutes.CreateTravelScreen(event.travelId))

                        is MainScreenUiEvent.OnSelectTravel -> mainScreenViewModel.selectTravel(
                            event.travelId
                        )

                        is MainScreenUiEvent.SetSearchScreenExpanded -> mainScreenViewModel.setSearchScreenExpanded(
                            event.expanded
                        )

                        is MainScreenUiEvent.ClearSearchQuery -> mainScreenViewModel.clearSearchQuery()
                        is MainScreenUiEvent.UpdateQuery -> mainScreenViewModel.updateQuery(event.query)
                        is MainScreenUiEvent.OnDeleteTravelsClick -> mainScreenViewModel.onDeleteTravelsClick()
                        is MainScreenUiEvent.SetDropDownMenuShowing -> mainScreenViewModel.setDropdownMenuShowing(
                            event.isShowing
                        )

                        MainScreenUiEvent.ShowDeleteDialog -> mainScreenViewModel.showDeleteDialog()
                        MainScreenUiEvent.OnClickFloatingButton -> {
                            navController.navigate(TravelRoutes.CreateTravelScreen())
                        }
                    }
                })
        }

        composable<TravelRoutes.CreateTravelScreen> { navBackStackEntry ->
            val travelId = navBackStackEntry.toRoute<TravelRoutes.CreateTravelScreen>().travelId
            LaunchedEffect(travelId) {
                if (travelId != null) {
                    createTravelViewModel.loadTravel(travelId)
                } else {
                    createTravelViewModel.resetUiState()
                }
            }

            CreateTravelScreen(
                uiState = createUiState.value,
                onEvent = { event ->
                    when (event) {
                        is CreateTravelUiEvent.OnTravelCreated -> {
                            createTravelViewModel.createTravel()
                            mainScreenViewModel.loadTravels()
                            navController.popBackStack()
                        }

                        CreateTravelUiEvent.OnTravelDeleted -> {
                            createTravelViewModel.deleteTravel()
                            mainScreenViewModel.loadTravels()
                            navController.popBackStack()
                        }

                        is CreateTravelUiEvent.ShareTravel -> createTravelViewModel.shareTravel()
                        is CreateTravelUiEvent.OnDayAdded -> createTravelViewModel.onDayAdded(event.dayTitle)
                        is CreateTravelUiEvent.SetDropdownMenuShowing -> createTravelViewModel.setDropdownMenuShowing(
                            event.isShowing
                        )

                        is CreateTravelUiEvent.SetIsEditing -> createTravelViewModel.setIsEditing(
                            event.isEditing
                        )

                        is CreateTravelUiEvent.SetDatePickerShowing -> createTravelViewModel.setDatePickerModal(
                            event.isShowing
                        )

                        is CreateTravelUiEvent.SetDeleteDialogShowing -> createTravelViewModel.setDeleteDialogShowing(
                            event.isShowing
                        )

                        is CreateTravelUiEvent.SetDetailsAlertDialogShowing -> createTravelViewModel.setDetailsAlertDialogShowing(
                            event.isShowing
                        )

                        is CreateTravelUiEvent.DeleteDay -> createTravelViewModel.deleteDay(event.index)
                        is CreateTravelUiEvent.AddActivity -> createTravelViewModel.addActivity(
                            event.index
                        )

                        is CreateTravelUiEvent.UpdateTravelDayText -> createTravelViewModel.updateTravelDayText(
                            event.index,
                            event.newText
                        )

                        is CreateTravelUiEvent.UpdateTravelName -> createTravelViewModel.updateTravelName(
                            event.newText
                        )

                        is CreateTravelUiEvent.UpdateTravelActivityText -> createTravelViewModel.updateTravelActivityText(
                            dayIndex = event.dayIndex,
                            activityIndex = event.activityIndex,
                            newText = event.newText
                        )

                        is CreateTravelUiEvent.DateRangeSelected -> createTravelViewModel.onDateRangeSelected(
                            event.range
                        )
                    }
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