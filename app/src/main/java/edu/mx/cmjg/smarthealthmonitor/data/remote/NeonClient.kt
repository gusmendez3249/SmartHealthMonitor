package edu.mx.cmjg.smarthealthmonitor.data.remote

import edu.mx.cmjg.smarthealthmonitor.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NeonClient {
    private const val BASE_URL = "https://${BuildConfig.NEON_HOST}/"

    val AUTH_HEADER  = "Bearer ${BuildConfig.NEON_API_KEY}"
    val CONN_STRING  = BuildConfig.NEON_CONN_STRING

    val api: NeonApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }).build())
            .build()
            .create(NeonApiService::class.java)
    }
}
