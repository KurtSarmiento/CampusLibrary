package com.campuslibrary

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("books")
    suspend fun getBooks(): Response<List<Book>>

    @GET("books/{id}")
    suspend fun getBook(@Path("id") id: Int): Response<Book>

    @POST("loans")
    suspend fun borrowBook(@Body request: BorrowRequest): Response<Loan>

    @GET("loans")
    suspend fun getLoans(): Response<List<Loan>>
}
