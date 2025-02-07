package com.awesome.news_app.api


import NewsApi
import com.awesome.news_app.util.Constants.Companion.base_url
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory




object RetrofitInstance {
    private const val BASE_URL = base_url

    val api: NewsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }
}