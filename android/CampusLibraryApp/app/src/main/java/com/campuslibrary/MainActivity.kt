package com.campuslibrary

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.campuslibrary.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiService = RetrofitClient.getInstance().create(ApiService::class.java)
        val repository = BookRepository(apiService)
        val bookViewModel = BookViewModel(repository)
        val loanViewModel = LoanViewModel(repository)
        setContent {
            MainScreen(
                bookViewModel = bookViewModel,
                loanViewModel = loanViewModel,
                repository = repository,
                onBorrowError = { message ->
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}
