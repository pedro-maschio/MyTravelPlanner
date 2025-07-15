package com.pedro.solutions.mytravelplanning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pedro.solutions.mytravelplanning.ui.navigation.MainScreen
import com.pedro.solutions.mytravelplanning.ui.theme.MyTravelPlanningTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyTravelPlanningTheme {
                MainScreen()
            }
        }
    }
}