package com.will118.scoville2000

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.will118.scoville2000.engine.GameState
import com.will118.scoville2000.engine.GameStateExecutor
import com.will118.scoville2000.ui.theme.Gt2000Theme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream

class MainActivity : ComponentActivity() {
    val Context.dataStore: DataStore<GameState> by dataStore("game-state", serializer = GameStateSerializer)

    @ExperimentalGraphicsApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applicationContext.dataStore.updateData {  }

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

object GameStateSerializer : Serializer<GameState> {
    override val defaultValue = GameState()

    override suspend fun readFrom(input: InputStream): GameState {
        try {
            return GameState()
        } catch (e: Exception) {
            throw CorruptionException("Unable to read GameState", e)
        }
    }

    override suspend fun writeTo(t: GameState, output: OutputStream) {
        output.write(byteArrayOf())
    }
}