package com.abryan.cinetrail

import com.abryan.cinetrail.data.api.TmdbService
import com.abryan.cinetrail.data.model.GenreDto
import com.abryan.cinetrail.data.model.GenreResponse
import com.abryan.cinetrail.data.model.MovieDetailDto
import com.abryan.cinetrail.data.model.MovieResponse
import com.abryan.cinetrail.data.model.WatchProviderDto
import com.abryan.cinetrail.data.model.WatchProviderRegion
import com.abryan.cinetrail.data.model.WatchProviderResponse
import com.abryan.cinetrail.data.repository.MovieRepository
import com.abryan.cinetrail.viewmodel.DetailViewModel
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
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeDetailTmdbService(
        var shouldThrowError: Boolean = false
    ) : TmdbService {
        override suspend fun getTrendingMovies(language: String, page: Int): MovieResponse {
            return MovieResponse(1, emptyList(), 1, 0)
        }

        override suspend fun getPopularMovies(language: String, page: Int): MovieResponse {
            return MovieResponse(1, emptyList(), 1, 0)
        }

        override suspend fun getTopRatedMovies(language: String, page: Int): MovieResponse {
            return MovieResponse(1, emptyList(), 1, 0)
        }

        override suspend fun discoverMovies(
            language: String,
            page: Int,
            country: String?,
            genreId: Int?,
            sortBy: String
        ): MovieResponse {
            return MovieResponse(1, emptyList(), 1, 0)
        }

        override suspend fun getGenres(language: String): GenreResponse {
            return GenreResponse(emptyList())
        }

        override suspend fun searchMovies(query: String, language: String, page: Int): MovieResponse {
            return MovieResponse(1, emptyList(), 1, 0)
        }

        override suspend fun getMovieDetails(movieId: Int, language: String): MovieDetailDto {
            if (shouldThrowError) {
                throw IOException("Network error")
            }
            return MovieDetailDto(
                id = movieId,
                title = "Inception",
                overview = "A thief who steals corporate secrets through the use of dream-sharing technology.",
                posterPath = "/poster.jpg",
                backdropPath = "/backdrop.jpg",
                voteAverage = 8.8,
                releaseDate = "2010-07-16",
                runtime = 148,
                genres = listOf(GenreDto(28, "Action"), GenreDto(878, "Sci-Fi")),
                originCountry = listOf("US")
            )
        }

        override suspend fun getWatchProviders(movieId: Int): WatchProviderResponse {
            val netflix = WatchProviderDto(8, "Netflix", "/netflix.jpg", 1)
            val idProviders = WatchProviderRegion(
                link = "https://tmdb.org/watch/id",
                flatrate = listOf(netflix),
                rent = emptyList(),
                buy = emptyList()
            )
            val usProviders = WatchProviderRegion(
                link = "https://tmdb.org/watch/us",
                flatrate = listOf(netflix),
                rent = listOf(WatchProviderDto(2, "Apple TV", "/apple.jpg", 2)),
                buy = emptyList()
            )
            return WatchProviderResponse(
                id = movieId,
                results = mapOf(
                    "ID" to idProviders,
                    "US" to usProviders
                )
            )
        }

        override suspend fun getMovieCredits(movieId: Int, language: String): com.abryan.cinetrail.data.model.MovieCreditsResponse {
            val actor = com.abryan.cinetrail.data.model.CastDto(101, "Leonardo DiCaprio", "Dom Cobb", "/leo.jpg")
            return com.abryan.cinetrail.data.model.MovieCreditsResponse(movieId, listOf(actor))
        }
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
    fun initialState_hasDefaultValues() {
        val fakeApi = FakeDetailTmdbService()
        val repository = MovieRepository(fakeApi)
        val viewModel = DetailViewModel(repository)

        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
        assertFalse(state.isError)
        assertNull(state.movieDetail)
        assertNull(state.watchProviderRegion)
        assertEquals("ID", state.selectedRegion)
    }

    @Test
    fun loadMovieDetails_success_updatesUiStateWithDetailAndProviders() = runTest {
        val fakeApi = FakeDetailTmdbService()
        val repository = MovieRepository(fakeApi)
        val viewModel = DetailViewModel(repository)

        viewModel.loadMovieDetails(550, "id-ID")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isError)
        assertNotNull(state.movieDetail)
        assertEquals(550, state.movieDetail?.id)
        assertEquals("Inception", state.movieDetail?.title)
        assertNotNull(state.watchProviderRegion)
        assertEquals(1, state.watchProviderRegion?.flatrate?.size)
        assertEquals("Netflix", state.watchProviderRegion?.flatrate?.first()?.providerName)
    }

    @Test
    fun loadMovieDetails_failure_setsIsErrorTrue() = runTest {
        val fakeApi = FakeDetailTmdbService(shouldThrowError = true)
        val repository = MovieRepository(fakeApi)
        val viewModel = DetailViewModel(repository)

        viewModel.loadMovieDetails(999, "id-ID")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isError)
        assertNull(state.movieDetail)
    }

    @Test
    fun changeRegion_updatesSelectedRegionAndProviders() = runTest {
        val fakeApi = FakeDetailTmdbService()
        val repository = MovieRepository(fakeApi)
        val viewModel = DetailViewModel(repository)

        viewModel.loadMovieDetails(550, "id-ID")
        advanceUntilIdle()

        assertEquals("ID", viewModel.uiState.value.selectedRegion)
        assertEquals(0, viewModel.uiState.value.watchProviderRegion?.rent?.size)

        // Ubah region ke US
        viewModel.changeRegion("US", 550)
        advanceUntilIdle()

        val updatedState = viewModel.uiState.value
        assertEquals("US", updatedState.selectedRegion)
        assertNotNull(updatedState.watchProviderRegion)
        assertEquals(1, updatedState.watchProviderRegion?.rent?.size)
        assertEquals("Apple TV", updatedState.watchProviderRegion?.rent?.first()?.providerName)
    }

    @Test
    fun loadMovieDetails_populatesCastList() = runTest {
        val fakeApi = FakeDetailTmdbService()
        val repository = MovieRepository(fakeApi)
        val viewModel = DetailViewModel(repository)

        viewModel.loadMovieDetails(550, "id-ID")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.cast.size)
        assertEquals("Leonardo DiCaprio", state.cast.first().name)
        assertEquals("Dom Cobb", state.cast.first().character)
    }

    @Test
    fun toggleFavorite_switchesFavoriteStateCorrectly() {
        val fakeApi = FakeDetailTmdbService()
        val repository = MovieRepository(fakeApi)
        val viewModel = DetailViewModel(repository)

        assertFalse(viewModel.uiState.value.isFavorite)

        viewModel.toggleFavorite()
        assertTrue(viewModel.uiState.value.isFavorite)

        viewModel.toggleFavorite()
        assertFalse(viewModel.uiState.value.isFavorite)
    }
}
