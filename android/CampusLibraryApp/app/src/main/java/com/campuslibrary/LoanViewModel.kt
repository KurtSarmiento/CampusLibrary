package com.campuslibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoanUiState<out T> {
    object Loading : LoanUiState<Nothing>()
    object Empty : LoanUiState<Nothing>()
    data class Success<T>(val data: T) : LoanUiState<T>()
    data class Error(val message: String) : LoanUiState<Nothing>()
}

class LoanViewModel(private val repository: BookRepository) : ViewModel() {
    private val _loans = MutableStateFlow<LoanUiState<List<Loan>>>(LoanUiState.Loading)
    val loans: StateFlow<LoanUiState<List<Loan>>> = _loans.asStateFlow()

    private val _members = MutableStateFlow<LoanUiState<List<Member>>>(LoanUiState.Loading)
    val members: StateFlow<LoanUiState<List<Member>>> = _members.asStateFlow()

    init {
        loadLoans()
        loadMembers()
    }

    fun loadLoans() {
        viewModelScope.launch {
            _loans.value = LoanUiState.Loading
            val result = repository.getLoans()
            _loans.value = if (result.isSuccess) {
                val data = result.getOrNull().orEmpty()
                if (data.isEmpty()) LoanUiState.Empty else LoanUiState.Success(data)
            } else {
                LoanUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun loadMembers() {
        viewModelScope.launch {
            _members.value = LoanUiState.Loading
            val result = repository.getMembers()
            _members.value = if (result.isSuccess) {
                val data = result.getOrNull().orEmpty()
                if (data.isEmpty()) LoanUiState.Empty else LoanUiState.Success(data)
            } else {
                LoanUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun returnLoan(loanId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.returnLoan(loanId)
            if (result.isSuccess) {
                loadLoans()
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Return failed")
            }
        }
    }
}
