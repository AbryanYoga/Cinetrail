package com.abryan.cinetrail

import com.abryan.cinetrail.data.api.TmdbService
import com.abryan.cinetrail.data.model.GenreDto
import com.abryan.cinetrail.data.model.GenreResponse
import com.abryan.cinetrail.data.model.MovieDetailDto
import com.abryan.cinetrail.data.model.MovieDto
import com.abryan.cinetrail.data.model.MovieResponse
import com.abryan.cinetrail.data.model.WatchProviderResponse
import com.abryan.cinetrail.data.repository.MovieRepository
import com.abryan.cinetrail.viewmodel.DashboardViewModel
import com.abryan.cinetrail.viewmodel.MovieCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeTmdbService : TmdbService {
        override suspend fun getTrendingMovies(language: String, page: Int): MovieResponse {
            return MovieResponse(1, listOf(createMovie(1, "Trending $language")), 1, 1)
        }

        override suspend fun getPopularMovies(language: String, page: Int): MovieResponse {
            return MovieResponse(1, listOf(createMovie(2, "Popular $language")), 1, 1)
        }

        override suspend fun getTopRatedMovies(language: String, page: Int): MovieResponse {
            return MovieResponse(1, listOf(createMovie(3, "Top Rated $language")), 1, 1)
        }

        override suspend fun discoverMovies(
            language: String,
            page: Int,
            country: String?,
            genreId: Int?,
            sortBy: String
        ): MovieResponse {
            return MovieResponse(1, listOf(createMovie(4, "Filtered $country-$genreId-$sortBy")), 1, 1)
        }

        override suspend fun getGenres(language: String): GenreResponse {
            return GenreResponse(listOf(GenreDto(28, "Action"), GenreDto(35, "Comedy")))
        }

        override suspend fun searchMovies(query: String, language: String, page: Int): MovieResponse {
            return MovieResponse(1, listOf(createMovie(5, "Search: $query")), 1, 1)
        }

        override suspend fun getMovieDetails(movieId: Int, language: String): MovieDetailDto {
            return MovieDetailDto(movieId, "Detail", "Overview", null, null, 8.0, "2026", 120, emptyList(), emptyList())
        }

        override suspend fun getWatchProviders(movieId: Int): WatchProviderResponse {
            return WatchProviderResponse(movieId, emptyMap())
        }

        private fun createMovie(id: Int, title: String) = MovieDto(
            id = id,
            title = title,
            overview = "Overview",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            voteAverage = 8.5,
            releaseDate = "2026-11-20",
            genreIds = listOf(28),
            originCountry = listOf("ID")
        )
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadData_initialState_populatesTrendingAndDisplayedMovies() = runTest {
        val repository = MovieRepository(FakeTmdbService())
        val viewModel = DashboardViewModel(repository)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isError)
        assertEquals(1, state.trendingMovies.size)
        assertEquals("Trending en-US", state.trendingMovies.first().title)
        assertEquals(1, state.displayedMovies.size)
        assertEquals("Popular en-US", state.displayedMovies.first().title)
        assertEquals(2, state.genres.size)
        assertEquals("Action", state.genresMap[28])
    }

    @Test
    fun selectCategory_topRated_loadsTopRatedMovies() = runTest {
        val repository = MovieRepository(FakeTmdbService())
        val viewModel = DashboardViewModel(repository)
        advanceUntilIdle()

        viewModel.selectCategory(MovieCategory.TOP_RATED)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(MovieCategory.TOP_RATED, state.selectedCategory)
        assertEquals("Top Rated en-US", state.displayedMovies.first().title)
    }

    @Test
    fun selectCountry_updatesSelectedCountryAndFilters() = runTest {
        val repository = MovieRepository(FakeTmdbService())
        val viewModel = DashboardViewModel(repository)
        advanceUntilIdle()

        viewModel.selectCountry("KR")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("KR", state.selectedCountry)
        assertTrue(state.displayedMovies.first().title.contains("Filtered KR-null-popularity.desc"))
    }

    @Test
    fun selectGenre_updatesSelectedGenreAndFilters() = runTest {
        val repository = MovieRepository(FakeTmdbService())
        val viewModel = DashboardViewModel(repository)
        advanceUntilIdle()

        viewModel.selectGenre(28)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(28, state.selectedGenreId)
        assertTrue(state.displayedMovies.first().title.contains("Filtered null-28-popularity.desc"))
    }

    @Test
    fun updateLanguage_reloadsAllDataWithNewLanguage() = runTest {
        val repository = MovieRepository(FakeTmdbService())
        val viewModel = DashboardViewModel(repository)
        advanceUntilIdle()

        viewModel.updateLanguage("id-ID")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("id-ID", state.currentLanguage)
        assertEquals("Trending id-ID", state.trendingMovies.first().title)
        assertEquals("Popular id-ID", state.displayedMovies.first().title)
    }
}
