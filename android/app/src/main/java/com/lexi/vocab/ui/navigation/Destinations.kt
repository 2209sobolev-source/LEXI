package com.lexi.vocab.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Destination("home", "Главная", Icons.Filled.Home)
    data object Review : Destination("review", "Повторение", Icons.Filled.School)
    data object Dictionary : Destination("dictionary", "Словарь", Icons.Filled.MenuBook)
    data object Stats : Destination("stats", "Статистика", Icons.Filled.BarChart)
}

val bottomNavDestinations = listOf(
    Destination.Home,
    Destination.Review,
    Destination.Dictionary,
    Destination.Stats
)
