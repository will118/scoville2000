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
import com.will118.scoville2000.engine.GameStateData
import com.will118.scoville2000.engine.GameStateExecutor
import com.will118.scoville2000.ui.theme.Gt2000Theme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

class MainActivity : ComponentActivity() {
    val Context.dataStore: DataStore<GameStateData> by dataStore(
        "game-state-data",
        serializer = GameStateDataSerializer
    )

    @ExperimentalGraphicsApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialStateData = runBlocking {
            applicationContext.dataStore.data.first()
        }

        setContent {
            val navController = rememberNavController()

            val gameState = GameState(initialStateData)
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

@ExperimentalSerializationApi
object GameStateDataSerializer : Serializer<GameStateData> {
    override val defaultValue = GameStateData()

    override suspend fun readFrom(input: InputStream): GameStateData {
        try {
            return input.use {
                ProtoBuf.decodeFromByteArray(it.readBytes())
            }
        } catch (e: Exception) {
            throw CorruptionException("Unable to read GameState", e)
        }
    }

    override suspend fun writeTo(t: GameStateData, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(t))
    }
}