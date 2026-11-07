package com.campuslibrary.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuslibrary.Book
import com.campuslibrary.BookViewModel
import com.campuslibrary.BookUiState
import com.campuslibrary.Loan
import com.campuslibrary.LoanUiState
import com.campuslibrary.LoanViewModel
import com.campuslibrary.Member
import com.campuslibrary.ui.theme.CampusInk
import com.campuslibrary.ui.theme.CampusLine
import com.campuslibrary.ui.theme.CampusMuted
import com.campuslibrary.ui.theme.CampusNavy
import com.campuslibrary.ui.theme.CampusRed
import com.campuslibrary.ui.theme.CampusSurface
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
    val isBorrowing by viewModel.isBorrowing.collectAsState()
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
                    isBorrowing = isBorrowing,
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
    isBorrowing: Boolean,
    expandedMenu: Boolean,
    setExpandedMenu: (Boolean) -> Unit,
    selectedMember: Member?,
    setSelectedMember: (Member?) -> Unit,
    onBorrowSuccess: (Loan) -> Unit,
    onBorrowError: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val canBorrow = !book.isOnLoan && selectedMember != null && !isBorrowing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "CATALOGUE ENTRY",
            color = CampusRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CampusLine),
            color = CampusSurface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = CampusInk
                )
                Text(
                    text = "Author: ${book.author ?: "Unknown author"}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = CampusMuted
                )
                Text(
                    text = "ISBN: ${book.isbn}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CampusMuted
                )
                Text(
                    text = "Published Year: ${book.publishedYear}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CampusMuted
                )
                if (book.isOnLoan) {
                    Surface(color = Color(0xFFF5DFDA)) {
                        Text(
                            text = "On loan to ${book.loanedToMemberName ?: "a member"}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF87392F)
                        )
                    }
                } else {
                    Surface(color = Color(0xFFDCEFE1)) {
                        Text(
                            text = "Available on shelf",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF245C43)
                        )
                    }
                }
            }
        }

        if (members is LoanUiState.Success) {
            val memberList = members.data
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CampusLine),
                color = CampusSurface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "CIRCULATION MEMBER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = CampusMuted
                    )
                    Box {
                        Button(
                            onClick = { setExpandedMenu(!expandedMenu) },
                            enabled = memberList.isNotEmpty() && !isBorrowing,
                            colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = selectedMember?.let { "${it.name} (${it.email})" } ?: "Select member for checkout",
                                color = Color.White
                            )
                        }
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
                    }
                }
            }
        } else if (members is LoanUiState.Error) {
            Text(text = members.message, color = Color(0xFF87392F))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { onNavigateBack() },
                enabled = !isBorrowing
            ) {
                Text(text = "← Back to collection", color = CampusRed)
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
                },
                colors = ButtonDefaults.buttonColors(containerColor = CampusRed),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = when {
                        isBorrowing -> "Borrowing..."
                        book.isOnLoan -> "Already on loan"
                        else -> "Borrow title"
                    },
                    color = Color.White
                )
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
        Text(text = message, color = CampusMuted)
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
