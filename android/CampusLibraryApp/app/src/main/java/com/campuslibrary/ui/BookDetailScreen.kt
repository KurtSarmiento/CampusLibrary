package com.campuslibrary.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campuslibrary.Book
import com.campuslibrary.BookViewModel
import com.campuslibrary.BookUiState
import com.campuslibrary.Loan
import com.campuslibrary.LoanUiState
import com.campuslibrary.LoanViewModel
import com.campuslibrary.Member
import com.campuslibrary.ui.theme.SurfaceContent

@Composable
fun BookDetailScreen(
    viewModel: BookViewModel,
    loanViewModel: LoanViewModel,
    bookId: Int,
    onBorrowSuccess: (Loan) -> Unit = {},
    onBorrowError: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.selected.collectAsState()
    val membersState by loanViewModel.members.collectAsState()
    var expandedMenu by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf<Member?>(null) }

    LaunchedEffect(bookId) {
        viewModel.loadBook(bookId)
        loanViewModel.loadMembers()
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
                    members = membersState,
                    expandedMenu = expandedMenu,
                    setExpandedMenu = { expandedMenu = it },
                    selectedMember = selectedMember,
                    setSelectedMember = { selectedMember = it },
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
    members: LoanUiState<List<Member>>,
    expandedMenu: Boolean,
    setExpandedMenu: (Boolean) -> Unit,
    selectedMember: Member?,
    setSelectedMember: (Member?) -> Unit,
    onBorrowSuccess: (Loan) -> Unit,
    onBorrowError: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val canBorrow = !book.isOnLoan && selectedMember != null

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
                if (book.isOnLoan) {
                    Text(
                        text = "On loan to ${book.loanedToMemberName ?: "a member"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(text = "Available", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        if (members is LoanUiState.Success) {
            val memberList = members.data
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = "Borrow as", style = MaterialTheme.typography.labelLarge)
                DropdownMenu(
                    expanded = expandedMenu,
                    onDismissRequest = { setExpandedMenu(false) }
                ) {
                    for (member in memberList) {
                        DropdownMenuItem(
                            text = { Text(text = "${member.name} (${member.email})") },
                            onClick = {
                                setSelectedMember(member)
                                setExpandedMenu(false)
                            }
                        )
                    }
                }
                Button(
                    onClick = { setExpandedMenu(!expandedMenu) },
                    enabled = memberList.isNotEmpty()
                ) {
                    Text(
                        text = selectedMember?.name ?: "Select member"
                    )
                }
            }
        } else if (members is LoanUiState.Error) {
            Text(text = members.message, color = MaterialTheme.colorScheme.error)
        }

        TextButton(onClick = { onNavigateBack() }) {
            Text(text = "Back to list")
        }
        Button(
            enabled = canBorrow,
            onClick = {
                selectedMember?.let { member ->
                    viewModel.borrowBook(book.id, memberId = member.id,
                        onSuccess = { loan -> onBorrowSuccess(loan) },
                        onError = { message -> onBorrowError(message) }
                    )
                }
            }
        ) {
            Text(text = if (book.isOnLoan) "Already on loan" else "Borrow")
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
