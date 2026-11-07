package com.campuslibrary.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.campuslibrary.BookRepository
import com.campuslibrary.BookViewModel
import com.campuslibrary.LoanViewModel
import com.campuslibrary.ui.theme.CampusLibraryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryNavGraph(
    modifier: Modifier = Modifier,
    bookViewModel: BookViewModel,
    loanViewModel: LoanViewModel,
    repository: BookRepository,
    onBorrowError: (String) -> Unit = {}
) {
    val navController = rememberNavController()

    CampusLibraryTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("CampusLibrary") },
                    actions = {
                        IconButton(onClick = { navController.navigate("myLoans") }) {
                            Icon(imageVector = List, contentDescription = "My loans")
                        }
                    }
                )
            },
            modifier = modifier
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "bookList",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("bookList") {
                    BookListScreen(
                        viewModel = bookViewModel,
                        repository = repository,
                        onBookClick = { bookId ->
                            navController.navigate("bookDetail/$bookId")
                        },
                        onBorrowError = onBorrowError
                    )
                }
                composable(
                    route = "bookDetail/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { backStackEntry ->
                    val bookId = backStackEntry.arguments?.getInt("id") ?: 0
                    BookDetailScreen(
                        viewModel = bookViewModel,
                        bookId = bookId,
                        onBorrowSuccess = { navController.navigate("myLoans") },
                        onBorrowError = onBorrowError,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("myLoans") {
                    MyLoansScreen(
                        viewModel = loanViewModel,
                        repository = repository
                    )
                }
            }
        }
    }
}
