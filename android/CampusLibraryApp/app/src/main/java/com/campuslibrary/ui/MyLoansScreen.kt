package com.campuslibrary.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuslibrary.BookRepository
import com.campuslibrary.Loan
import com.campuslibrary.LoanUiState
import com.campuslibrary.LoanViewModel
import com.campuslibrary.ui.theme.CampusInk
import com.campuslibrary.ui.theme.CampusLine
import com.campuslibrary.ui.theme.CampusMuted
import com.campuslibrary.ui.theme.CampusRed
import com.campuslibrary.ui.theme.CampusSurface
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
            is LoanUiState.Empty -> EmptyScreen("No open loans. Every title is back on the shelf.")
            is LoanUiState.Error -> ErrorScreen(state.message) { viewModel.loadLoans() }
            is LoanUiState.Success -> {
                LoansContent(loans = state.data, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun LoansContent(loans: List<Loan>, viewModel: LoanViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = "CIRCULATION DESK",
                    color = CampusRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Open loans",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = CampusInk
                    )
                    Text(
                        text = "${loans.size} currently out",
                        fontSize = 13.sp,
                        color = CampusMuted,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }
        }
        items(loans, key = { it.id }) { loan ->
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
                            text = "${loan.book.title} (${loan.book.isbn})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CampusInk
                        )
                        Text(
                            text = "Member: ${loan.member.name} (${loan.member.email})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CampusMuted
                        )
                        Text(
                            text = "Borrowed: ${loan.borrowedAt}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CampusMuted
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.returnLoan(loan.id,
                                onSuccess = { viewModel.loadLoans() },
                                onError = { message -> /* handled silently or via snackbar */ }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CampusRed),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
                    ) {
                        Text(text = "Return", color = Color.White)
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
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CIRCULATION DESK",
            color = CampusRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = message,
            color = CampusMuted,
            fontWeight = FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
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
