package com.campuslibrary

data class Loan(
    val id: Int,
    val bookId: Int,
    val memberId: Int,
    val borrowedAt: String,
    val returnedAt: String?,
    val book: Book,
    val member: Member
)
