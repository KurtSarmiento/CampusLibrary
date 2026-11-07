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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campuslibrary.Book
import com.campuslibrary.BookRepository
import com.campuslibrary.BookViewModel
import com.campuslibrary.BookUiState
import com.campuslibrary.ui.theme.SurfaceContent

@Composable
fun BookListScreen(
    viewModel: BookViewModel,
    repository: BookRepository,
    onBookClick: (Int) -> Unit = {},
    onBorrowError: (String) -> Unit = {}
) {
    val uiState by viewModel.books.collectAsState()

    SurfaceContent {
        when (val state = uiState) {
            is BookUiState.Loading -> LoadingScreen()
            is BookUiState.Empty -> EmptyScreen("No books available")
            is BookUiState.Error -> ErrorScreen(state.message) { viewModel.loadBooks() }
            is BookUiState.Success -> {
                BookListContent(
                    books = state.data,
                    onBookClick = onBookClick,
                    onBorrowError = onBorrowError
                )
            }
        }
    }
}

@Composable
private fun BookListContent(
    books: List<Book>,
    onBookClick: (Int) -> Unit,
    onBorrowError: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(books, key = { it.id }) { book ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = book.title, style = MaterialTheme.typography.titleMedium)
                        Text(text = "${book.author ?: "Unknown author"} · ${book.isbn}")
                        Text(text = "Year: ${book.publishedYear}", style = MaterialTheme.typography.labelSmall)
                    }
                    TextButton(
                        onClick = { onBookClick(book.id) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text(text = "View")
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
