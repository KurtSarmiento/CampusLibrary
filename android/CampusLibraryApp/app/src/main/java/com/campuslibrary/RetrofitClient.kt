package com.campuslibrary

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5238/api/"
    private var retrofit: Retrofit? = null

    fun getInstance(): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: buildRetrofit().also { retrofit = it }
        }
    }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
