package com.abryan.cinetrail.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abryan.cinetrail.R
import com.abryan.cinetrail.ui.component.CountryFilterSection
import com.abryan.cinetrail.ui.component.EmptyState
import com.abryan.cinetrail.ui.component.ErrorState
import com.abryan.cinetrail.ui.component.GenreFilterSection
import com.abryan.cinetrail.ui.component.HeroCarousel
import com.abryan.cinetrail.ui.component.LoadingState
import com.abryan.cinetrail.ui.component.MovieCard
import com.abryan.cinetrail.viewmodel.COUNTRY_OPTIONS
import com.abryan.cinetrail.viewmodel.DashboardViewModel
import com.abryan.cinetrail.viewmodel.MovieCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onMovieClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguageDialog(
            currentLanguage = uiState.currentLanguage,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { lang ->
                showLanguageDialog = false
                viewModel.updateLanguage(lang)
                onLanguageChange(lang)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.app_name),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search_placeholder)
                        )
                    }
                    IconButton(onClick = { showLanguageDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = stringResource(id = R.string.language)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search Trigger Bar (disesuaikan dengan gaya referensi)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { onSearchClick() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(id = R.string.search_placeholder),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // Hero Carousel (Trending)
                if (uiState.trendingMovies.isNotEmpty()) {
                    item {
                        Column {
                            Text(
                                text = stringResource(id = R.string.trending_now),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            HeroCarousel(
                                trendingMovies = uiState.trendingMovies,
                                onMovieClick = onMovieClick
                            )
                        }
                    }
                }

                // Country Filter Chips
                item {
                    Column {
                        Text(
                            text = stringResource(id = R.string.all_countries),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        CountryFilterSection(
                            countries = COUNTRY_OPTIONS,
                            selectedCountry = uiState.selectedCountry,
                            onCountrySelected = { viewModel.selectCountry(it) }
                        )
                    }
                }

                // Genre Filter Chips
                if (uiState.genres.isNotEmpty()) {
                    item {
                        Column {
                            Text(
                                text = stringResource(id = R.string.all_genres),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            GenreFilterSection(
                                genres = uiState.genres,
                                selectedGenreId = uiState.selectedGenreId,
                                onGenreSelected = { viewModel.selectGenre(it) }
                            )
                        }
                    }
                }

                // Pengalih Kategori Film (Populer vs Rating Tertinggi)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = uiState.selectedCategory == MovieCategory.POPULAR,
                            onClick = { viewModel.selectCategory(MovieCategory.POPULAR) },
                            label = {
                                Text(
                                    text = stringResource(id = R.string.popular_movies),
                                    fontWeight = if (uiState.selectedCategory == MovieCategory.POPULAR) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )

                        FilterChip(
                            selected = uiState.selectedCategory == MovieCategory.TOP_RATED,
                            onClick = { viewModel.selectCategory(MovieCategory.TOP_RATED) },
                            label = {
                                Text(
                                    text = stringResource(id = R.string.top_rated),
                                    fontWeight = if (uiState.selectedCategory == MovieCategory.TOP_RATED) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                // Status Konten (Loading Skeleton / Error / Empty / List Film)
                if (uiState.isLoading && uiState.displayedMovies.isEmpty()) {
                    item { LoadingState() }
                } else if (uiState.isError && uiState.displayedMovies.isEmpty()) {
                    item { ErrorState(onRetry = { viewModel.loadData() }) }
                } else if (uiState.displayedMovies.isEmpty()) {
                    item { EmptyState() }
                } else {
                    items(uiState.displayedMovies) { movie ->
                        MovieCard(
                            movie = movie,
                            genresMap = uiState.genresMap,
                            onClick = { onMovieClick(movie.id) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun LanguageDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf(
        "en-US" to stringResource(id = R.string.english),
        "id-ID" to stringResource(id = R.string.indonesian)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.select_language)) },
        text = {
            Column {
                languages.forEach { (code, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(code) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentLanguage == code,
                            onClick = { onLanguageSelected(code) }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        }
    )
}
