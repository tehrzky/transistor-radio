package com.transistor.radio.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.transistor.radio.domain.model.PlaybackState
import com.transistor.radio.ui.navigation.Screen
import com.transistor.radio.ui.screens.categories.CategoriesScreen
import com.transistor.radio.ui.screens.home.HomeScreen
import com.transistor.radio.ui.screens.search.SearchScreen
import com.transistor.radio.ui.screens.settings.SettingsScreen
import com.transistor.radio.ui.viewmodel.PlayerViewModel

@Composable
fun RootScreen(
    navController: NavHostController = rememberNavController(),
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route ?: Screen.Home.route

    val currentStation by playerViewModel.currentStation.collectAsState()
    val playbackState by playerViewModel.playbackState.collectAsState()
    var showFullPlayer by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Layer 0: NavHost
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Home.route) {
                HomeScreen(onStationClick = { station ->
                    playerViewModel.playStation(station)
                })
            }
            composable(Screen.Search.route) {
                SearchScreen(onStationClick = { station ->
                    playerViewModel.playStation(station)
                })
            }
            composable(Screen.Categories.route) {
                CategoriesScreen(onStationClick = { station ->
                    playerViewModel.playStation(station)
                })
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }

        // Layer 1: Floating Mini-Player
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 84.dp)
        ) {
            MiniPlayer(
                station = currentStation,
                playbackState = playbackState,
                onPlayPause = { playerViewModel.togglePlayPause() },
                onTap = { showFullPlayer = true }
            )
        }

        // Layer 2: Floating Nav Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        ) {
            FloatingNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }

        // Full Player Sheet
        FullPlayerSheet(
            station = currentStation,
            playbackState = playbackState,
            visible = showFullPlayer,
            onDismiss = { showFullPlayer = false },
            onPlayPause = { playerViewModel.togglePlayPause() },
            onStop = {
                playerViewModel.stop()
                showFullPlayer = false
            }
        )
    }
}
