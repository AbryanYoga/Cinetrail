package com.abryan.cinetrail

import com.abryan.cinetrail.data.api.TmdbService
import com.abryan.cinetrail.data.model.GenreDto
import com.abryan.cinetrail.data.model.GenreResponse
import com.abryan.cinetrail.data.model.MovieDetailDto
import com.abryan.cinetrail.data.model.MovieDto
import com.abryan.cinetrail.data.model.MovieResponse
import com.abryan.cinetrail.data.model.WatchProviderDto
import com.abryan.cinetrail.data.model.WatchProviderRegion
import com.abryan.cinetrail.data.model.WatchProviderResponse
import com.abryan.cinetrail.data.repository.MovieRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieRepositoryTest {

    private class FakeTmdbService : TmdbService {
        override suspend fun getTrendingMovies(language: String, page: Int): MovieResponse {
            return MovieResponse(1, listOf(createMovie(1, "Trending 1")), 1, 1)
        }

        override suspend fun getPopularMovies(language: String, page: Int): MovieResponse {
            return MovieResponse(1, listOf(createMovie(2, "Popular 1")), 1, 1)
        }

        override suspend fun getTopRatedMovies(language: String, page: Int): MovieResponse {
            return MovieResponse(1, listOf(createMovie(3, "Top Rated 1")), 1, 1)
        }

        override suspend fun discoverMovies(
            language: String,
            page: Int,
            country: String?,
            genreId: Int?,
            sortBy: String
        ): MovieResponse {
            return MovieResponse(1, listOf(createMovie(4, "Discovered ($country, $genreId)")), 1, 1)
        }

        override suspend fun getGenres(language: String): GenreResponse {
            return GenreResponse(listOf(GenreDto(28, "Action"), GenreDto(35, "Comedy")))
        }

        override suspend fun searchMovies(query: String, language: String, page: Int): MovieResponse {
            return MovieResponse(1, listOf(createMovie(5, "Search: $query")), 1, 1)
        }

        override suspend fun getMovieDetails(movieId: Int, language: String): MovieDetailDto {
            return if (language == "id-ID") {
                // Sinopsis kosong sengaja untuk menguji fallback ke en-US
                MovieDetailDto(
                    id = movieId,
                    title = "Film Uji",
                    overview = "",
                    posterPath = null,
                    backdropPath = null,
                    voteAverage = 7.5,
                    releaseDate = "2026-01-01",
                    runtime = 120,
                    genres = listOf(GenreDto(28, "Action")),
                    originCountry = listOf("ID")
                )
            } else {
                // Versi en-US mengembalikan sinopsis bahasa Inggris
                MovieDetailDto(
                    id = movieId,
                    title = "Test Movie",
                    overview = "English overview fallback.",
                    posterPath = null,
                    backdropPath = null,
                    voteAverage = 7.5,
                    releaseDate = "2026-01-01",
                    runtime = 120,
                    genres = listOf(GenreDto(28, "Action")),
                    originCountry = listOf("ID")
                )
            }
        }

        override suspend fun getWatchProviders(movieId: Int): WatchProviderResponse {
            val idRegion = WatchProviderRegion(
                link = "https://tmdb.org",
                flatrate = listOf(WatchProviderDto(8, "Netflix", null, 1)),
                rent = null,
                buy = null
            )
            return WatchProviderResponse(movieId, mapOf("ID" to idRegion))
        }

        override suspend fun getMovieCredits(movieId: Int, language: String): com.abryan.cinetrail.data.model.MovieCreditsResponse {
            val actor = com.abryan.cinetrail.data.model.CastDto(101, "Reza Rahadian", "Habibie", "/reza.jpg")
            return com.abryan.cinetrail.data.model.MovieCreditsResponse(movieId, listOf(actor))
        }

        private fun createMovie(id: Int, title: String) = MovieDto(
            id = id,
            title = title,
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            voteAverage = 8.0,
            releaseDate = "2026-11-20",
            genreIds = listOf(28),
            originCountry = listOf("ID")
        )
    }

    @Test
    fun getTrendingMovies_returnsList() = runBlocking {
        val repo = MovieRepository(FakeTmdbService())
        val movies = repo.getTrendingMovies("id-ID")
        assertEquals(1, movies.size)
        assertEquals("Trending 1", movies.first().title)
    }

    @Test
    fun getPopularMovies_withFilter_usesDiscover() = runBlocking {
        val repo = MovieRepository(FakeTmdbService())
        val movies = repo.getPopularMovies("id-ID", country = "KR", genreId = 28)
        assertEquals(1, movies.size)
        assertEquals("Discovered (KR, 28)", movies.first().title)
    }

    @Test
    fun searchMovies_emptyQuery_returnsEmptyWithoutCallingApi() = runBlocking {
        val repo = MovieRepository(FakeTmdbService())
        val results = repo.searchMovies("   ", "id-ID")
        assertTrue(results.isEmpty())
    }

    @Test
    fun getMovieDetails_overviewFallbackToEnglish_whenLocalOverviewEmpty() = runBlocking {
        val repo = MovieRepository(FakeTmdbService())
        val result = repo.getMovieDetails(42, "id-ID")
        assertTrue(result.isSuccess)
        val detail = result.getOrNull()
        assertNotNull(detail)
        assertEquals("English overview fallback.", detail?.overview)
    }

    @Test
    fun getWatchProviders_returnsRequestedRegion() = runBlocking {
        val repo = MovieRepository(FakeTmdbService())
        val providers = repo.getWatchProviders(42, "ID")
        assertNotNull(providers)
        assertEquals("Netflix", providers?.flatrate?.firstOrNull()?.providerName)
    }
}
