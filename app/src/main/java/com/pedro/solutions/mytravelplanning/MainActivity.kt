package com.pedro.solutions.mytravelplanning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pedro.solutions.mytravelplanning.ui.navigation.AppNavHost
import com.pedro.solutions.mytravelplanning.ui.theme.MyTravelPlanningTheme

/*

    TODO:
    1. Tela de intro para escolher se a viagem é de moto ou carro
    2.  Se for viagem de moto, colocar no prompt para dar dicas de segurança
    3. Tela de configurações para mudar se é moto ou carro
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyTravelPlanningTheme {
                AppNavHost()
            }
        }
    }
}