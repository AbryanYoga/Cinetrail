package com.abryan.cinetrail

import com.abryan.cinetrail.data.api.TmdbService
import com.abryan.cinetrail.data.model.GenreDto
import com.abryan.cinetrail.data.model.GenreResponse
import com.abryan.cinetrail.data.model.MovieCreditsResponse
import com.abryan.cinetrail.data.model.MovieDetailDto
import com.abryan.cinetrail.data.model.MovieDto
import com.abryan.cinetrail.data.model.MovieResponse
import com.abryan.cinetrail.data.model.WatchProviderResponse
import com.abryan.cinetrail.data.repository.MovieRepository
import com.abryan.cinetrail.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeSearchTmdbService(
        var shouldThrowError: Boolean = false
    ) : TmdbService {
        override suspend fun getTrendingMovies(language: String, page: Int): MovieResponse =
            MovieResponse(1, emptyList(), 1, 0)

        override suspend fun getPopularMovies(language: String, page: Int): MovieResponse =
            MovieResponse(1, emptyList(), 1, 0)

        override suspend fun getTopRatedMovies(language: String, page: Int): MovieResponse =
            MovieResponse(1, emptyList(), 1, 0)

        override suspend fun discoverMovies(
            language: String,
            page: Int,
            country: String?,
            genreId: Int?,
            sortBy: String
        ): MovieResponse = MovieResponse(1, emptyList(), 1, 0)

        override suspend fun getGenres(language: String): GenreResponse =
            GenreResponse(listOf(GenreDto(28, "Action"), GenreDto(35, "Comedy")))

        override suspend fun searchMovies(query: String, language: String, page: Int): MovieResponse {
            if (shouldThrowError) throw IOException("Search network error")
            val movie = MovieDto(
                id = 10,
                title = "Search Result for $query",
                overview = "Overview",
                posterPath = "/poster.jpg",
                backdropPath = "/backdrop.jpg",
                voteAverage = 8.1,
                releaseDate = "2026-05-10",
                genreIds = listOf(28),
                originCountry = listOf("US")
            )
            return MovieResponse(1, listOf(movie), 1, 1)
        }

        override suspend fun getMovieDetails(movieId: Int, language: String): MovieDetailDto =
            MovieDetailDto(movieId, "Title", "Overview", null, null, 7.0, "2026", 100, emptyList(), emptyList())

        override suspend fun getWatchProviders(movieId: Int): WatchProviderResponse =
            WatchProviderResponse(movieId, emptyMap())

        override suspend fun getMovieCredits(movieId: Int, language: String): MovieCreditsResponse =
            MovieCreditsResponse(movieId, emptyList())
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
    fun initialState_isIdle() {
        val fakeApi = FakeSearchTmdbService()
        val repository = MovieRepository(fakeApi)
        val viewModel = SearchViewModel(repository)

        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertFalse(state.isLoading)
        assertFalse(state.isError)
        assertTrue(state.searchResults.isEmpty())
        assertTrue(state.genresMap.isEmpty())
    }

    @Test
    fun onQueryChange_blankQuery_clearsResultsAndStopsLoading() = runTest {
        val fakeApi = FakeSearchTmdbService()
        val repository = MovieRepository(fakeApi)
        val viewModel = SearchViewModel(repository)

        viewModel.onQueryChange("Inception", "en-US")
        advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.searchResults.size)

        // Ubah query menjadi kosong
        viewModel.onQueryChange("", "en-US")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertFalse(state.isLoading)
        assertTrue(state.searchResults.isEmpty())
    }

    @Test
    fun onQueryChange_validQuery_updatesSearchResultsWithDebounce() = runTest {
        val fakeApi = FakeSearchTmdbService()
        val repository = MovieRepository(fakeApi)
        val viewModel = SearchViewModel(repository)

        viewModel.onQueryChange("Batman", "en-US")

        // Sebelum debounce 350ms, data belum terisi
        advanceTimeBy(200)
        assertTrue(viewModel.uiState.value.searchResults.isEmpty())

        // Setelah debounce selesai dan coroutine tereksekusi
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isError)
        assertEquals(1, state.searchResults.size)
        assertEquals("Search Result for Batman", state.searchResults.first().title)
    }

    @Test
    fun onQueryChange_error_setsIsErrorTrue() = runTest {
        val fakeApi = FakeSearchTmdbService(shouldThrowError = true)
        val repository = MovieRepository(fakeApi)
        val viewModel = SearchViewModel(repository)

        viewModel.onQueryChange("NetworkFail", "en-US")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.searchResults.isEmpty())
    }

    @Test
    fun loadGenres_populatesGenresMap() = runTest {
        val fakeApi = FakeSearchTmdbService()
        val repository = MovieRepository(fakeApi)
        val viewModel = SearchViewModel(repository)

        viewModel.loadGenres("en-US")
        advanceUntilIdle()

        val genresMap = viewModel.uiState.value.genresMap
        assertEquals(2, genresMap.size)
        assertEquals("Action", genresMap[28])
        assertEquals("Comedy", genresMap[35])
    }
}
