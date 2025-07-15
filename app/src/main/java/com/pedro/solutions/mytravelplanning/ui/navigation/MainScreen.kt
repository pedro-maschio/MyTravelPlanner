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
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelScreen

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RestrictedApi")
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntries by navController.currentBackStack.collectAsState()
    val isOnMainScreen =  false
    Scaffold(modifier = modifier, topBar = {
            CenterAlignedTopAppBar(title = {
                if(isOnMainScreen) {
                    Text(text = stringResource(R.string.travels_listing_title))
                }
            }, navigationIcon = {
                if(!isOnMainScreen) {
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
                navController.navigate(TravelsRoutes.CreateTravelScreen())
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = TravelsRoutes.CreateTravelScreen(),
        ) {

            composable<TravelsRoutes.CreateTravelScreen> { backStackEntry ->
                val travelId: TravelsRoutes.CreateTravelScreen = backStackEntry.toRoute()
                CreateTravelScreen(travelId=travelId.id) {
                    navController.popBackStack()
                }
            }
        }
    }

}