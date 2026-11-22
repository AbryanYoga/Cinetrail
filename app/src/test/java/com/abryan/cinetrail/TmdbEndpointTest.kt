package com.abryan.cinetrail

import com.abryan.cinetrail.data.api.TmdbService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TmdbEndpointTest {

    private lateinit var tmdbService: TmdbService
    private var lastRecordedUrl: String = ""

    @Before
    fun setup() {
        val capturingInterceptor = Interceptor { chain ->
            val request = chain.request()
            lastRecordedUrl = request.url.toString()

            val mockResponseBody = when {
                request.url.encodedPath.contains("genre/movie/list") -> """{"genres":[]}"""
                request.url.encodedPath.contains("watch/providers") -> """{"id":1,"results":{}}"""
                request.url.encodedPath.matches(Regex(".*/movie/\\d+$")) -> """{"id":1,"title":"Mock"}"""
                else -> """{"page":1,"results":[],"total_pages":1,"total_results":0}"""
            }

            Response.Builder()
                .request(request)
                .protocol(okhttp3.Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(mockResponseBody.toResponseBody("application/json".toMediaType()))
                .build()
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(capturingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        tmdbService = retrofit.create(TmdbService::class.java)
    }

    @Test
    fun getTrendingMovies_buildsCorrectUrl() = runBlocking {
        tmdbService.getTrendingMovies(language = "id-ID", page = 1)
        assertTrue(lastRecordedUrl.contains("/trending/movie/week"))
        assertTrue(lastRecordedUrl.contains("language=id-ID"))
        assertTrue(lastRecordedUrl.contains("page=1"))
    }

    @Test
    fun getPopularMovies_buildsCorrectUrl() = runBlocking {
        tmdbService.getPopularMovies(language = "en-US", page = 2)
        assertTrue(lastRecordedUrl.contains("/movie/popular"))
        assertTrue(lastRecordedUrl.contains("language=en-US"))
        assertTrue(lastRecordedUrl.contains("page=2"))
    }

    @Test
    fun getTopRatedMovies_buildsCorrectUrl() = runBlocking {
        tmdbService.getTopRatedMovies(language = "id-ID", page = 1)
        assertTrue(lastRecordedUrl.contains("/movie/top_rated"))
        assertTrue(lastRecordedUrl.contains("language=id-ID"))
    }

    @Test
    fun discoverMovies_withCountryAndGenre_buildsCorrectUrl() = runBlocking {
        tmdbService.discoverMovies(
            language = "id-ID",
            page = 1,
            country = "KR",
            genreId = 28,
            sortBy = "popularity.desc"
        )
        assertTrue(lastRecordedUrl.contains("/discover/movie"))
        assertTrue(lastRecordedUrl.contains("with_origin_country=KR"))
        assertTrue(lastRecordedUrl.contains("with_genres=28"))
        assertTrue(lastRecordedUrl.contains("sort_by=popularity.desc"))
        assertTrue(lastRecordedUrl.contains("language=id-ID"))
    }

    @Test
    fun getGenres_buildsCorrectUrl() = runBlocking {
        tmdbService.getGenres(language = "id-ID")
        assertTrue(lastRecordedUrl.contains("/genre/movie/list"))
        assertTrue(lastRecordedUrl.contains("language=id-ID"))
    }

    @Test
    fun searchMovies_buildsCorrectUrl() = runBlocking {
        tmdbService.searchMovies(query = "Avatar", language = "en-US", page = 1)
        assertTrue(lastRecordedUrl.contains("/search/movie"))
        assertTrue(lastRecordedUrl.contains("query=Avatar"))
        assertTrue(lastRecordedUrl.contains("language=en-US"))
    }

    @Test
    fun getMovieDetails_buildsCorrectUrl() = runBlocking {
        tmdbService.getMovieDetails(movieId = 550, language = "id-ID")
        assertTrue(lastRecordedUrl.contains("/movie/550"))
        assertTrue(lastRecordedUrl.contains("language=id-ID"))
    }

    @Test
    fun getWatchProviders_buildsCorrectUrl() = runBlocking {
        tmdbService.getWatchProviders(movieId = 550)
        assertTrue(lastRecordedUrl.contains("/movie/550/watch/providers"))
    }
}
