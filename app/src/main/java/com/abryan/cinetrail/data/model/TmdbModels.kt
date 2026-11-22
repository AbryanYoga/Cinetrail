package com.abryan.cinetrail.data.model

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<MovieDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class MovieDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("genre_ids") val genreIds: List<Int>?,
    @SerializedName("origin_country") val originCountry: List<String>?
) {
    fun getFullPosterUrl(): String? {
        return posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
    }

    fun getFullBackdropUrl(): String? {
        return backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" }
    }
}

data class GenreDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class GenreResponse(
    @SerializedName("genres") val genres: List<GenreDto>
)

data class MovieDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("genres") val genres: List<GenreDto>?,
    @SerializedName("origin_country") val originCountry: List<String>?
) {
    fun getFullPosterUrl(): String? {
        return posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
    }

    fun getFullBackdropUrl(): String? {
        return backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" }
    }
}

data class WatchProviderDto(
    @SerializedName("provider_id") val providerId: Int,
    @SerializedName("provider_name") val providerName: String,
    @SerializedName("logo_path") val logoPath: String?,
    @SerializedName("display_priority") val displayPriority: Int?
) {
    fun getFullLogoUrl(): String? {
        return logoPath?.let { "https://image.tmdb.org/t/p/w185$it" }
    }
}

data class WatchProviderRegion(
    @SerializedName("link") val link: String?,
    @SerializedName("flatrate") val flatrate: List<WatchProviderDto>?,
    @SerializedName("rent") val rent: List<WatchProviderDto>?,
    @SerializedName("buy") val buy: List<WatchProviderDto>?
)

data class WatchProviderResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("results") val results: Map<String, WatchProviderRegion>?
)
