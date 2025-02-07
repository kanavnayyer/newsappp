package com.awesome.news_app.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.awesome.news_app.api.RetrofitInstance
import com.awesome.news_app.model.Article
import com.awesome.news_app.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val _articlesData = MutableLiveData<List<Article>>()
    val articlesData: LiveData<List<Article>> = _articlesData

    private val articlesList = mutableListOf<Article>()
    private val articleUrlsSet = mutableSetOf<String>()
    private var currentPage = Constants.page
    private var isLastPage = false

    private var selectedSortOptions = mutableSetOf<String>()

    fun loadNextPage() {
        if (isLastPage) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getTopHeadlines(
                    country = Constants.country,
                    apiKey = Constants.api_key,
                    page = currentPage
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val newArticles = response.body()?.articles?.filter {
                            it.url != null && articleUrlsSet.add(it.url)
                        } ?: emptyList()

                        if (newArticles.isEmpty()) {
                            isLastPage = true
                        } else {
                            articlesList.addAll(newArticles)
                            sortArticles()
                            currentPage++
                        }
                    } else {
                        Log.e("API_ERROR", "Error: ${response.code()} - ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Exception: ${e.message}")
            }
        }
    }

    fun setSelectedSortOptions(options: Set<String>) {
        selectedSortOptions = options.toMutableSet()
        sortArticles()
    }

    fun updateArticles(articles: List<Article>) {
        articlesList.clear()
        articlesList.addAll(articles)
        sortArticles()
    }

    private fun sortArticles() {
        if (selectedSortOptions.isEmpty()) {
            _articlesData.value = articlesList
            return
        }

        val sortedList = articlesList.sortedWith(Comparator { article1, article2 ->
            var comparisonResult = 0

            for (option in selectedSortOptions) {
                when (option) {
                    "author" -> {
                        val result = compareValues(article1.author, article2.author)
                        if (result != 0) comparisonResult = result
                    }
                    "title" -> {
                        val result = compareValues(article1.title, article2.title)
                        if (result != 0) comparisonResult = result
                    }
                    "date" -> {
                        val result = compareValues(article1.publishedAt, article2.publishedAt)
                        if (result != 0) comparisonResult = result
                    }
                    "source" -> {
                        val result = compareValues(article1.source.name, article2.source.name)
                        if (result != 0) comparisonResult = result
                    }
                }
            }

            comparisonResult
        })

        _articlesData.value = sortedList
    }
}
