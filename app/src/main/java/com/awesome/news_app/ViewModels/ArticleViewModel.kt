package com.awesome.news_app.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.awesome.news_app.db.ArticleDatabase
import com.awesome.news_app.model.SavedArticle
import com.awesome.news_app.repo.ArticleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ArticleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ArticleRepository
    val allArticles: LiveData<List<SavedArticle>>

    init {
        val articleDao = ArticleDatabase.getDatabase(application).articleDao()
        repository = ArticleRepository(articleDao)
        allArticles = repository.allArticles
    }

    fun insertArticle(article: SavedArticle) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(article)
        }
    }

    fun deleteArticleByTitle(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteByTitle(title)
        }
    }

    fun checkIfArticleExists(title: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch (Dispatchers.IO){
            val exists = repository.articleExists(title)
            callback(exists)
        }
    }

    fun formatDate(isoDate: String?): String {
        if (isoDate.isNullOrEmpty()) return "No Date Available"
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        return try {
            val date = inputFormat.parse(isoDate)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            "Invalid Date"
        }
    }
}
