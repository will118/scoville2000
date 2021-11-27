package com.will118.scoville2000

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.will118.scoville2000.components.ChilliDex
import com.will118.scoville2000.components.gameover.GameOver
import com.will118.scoville2000.engine.*
import com.will118.scoville2000.ui.theme.Gt2000Theme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
@ExperimentalGraphicsApi
@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    private val Context.dataStore: DataStore<GameStateData> by dataStore(
        "game-state-data",
        serializer = GameStateDataSerializer
    )

    private data class RunningGame(val state: GameState, val executor: GameStateExecutor)

    private fun createRunningGame(): RunningGame {
        val gameStateData = runBlocking {
            applicationContext.dataStore.data.first()
        }

        val gameState = GameState(gameStateData.copy())
        val gameStateExecutor = GameStateExecutor(
            gameState = gameState,
            onSaveTick = { updated ->
                applicationContext.dataStore.updateData {
                    updated
                }
            },
        )

        return RunningGame(state = gameState, executor = gameStateExecutor)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            val navController = rememberNavController()

            val (game, updateGame) = remember { mutableStateOf(createRunningGame()) }

            Gt2000Theme {
                Surface(color = MaterialTheme.colors.background) {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.Game) {
                        composable(Routes.Game) {
                            GameContainer(
                                gameState = game.state,
                                gameStateExecutor = game.executor,
                                navigateToChilliDex = {
                                    navController.navigate(Routes.ChilliDex)
                                },
                                onGameOver = {
                                    runOnUiThread { // called from the executor
                                        navController.navigate(Routes.GameOver)
                                    }
                                },
                            )
                        }
                        composable(Routes.ChilliDex) {
                            ChilliDex(
                                objectStateId = game.state.id,
                                gameStateExecutor = game.executor,
                                currentPlantTypes = game.state.plantTypes,
                                plantSeed = { game.executor.enqueueSync(PlantSeed(it)) },
                                autoPlantTechnologyCapable = game.state
                                    .technologies
                                    .contains(Technology.AutoPlanter),
                                autoPlantChecked = { plantType, checked ->
                                    game.executor.enqueueSync(AutoPlantChecked(plantType, checked))
                                },
                                onGameOver = {
                                    runOnUiThread { // called from the executor
                                        navController.navigate(Routes.GameOver)
                                    }
                                },
                            )
                        }
                        composable(Routes.GameOver) {
                            GameOver {
                                val newGameStateData = GameStateData()

                                runBlocking {
                                    applicationContext.dataStore.updateData {
                                        newGameStateData
                                    }
                                }

                                // We have already closed down timers/channels for the old executor.
                                // Hopefully...
                                updateGame(createRunningGame())
                                navController.navigate(Routes.Game)
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