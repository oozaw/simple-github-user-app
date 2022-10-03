package com.dicoding.githubuserapi.data.remote.api

import com.dicoding.githubuserapi.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
   companion object {
      private const val API_KEY = BuildConfig.API_KEY
      private const val BASE_URL = BuildConfig.BASE_URL

      fun getApiService(): ApiService {
         val loggingInterceptor = if (BuildConfig.DEBUG) { HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY) }
                                    else { HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE) }
         val client = OkHttpClient.Builder()
            .addInterceptor{ chain ->
               val original = chain.request()
               val requestBuilder = original.newBuilder()
                  .header("Authorization", API_KEY)
               val request = requestBuilder.build()
               chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .build()
         val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

         return retrofit.create(ApiService::class.java)
      }
   }
}