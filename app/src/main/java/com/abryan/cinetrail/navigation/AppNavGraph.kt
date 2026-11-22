package com.abryan.cinetrail.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abryan.cinetrail.ui.screen.DashboardScreen
import com.abryan.cinetrail.ui.screen.DetailScreen
import com.abryan.cinetrail.ui.screen.SearchScreen
import com.abryan.cinetrail.viewmodel.DashboardViewModel
import com.abryan.cinetrail.viewmodel.DetailViewModel
import com.abryan.cinetrail.viewmodel.SearchViewModel

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Search : Screen("search")
    object Detail : Screen("detail/{movieId}") {
        fun createRoute(movieId: Int) = "detail/$movieId"
    }
}

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onLanguageChange: (String) -> Unit = {}
) {
    val dashboardViewModel: DashboardViewModel = viewModel()
    val searchViewModel: SearchViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onMovieClick = { movieId ->
                    navController.navigate(Screen.Detail.createRoute(movieId))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                onLanguageChange = onLanguageChange
            )
        }

        composable(Screen.Search.route) {
            val currentLanguage = dashboardViewModel.uiState.value.currentLanguage
            SearchScreen(
                viewModel = searchViewModel,
                currentLanguage = currentLanguage,
                onMovieClick = { movieId ->
                    navController.navigate(Screen.Detail.createRoute(movieId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            val currentLanguage = dashboardViewModel.uiState.value.currentLanguage
            val detailViewModel: DetailViewModel = viewModel()

            DetailScreen(
                movieId = movieId,
                currentLanguage = currentLanguage,
                viewModel = detailViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
