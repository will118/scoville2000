package com.will118.scoville2000

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.unit.dp
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
    private val Context.dataStore: DataStore<GameStateData> by dataStore(
        "game-state-data",
        serializer = GameStateDataSerializer
    )

    @ExperimentalGraphicsApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            Gt2000Theme {
                Surface(color = MaterialTheme.colors.background) {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.Game) {
                        composable(Routes.Game) {
                            val initialStateData = runBlocking {
                                applicationContext.dataStore.data.first()
                            }

                            val gameState = GameState(initialStateData.copy())
                            val gameStateExecutor = GameStateExecutor(
                                gameState = gameState,
                                onSaveTick = { updated ->
                                    applicationContext.dataStore.updateData {
                                        updated
                                    }
                                },
                                onGameOver = {
                                    GlobalScope.launch {
                                        runOnUiThread {
                                            navController.navigate(Routes.GameOver)
                                        }
                                    }
                                },
                            )

                            Game(
                                gameState = gameState,
                                gameStateExecutor = gameStateExecutor
                            )
                        }
                        composable(Routes.GameOver) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(text = "Game Over")
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Button(onClick = {
                                        runBlocking {
                                            applicationContext.dataStore.updateData {
                                                GameStateData()
                                            }
                                        }
                                        navController.navigate(Routes.Game)
                                    }) {
                                        Text(text = "Restart")
                                    }
                                }
                            }
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