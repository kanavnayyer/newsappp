package com.awesome.news_app.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.awesome.news_app.model.SavedArticle

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: SavedArticle)

    @Delete
    suspend fun deleteArticle(article: SavedArticle)

    @Query("SELECT * FROM saved_articles ORDER BY id DESC")
    fun getAllArticles(): LiveData<List<SavedArticle>>

    @Query("DELETE FROM saved_articles WHERE title = :title")
    suspend fun deleteArticleByTitle(title: String)





    @Query("SELECT EXISTS(SELECT 1 FROM saved_articles WHERE title = :title)")
    suspend fun doesArticleExist(title: String): Boolean

}

