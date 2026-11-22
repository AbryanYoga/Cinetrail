package com.abryan.cinetrail

import com.abryan.cinetrail.data.model.GenreResponse
import com.abryan.cinetrail.data.model.MovieDetailDto
import com.abryan.cinetrail.data.model.MovieResponse
import com.abryan.cinetrail.data.model.WatchProviderResponse
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TmdbModelsTest {

    private val gson = Gson()

    @Test
    fun movieResponse_deserialization_isCorrect() {
        val json = """
            {
                "page": 1,
                "results": [
                    {
                        "id": 101,
                        "title": "Cinetrail Movie",
                        "overview": "Sinopsis film pengujian.",
                        "poster_path": "/test_poster.jpg",
                        "backdrop_path": "/test_backdrop.jpg",
                        "vote_average": 8.7,
                        "release_date": "2026-11-20",
                        "genre_ids": [28, 12],
                        "origin_country": ["ID"]
                    }
                ],
                "total_pages": 10,
                "total_results": 200
            }
        """.trimIndent()

        val response = gson.fromJson(json, MovieResponse::class.java)
        assertEquals(1, response.page)
        assertEquals(1, response.results.size)
        assertEquals(10, response.totalPages)
        assertEquals(200, response.totalResults)

        val movie = response.results.first()
        assertEquals(101, movie.id)
        assertEquals("Cinetrail Movie", movie.title)
        assertEquals("Sinopsis film pengujian.", movie.overview)
        assertEquals(8.7, movie.voteAverage ?: 0.0, 0.01)
        assertEquals("https://image.tmdb.org/t/p/w500/test_poster.jpg", movie.getFullPosterUrl())
        assertEquals("https://image.tmdb.org/t/p/w780/test_backdrop.jpg", movie.getFullBackdropUrl())
        assertEquals(listOf(28, 12), movie.genreIds)
        assertEquals(listOf("ID"), movie.originCountry)
    }

    @Test
    fun movieDetailDto_deserialization_isCorrect() {
        val json = """
            {
                "id": 500,
                "title": "Interstellar",
                "overview": "Perjalanan melintasi luar angkasa.",
                "poster_path": "/poster.jpg",
                "backdrop_path": "/backdrop.jpg",
                "vote_average": 8.6,
                "release_date": "2014-11-05",
                "runtime": 169,
                "genres": [
                    { "id": 12, "name": "Adventure" },
                    { "id": 18, "name": "Drama" },
                    { "id": 878, "name": "Sci-Fi" }
                ],
                "origin_country": ["US"]
            }
        """.trimIndent()

        val detail = gson.fromJson(json, MovieDetailDto::class.java)
        assertEquals(500, detail.id)
        assertEquals("Interstellar", detail.title)
        assertEquals(169, detail.runtime)
        assertEquals(3, detail.genres?.size)
        assertEquals("Sci-Fi", detail.genres?.get(2)?.name)
        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", detail.getFullPosterUrl())
    }

    @Test
    fun watchProviderResponse_deserialization_isCorrect() {
        val json = """
            {
                "id": 100,
                "results": {
                    "ID": {
                        "link": "https://www.themoviedb.org/movie/100/watch?locale=ID",
                        "flatrate": [
                            {
                                "provider_id": 8,
                                "provider_name": "Netflix",
                                "logo_path": "/netflix.jpg",
                                "display_priority": 1
                            }
                        ],
                        "rent": [
                            {
                                "provider_id": 2,
                                "provider_name": "Apple TV",
                                "logo_path": "/apple.jpg",
                                "display_priority": 2
                            }
                        ],
                        "buy": []
                    }
                }
            }
        """.trimIndent()

        val response = gson.fromJson(json, WatchProviderResponse::class.java)
        assertNotNull(response.results)
        val idRegion = response.results?.get("ID")
        assertNotNull(idRegion)
        assertEquals(1, idRegion?.flatrate?.size)
        assertEquals("Netflix", idRegion?.flatrate?.first()?.providerName)
        assertEquals("https://image.tmdb.org/t/p/w185/netflix.jpg", idRegion?.flatrate?.first()?.getFullLogoUrl())
        assertEquals(1, idRegion?.rent?.size)
        assertTrue(idRegion?.buy?.isEmpty() == true)
    }

    @Test
    fun genreResponse_deserialization_isCorrect() {
        val json = """
            {
                "genres": [
                    { "id": 28, "name": "Action" },
                    { "id": 35, "name": "Comedy" }
                ]
            }
        """.trimIndent()

        val response = gson.fromJson(json, GenreResponse::class.java)
        assertEquals(2, response.genres.size)
        assertEquals("Action", response.genres[0].name)
        assertEquals("Comedy", response.genres[1].name)
    }
}
