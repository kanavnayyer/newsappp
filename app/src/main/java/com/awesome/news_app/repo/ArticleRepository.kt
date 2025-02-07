package com.awesome.news_app.repo

import com.awesome.news_app.db.ArticleDao
import com.awesome.news_app.model.SavedArticle

class ArticleRepository(private val articleDao: ArticleDao) {

    val allArticles = articleDao.getAllArticles()

    suspend fun insert(article: SavedArticle) {
        articleDao.insertArticle(article)
    }



    suspend fun deleteByTitle(title: String) {
        articleDao.deleteArticleByTitle(title)
    }



    suspend fun articleExists(title: String): Boolean {
        return articleDao.doesArticleExist(title)
    }


}
