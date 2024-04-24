package com.example.facial

import java.sql.Date

data class ArticleModel (
    val articles: List<Data>
){
    data class Data (val id: String?, val title: String?, val category: String?, val body: String?, val created_at: String?, val updated_at: String?)
}