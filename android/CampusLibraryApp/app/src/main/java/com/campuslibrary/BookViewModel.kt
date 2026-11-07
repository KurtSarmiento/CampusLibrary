package com.campuslibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BookUiState<out T> {
    object Loading : BookUiState<Nothing>()
    object Empty : BookUiState<Nothing>()
    data class Success<T>(val data: T) : BookUiState<T>()
    data class Error(val message: String) : BookUiState<Nothing>()
}

class BookViewModel(private val repository: BookRepository) : ViewModel() {
    private val _books = MutableStateFlow<BookUiState<List<Book>>>(BookUiState.Loading)
    val books: StateFlow<BookUiState<List<Book>>> = _books.asStateFlow()

    private val _selected = MutableStateFlow<BookUiState<Book>>(BookUiState.Loading)
    val selected: StateFlow<BookUiState<Book>> = _selected.asStateFlow()

    private val _isBorrowing = MutableStateFlow(false)
    val isBorrowing: StateFlow<Boolean> = _isBorrowing.asStateFlow()

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _books.value = BookUiState.Loading
            val result = repository.getBooks()
            _books.value = if (result.isSuccess) {
                val data = result.getOrNull().orEmpty()
                if (data.isEmpty()) BookUiState.Empty else BookUiState.Success(data)
            } else {
                BookUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun loadBook(id: Int) {
        viewModelScope.launch {
            _selected.value = BookUiState.Loading
            val result = repository.getBook(id)
            _selected.value = if (result.isSuccess) {
                val data = result.getOrNull()
                if (data != null) BookUiState.Success(data) else BookUiState.Empty
            } else {
                BookUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun borrowBook(bookId: Int, memberId: Int, onSuccess: (Loan) -> Unit, onError: (String) -> Unit) {
        if (_isBorrowing.value) return
        viewModelScope.launch {
            _isBorrowing.value = true
            val result = repository.borrowBook(bookId, memberId)
            _isBorrowing.value = false
            if (result.isSuccess) {
                val loan = result.getOrNull()
                if (loan != null) {
                    loadBooks()
                    loadBook(bookId)
                    onSuccess(loan)
                }
            } else {
                onError(result.exceptionOrNull()?.message ?: "Borrow failed")
            }
        }
    }
}
