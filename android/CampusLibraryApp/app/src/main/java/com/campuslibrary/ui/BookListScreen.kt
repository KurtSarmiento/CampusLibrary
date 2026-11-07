package com.campuslibrary.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuslibrary.Book
import com.campuslibrary.BookRepository
import com.campuslibrary.BookViewModel
import com.campuslibrary.BookUiState
import com.campuslibrary.ui.theme.CampusInk
import com.campuslibrary.ui.theme.CampusLine
import com.campuslibrary.ui.theme.CampusMuted
import com.campuslibrary.ui.theme.CampusRed
import com.campuslibrary.ui.theme.CampusSurface
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = "THE SHELVES",
                    color = CampusRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                )
                Text(
                    text = "Collection",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = CampusInk
                )
            }
        }
        items(books, key = { it.id }) { book ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CampusLine),
                color = CampusSurface
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CampusInk
                        )
                        Text(
                            text = "${book.author ?: "Unknown author"} · ISBN: ${book.isbn}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CampusMuted
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "Year: ${book.publishedYear}",
                                style = MaterialTheme.typography.labelSmall,
                                color = CampusMuted
                            )
                            if (book.isOnLoan) {
                                Surface(
                                    color = Color(0xFFF5DFDA)
                                ) {
                                    Text(
                                        text = "On loan to ${book.loanedToMemberName ?: "a member"}",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF87392F)
                                    )
                                }
                            } else {
                                Surface(
                                    color = Color(0xFFDCEFE1)
                                ) {
                                    Text(
                                        text = "Available",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF245C43)
                                    )
                                }
                            }
                        }
                    }
                    Button(
                        onClick = { onBookClick(book.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = CampusRed),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
                    ) {
                        Text(text = "View", color = Color.White)
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
        CircularProgressIndicator(color = CampusRed)
    }
}

@Composable
private fun EmptyScreen(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, color = CampusMuted, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Error: $message", color = Color(0xFF87392F), fontWeight = FontWeight.Bold)
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = CampusRed),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Retry", color = Color.White)
        }
    }
}
