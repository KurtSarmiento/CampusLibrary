package com.campuslibrary.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campuslibrary.Book
import com.campuslibrary.BookViewModel
import com.campuslibrary.BookUiState
import com.campuslibrary.Loan
import com.campuslibrary.ui.theme.SurfaceContent

@Composable
fun BookDetailScreen(
    viewModel: BookViewModel,
    bookId: Int,
    onBorrowSuccess: (Loan) -> Unit = {},
    onBorrowError: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.selected.collectAsState()

    LaunchedEffect(bookId) {
        viewModel.loadBook(bookId)
    }

    SurfaceContent {
        when (val state = uiState) {
            is BookUiState.Loading -> LoadingScreen()
            is BookUiState.Empty -> EmptyScreen("Book not found")
            is BookUiState.Error -> ErrorScreen(state.message) { viewModel.loadBook(bookId) }
            is BookUiState.Success -> {
                BookDetailContent(
                    book = state.data,
                    viewModel = viewModel,
                    onBorrowSuccess = onBorrowSuccess,
                    onBorrowError = onBorrowError,
                    onNavigateBack = onNavigateBack
                )
            }
        }
    }
}

@Composable
private fun BookDetailContent(
    book: Book,
    viewModel: BookViewModel,
    onBorrowSuccess: (Loan) -> Unit,
    onBorrowError: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = book.title, style = MaterialTheme.typography.headlineMedium)
                Text(text = book.author ?: "Unknown author")
                Text(text = "ISBN: ${book.isbn}")
                Text(text = "Published: ${book.publishedYear}")
            }
        }
        TextButton(onClick = { onNavigateBack() }) {
            Text(text = "Back to list")
        }
        Button(
            onClick = {
                viewModel.borrowBook(book.id, memberId = 1,
                    onSuccess = { loan -> onBorrowSuccess(loan) },
                    onError = { message -> onBorrowError(message) }
                )
            }
        ) {
            Text(text = "Borrow")
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
