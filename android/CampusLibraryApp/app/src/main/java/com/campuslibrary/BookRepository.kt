package com.campuslibrary

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BookRepository(private val apiService: ApiService) {
    suspend fun getBooks(): Result<List<Book>> {
        return try {
            val response = apiService.getBooks()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception("Failed to load books: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBook(id: Int): Result<Book> {
        return try {
            val response = apiService.getBook(id)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception("Failed to load book: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun borrowBook(bookId: Int, memberId: Int): Result<Loan> {
        return try {
            val response = apiService.borrowBook(BorrowRequest(bookId, memberId))
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else if (response.code() == 409) {
                Result.failure(Exception("This book is already on loan"))
            } else {
                Result.failure(Exception("Borrow failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLoans(): Result<List<Loan>> {
        return try {
            val response = apiService.getLoans()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception("Failed to load loans: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMembers(): Result<List<Member>> {
        return try {
            val response = apiService.getMembers()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception("Failed to load members: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun returnLoan(loanId: Int): Result<Unit> {
        return try {
            val response = apiService.returnLoan(loanId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Return failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun booksFlow(): Flow<Result<List<Book>>> = flow {
        emit(getBooks())
    }

    fun loansFlow(): Flow<Result<List<Loan>>> = flow {
        emit(getLoans())
    }
}
