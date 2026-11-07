package com.campuslibrary.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.campuslibrary.BookRepository
import com.campuslibrary.BookViewModel
import com.campuslibrary.LoanViewModel
import com.campuslibrary.ui.theme.CampusLibraryTheme
import com.campuslibrary.ui.theme.CampusNavy
import com.campuslibrary.ui.theme.CampusRed
import com.campuslibrary.ui.theme.CampusSurface

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
                Column {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(CampusRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "CL",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Column(modifier = Modifier.padding(start = 10.dp)) {
                                    Text(
                                        text = "Campus Library",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "KNOWLEDGE IN COMMON",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 1.2.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate("bookList") }) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Collection",
                                    tint = CampusNavy
                                )
                            }
                            IconButton(onClick = { navController.navigate("myLoans") }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.List,
                                    contentDescription = "Open loans",
                                    tint = CampusNavy
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = CampusSurface
                        )
                    )
                    // Secondary Navy Navigation Bar matching WebMvc .library-nav
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = CampusNavy
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MAIN COLLECTION · OPEN DAILY",
                                color = Color(0xFFD9E1E6),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
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
                        loanViewModel = loanViewModel,
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
