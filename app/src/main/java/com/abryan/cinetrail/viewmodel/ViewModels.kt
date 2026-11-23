package com.abryan.cinetrail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abryan.cinetrail.R
import com.abryan.cinetrail.data.model.CastDto
import com.abryan.cinetrail.data.model.GenreDto
import com.abryan.cinetrail.data.model.MovieDetailDto
import com.abryan.cinetrail.data.model.MovieDto
import com.abryan.cinetrail.data.model.WatchProviderRegion
import com.abryan.cinetrail.data.repository.MovieRepository
import com.abryan.cinetrail.ui.component.CountryOption
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class MovieCategory {
    POPULAR,
    TOP_RATED
}

data class DashboardUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val trendingMovies: List<MovieDto> = emptyList(),
    val displayedMovies: List<MovieDto> = emptyList(),
    val selectedCategory: MovieCategory = MovieCategory.POPULAR,
    val genres: List<GenreDto> = emptyList(),
    val genresMap: Map<Int, String> = emptyMap(),
    val selectedCountry: String? = null,
    val selectedGenreId: Int? = null,
    val currentLanguage: String = "en-US"
) {
    val popularMovies: List<MovieDto> get() = displayedMovies
}

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
                val state = _uiState.value
                val language = state.currentLanguage
                val genres = repository.getGenres(language)
                val genresMap = genres.associate { it.id to it.name }
                val trending = repository.getTrendingMovies(language)
                val movies = if (state.selectedCategory == MovieCategory.TOP_RATED) {
                    repository.getTopRatedMovies(
                        language = language,
                        country = state.selectedCountry,
                        genreId = state.selectedGenreId
                    )
                } else {
                    repository.getPopularMovies(
                        language = language,
                        country = state.selectedCountry,
                        genreId = state.selectedGenreId
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = false,
                    genres = genres,
                    genresMap = genresMap,
                    trendingMovies = trending,
                    displayedMovies = movies
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = true
                )
            }
        }
    }

    fun selectCategory(category: MovieCategory) {
        if (_uiState.value.selectedCategory == category) return
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        loadMovies()
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
                val state = _uiState.value
                val language = state.currentLanguage
                val movies = if (state.selectedCategory == MovieCategory.TOP_RATED) {
                    repository.getTopRatedMovies(
                        language = language,
                        country = state.selectedCountry,
                        genreId = state.selectedGenreId
                    )
                } else {
                    repository.getPopularMovies(
                        language = language,
                        country = state.selectedCountry,
                        genreId = state.selectedGenreId
                    )
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = false,
                    displayedMovies = movies
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
    val cast: List<CastDto> = emptyList(),
    val watchProviderRegion: WatchProviderRegion? = null,
    val selectedRegion: String = "ID",
    val isFavorite: Boolean = false
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
                    val cast = repository.getMovieCredits(movieId, language)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isError = false,
                        movieDetail = detail,
                        cast = cast,
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

    fun toggleFavorite() {
        _uiState.value = _uiState.value.copy(isFavorite = !_uiState.value.isFavorite)
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

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String, language: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)
        searchJob?.cancel()
        if (newQuery.isBlank()) {
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(),
                isLoading = false,
                isError = false
            )
            return
        }

        searchJob = viewModelScope.launch {
            delay(350) // Debounce 350ms untuk efisiensi request ke API
            _uiState.value = _uiState.value.copy(isLoading = true, isError = false)
            try {
                val results = repository.searchMovies(newQuery.trim(), language)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    searchResults = results
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
