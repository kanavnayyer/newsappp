package com.awesome.news_app.model

import androidx.room.Entity
import androidx.room.PrimaryKey





@Entity(tableName = "saved_articles")
data class SavedArticle(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val author: String? = null,
    val imagePath: String? = null,
    val content: String? = null,
    val sourceName:String?=null,
    val publishedAt:String?=null
)

