package com.fliq.views.v2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    object QuestMap : Screen("quest_map")
    object Game : Screen("game/{stageId}") {
        fun createRoute(stageId: String) = "game/$stageId"
    }
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}

@Composable
fun MainFlowV2() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = Screen.QuestMap.route) {
        composable(Screen.QuestMap.route) {
            val viewModel: QuestMapViewModel = hiltViewModel()
            val userData by viewModel.userData.collectAsState()
            val stageProgress by viewModel.stageProgress.collectAsState()
            
            QuestMapScreen(
                currentStageId = "w1_s${userData?.currentStage ?: 1}",
                stageProgress = stageProgress,
                xp = userData?.xp ?: 0,
                coins = userData?.coins ?: 0,
                avatarId = userData?.avatarId ?: 1,
                username = userData?.username ?: "Commander",
                onStageClick = { stage ->
                    navController.navigate(Screen.Game.createRoute(stage.id))
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.Game.route) { backStackEntry ->
            val stageId = backStackEntry.arguments?.getString("stageId") ?: "w1_s1"
            val viewModel: GameViewModelV2 = hiltViewModel()
            
            val gameState by viewModel.gameState.collectAsState()
            val tiles by viewModel.tiles.collectAsState()
            val score by viewModel.score.collectAsState()
            val combo by viewModel.combo.collectAsState()
            val lives by viewModel.lives.collectAsState()
            val progress by viewModel.progress.collectAsState()
            val currentStage by viewModel.currentStage.collectAsState()
            val selectedBoost by viewModel.selectedBoost.collectAsState()
            
            LaunchedEffect(stageId) {
                viewModel.loadStage(stageId)
            }
            
            GameScreenV2(
                gameState = gameState,
                tiles = tiles,
                score = score,
                combo = combo,
                lives = lives,
                progress = progress,
                currentStage = currentStage,
                selectedBoost = selectedBoost,
                effects = viewModel.effects,
                onTileTapped = viewModel::onTileTapped,
                onTileEntered = viewModel::onTileEntered,
                onPauseClick = viewModel::pause,
                onBoostSelect = viewModel::selectBoost,
                onStartStage = viewModel::startStage,
                onLoadStage = viewModel::loadStage,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
