package com.abryan.cinetrail.data.api

import com.abryan.cinetrail.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        val apiKey = BuildConfig.TMDB_API_KEY.trim()

        val newRequest = if (apiKey.isNotBlank()) {
            if (apiKey.startsWith("ey")) {
                // Mendukung token akses Bearer TMDB v4
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $apiKey")
                    .build()
            } else {
                // Mendukung API key TMDB v3 standar via parameter query
                val newUrl = originalUrl.newBuilder()
                    .addQueryParameter("api_key", apiKey)
                    .build()
                originalRequest.newBuilder().url(newUrl).build()
            }
        } else {
            originalRequest
        }
        chain.proceed(newRequest)
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val instance: TmdbService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbService::class.java)
    }
}

