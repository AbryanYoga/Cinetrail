package com.abryan.cinetrail.data.api

import com.abryan.cinetrail.data.model.GenreResponse
import com.abryan.cinetrail.data.model.MovieDetailDto
import com.abryan.cinetrail.data.model.MovieResponse
import com.abryan.cinetrail.data.model.WatchProviderResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbService {

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("language") language: String,
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String,
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("language") language: String,
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("language") language: String,
        @Query("page") page: Int = 1,
        @Query("with_origin_country") country: String? = null,
        @Query("with_genres") genreId: Int? = null,
        @Query("sort_by") sortBy: String = "popularity.desc"
    ): MovieResponse

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("language") language: String
    ): GenreResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("language") language: String,
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("movie/{id}")
    suspend fun getMovieDetails(
        @Path("id") movieId: Int,
        @Query("language") language: String
    ): MovieDetailDto

    @GET("movie/{id}/watch/providers")
    suspend fun getWatchProviders(
        @Path("id") movieId: Int
    ): WatchProviderResponse
}
