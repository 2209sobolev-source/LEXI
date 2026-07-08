package com.lexi.vocab.ui.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lexi.vocab.data.repository.VocabRepository
import com.lexi.vocab.ui.components.BottomNavBar
import com.lexi.vocab.ui.dictionary.DictionaryScreen
import com.lexi.vocab.ui.home.HomeScreen
import com.lexi.vocab.ui.review.ReviewScreen
import com.lexi.vocab.ui.stats.StatsScreen

@Composable
fun VocabNavGraph(repository: VocabRepository) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.Home.route) {
                HomeScreen(
                    repository = repository,
                    onStartReview = { navController.navigate(Destination.Review.route) }
                )
            }
            composable(Destination.Review.route) {
                ReviewScreen(repository = repository)
            }
            composable(Destination.Dictionary.route) {
                DictionaryScreen(repository = repository)
            }
            composable(Destination.Stats.route) {
                StatsScreen(repository = repository)
            }
        }
    }
}
