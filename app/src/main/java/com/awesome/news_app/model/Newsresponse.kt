package com.awesome.news_app.model


data class NewResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)