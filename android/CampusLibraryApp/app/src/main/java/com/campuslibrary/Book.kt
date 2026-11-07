package com.campuslibrary

data class Book(
    val id: Int,
    val isbn: String,
    val title: String,
    val author: String?,
    val publishedYear: Int
)
