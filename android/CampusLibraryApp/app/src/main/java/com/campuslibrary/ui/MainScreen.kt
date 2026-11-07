package com.campuslibrary.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.campuslibrary.BookRepository
import com.campuslibrary.BookViewModel
import com.campuslibrary.Loan
import com.campuslibrary.LoanViewModel
import com.campuslibrary.ui.theme.CampusLibraryTheme

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    bookViewModel: BookViewModel,
    loanViewModel: LoanViewModel,
    repository: BookRepository,
    onBorrowError: (String) -> Unit = {}
) {
    CampusLibraryTheme {
        LibraryNavGraph(
            modifier = modifier,
            bookViewModel = bookViewModel,
            loanViewModel = loanViewModel,
            repository = repository,
            onBorrowError = onBorrowError
        )
    }
}
