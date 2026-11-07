package com.campuslibrary.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campuslibrary.BookRepository
import com.campuslibrary.Loan
import com.campuslibrary.LoanUiState
import com.campuslibrary.LoanViewModel
import com.campuslibrary.ui.theme.SurfaceContent

@Composable
fun MyLoansScreen(
    viewModel: LoanViewModel,
    repository: BookRepository
) {
    val uiState by viewModel.loans.collectAsState()

    SurfaceContent {
        when (val state = uiState) {
            is LoanUiState.Loading -> LoadingScreen()
            is LoanUiState.Empty -> EmptyScreen("No active loans")
            is LoanUiState.Error -> ErrorScreen(state.message) { viewModel.loadLoans() }
            is LoanUiState.Success -> {
                LoansContent(loans = state.data, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun LoansContent(loans: List<Loan>, viewModel: LoanViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(loans, key = { it.id }) { loan ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = loan.book.title, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Member: ${loan.member.name}")
                        Text(text = "Borrowed: ${loan.borrowedAt}")
                    }
                    Button(
                        onClick = {
                            viewModel.returnLoan(loan.id,
                                onSuccess = { viewModel.loadLoans() },
                                onError = { message -> /* could surface a snackbar; retry kept simple */ }
                            )
                        }
                    ) {
                        Text(text = "Return")
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyScreen(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message)
    }
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Error: $message", color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry) {
            Text(text = "Retry")
        }
    }
}
