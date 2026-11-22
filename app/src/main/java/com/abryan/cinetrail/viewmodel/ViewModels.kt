package com.abryan.cinetrail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abryan.cinetrail.R
import com.abryan.cinetrail.data.model.GenreDto
import com.abryan.cinetrail.data.model.MovieDetailDto
import com.abryan.cinetrail.data.model.MovieDto
import com.abryan.cinetrail.data.model.WatchProviderRegion
import com.abryan.cinetrail.data.repository.MovieRepository
import com.abryan.cinetrail.ui.component.CountryOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val trendingMovies: List<MovieDto> = emptyList(),
    val popularMovies: List<MovieDto> = emptyList(),
    val genres: List<GenreDto> = emptyList(),
    val genresMap: Map<Int, String> = emptyMap(),
    val selectedCountry: String? = null,
    val selectedGenreId: Int? = null,
    val currentLanguage: String = "en-US"
)

val COUNTRY_OPTIONS = listOf(
    CountryOption(null, R.string.all_countries),
    CountryOption("ID", R.string.country_id),
    CountryOption("US", R.string.country_us),
    CountryOption("KR", R.string.country_kr),
    CountryOption("JP", R.string.country_jp),
    CountryOption("IN", R.string.country_in),
    CountryOption("GB", R.string.country_gb)
)

class DashboardViewModel(
    private val repository: MovieRepository = MovieRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isError = false)
            try {
                val language = _uiState.value.currentLanguage
                val genres = repository.getGenres(language)
                val genresMap = genres.associate { it.id to it.name }
                val trending = repository.getTrendingMovies(language)
                val popular = repository.getPopularMovies(
                    language = language,
                    country = _uiState.value.selectedCountry,
                    genreId = _uiState.value.selectedGenreId
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = false,
                    genres = genres,
                    genresMap = genresMap,
                    trendingMovies = trending,
                    popularMovies = popular
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = true
                )
            }
        }
    }

    fun selectCountry(countryCode: String?) {
        if (_uiState.value.selectedCountry == countryCode) return
        _uiState.value = _uiState.value.copy(selectedCountry = countryCode)
        loadMovies()
    }

    fun selectGenre(genreId: Int?) {
        if (_uiState.value.selectedGenreId == genreId) return
        _uiState.value = _uiState.value.copy(selectedGenreId = genreId)
        loadMovies()
    }

    fun updateLanguage(language: String) {
        if (_uiState.value.currentLanguage == language) return
        _uiState.value = _uiState.value.copy(currentLanguage = language)
        loadData()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isError = false)
            try {
                val language = _uiState.value.currentLanguage
                val popular = repository.getPopularMovies(
                    language = language,
                    country = _uiState.value.selectedCountry,
                    genreId = _uiState.value.selectedGenreId
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = false,
                    popularMovies = popular
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = true
                )
            }
        }
    }
}

data class DetailUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val movieDetail: MovieDetailDto? = null,
    val watchProviderRegion: WatchProviderRegion? = null,
    val selectedRegion: String = "ID"
)

class DetailViewModel(
    private val repository: MovieRepository = MovieRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadMovieDetails(movieId: Int, language: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isError = false)
            val detailResult = repository.getMovieDetails(movieId, language)
            detailResult.fold(
                onSuccess = { detail ->
                    val providers = repository.getWatchProviders(movieId, _uiState.value.selectedRegion)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isError = false,
                        movieDetail = detail,
                        watchProviderRegion = providers
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isError = true
                    )
                }
            )
        }
    }

    fun changeRegion(region: String, movieId: Int) {
        _uiState.value = _uiState.value.copy(selectedRegion = region)
        viewModelScope.launch {
            val providers = repository.getWatchProviders(movieId, region)
            _uiState.value = _uiState.value.copy(watchProviderRegion = providers)
        }
    }
}

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val searchResults: List<MovieDto> = emptyList()
)

class SearchViewModel(
    private val repository: MovieRepository = MovieRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChange(newQuery: String, language: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)
        if (newQuery.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), isLoading = false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isError = false)
            val results = repository.searchMovies(newQuery, language)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                searchResults = results
            )
        }
    }
}
