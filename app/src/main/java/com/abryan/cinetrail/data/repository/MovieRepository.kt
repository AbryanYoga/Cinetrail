package com.abryan.cinetrail.data.repository

import com.abryan.cinetrail.data.api.RetrofitClient
import com.abryan.cinetrail.data.api.TmdbService
import com.abryan.cinetrail.data.model.CastDto
import com.abryan.cinetrail.data.model.GenreDto
import com.abryan.cinetrail.data.model.MovieDetailDto
import com.abryan.cinetrail.data.model.MovieDto
import com.abryan.cinetrail.data.model.WatchProviderRegion

class MovieRepository(
    private val api: TmdbService = RetrofitClient.instance
) {
    suspend fun getTrendingMovies(language: String): List<MovieDto> {
        return try {
            api.getTrendingMovies(language = language).results
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPopularMovies(
        language: String,
        country: String? = null,
        genreId: Int? = null,
        page: Int = 1
    ): List<MovieDto> {
        return try {
            if (country != null || genreId != null) {
                api.discoverMovies(
                    language = language,
                    country = country,
                    genreId = genreId,
                    page = page
                ).results
            } else {
                api.getPopularMovies(language = language, page = page).results
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTopRatedMovies(
        language: String,
        country: String? = null,
        genreId: Int? = null,
        page: Int = 1
    ): List<MovieDto> {
        return try {
            if (country != null || genreId != null) {
                api.discoverMovies(
                    language = language,
                    country = country,
                    genreId = genreId,
                    sortBy = "vote_average.desc",
                    page = page
                ).results
            } else {
                api.getTopRatedMovies(language = language, page = page).results
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getGenres(language: String): List<GenreDto> {
        return try {
            api.getGenres(language = language).genres
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchMovies(query: String, language: String): List<MovieDto> {
        if (query.isBlank()) return emptyList()
        return try {
            api.searchMovies(query = query, language = language).results
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMovieDetails(movieId: Int, language: String): Result<MovieDetailDto> {
        return try {
            var details = api.getMovieDetails(movieId = movieId, language = language)
            // Fallback overview to en-US if current language overview is empty
            if (details.overview.isNull_or_blank() && language != "en-US") {
                val englishDetails = api.getMovieDetails(movieId = movieId, language = "en-US")
                if (!englishDetails.overview.isNullOrBlank()) {
                    details = details.copy(overview = englishDetails.overview)
                }
            }
            Result.success(details)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWatchProviders(movieId: Int, regionCode: String = "ID"): WatchProviderRegion? {
        return try {
            val response = api.getWatchProviders(movieId)
            response.results?.get(regionCode.uppercase())
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMovieCredits(movieId: Int, language: String): List<CastDto> {
        return try {
            api.getMovieCredits(movieId, language).cast ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun String?.isNull_or_blank(): Boolean {
        return this == null || this.trim().isEmpty()
    }
}
