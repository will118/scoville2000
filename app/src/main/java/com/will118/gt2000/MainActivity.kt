package com.will118.gt2000

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.will118.gt2000.engine.GameState
import com.will118.gt2000.engine.GameStateExecutor
import com.will118.gt2000.ui.theme.Gt2000Theme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            val gameState = GameState()
            val gameStateExecutor = GameStateExecutor(
                gameState = gameState
            ) {
                GlobalScope.launch {
                    runOnUiThread {
                        navController.navigate(Routes.GameOver)
                    }
                }
            }

            Gt2000Theme {
                Surface(color = MaterialTheme.colors.background) {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.Game) {
                        composable(Routes.Game) {
                            Game(gameState = gameState, gameStateExecutor = gameStateExecutor)
                        }
                        composable(Routes.GameOver) {
                            Text(text = "Game Over")
                        }
                    }
                }
            }
        }
    }
}
