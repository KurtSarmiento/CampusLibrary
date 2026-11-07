# Spec: CampusLibrary Android App

## Objective

Build an Android mobile app for library members to browse books, view book details, borrow books, and view their active loans. The app communicates with `CampusLibrary.WebApi` over HTTP using Retrofit. It uses Jetpack Compose for UI, follows MVVM architecture (ViewModel + StateFlow), and handles loading/empty/error states on every screen.

**Users**: Library members (students, faculty) using Android phones.

**Success**: The app runs on Android emulator (API 26+), displays book list from the API, allows borrowing a book, and shows the user's active loans.

## Tech Stack

- Android Studio (latest stable)
- Kotlin
- Jetpack Compose (declarative UI)
- Min SDK: 26 (Android 8.0)
- Target SDK: 34 (Android 14)
- Libraries:
  - `androidx.lifecycle:lifecycle-viewmodel-compose` (ViewModel)
  - `androidx.navigation:navigation-compose` (Navigation)
  - `com.squareup.retrofit2:retrofit:2.9.0` (HTTP client)
  - `com.squareup.retrofit2:converter-gson:2.9.0` (JSON parsing)
  - `org.jetbrains.kotlinx:kotlinx-coroutines-android` (async)
  - `androidx.compose.material3:material3` (Material Design 3)
- API base URL: `http://10.0.2.2:<port>/api/` (emulator -> host machine)

## Commands

```bash
# Build debug APK (from android/CampusLibraryApp)
./gradlew assembleDebug

# Install on emulator/device
./gradlew installDebug

# Run on emulator/device
./gradlew installDebug
adb shell am start -n com.example.campuslibraryapp/.MainActivity

# Run tests
./gradlew test
```

## Project Structure

```
android/CampusLibraryApp/
├── app/
│   ├── build.gradle.kts                → Dependencies, SDK versions
│   ├── src/main/
│   │   ├── AndroidManifest.xml         → INTERNET permission, cleartext traffic
│   │   ├── java/com/example/campuslibraryapp/
│   │   │   ├── MainActivity.kt         → Entry point, sets content
│   │   │   ├── data/
│   │   │   │   ├── api/
│   │   │   │   │   ├── ApiService.kt   → Retrofit interface (books, loans endpoints)
│   │   │   │   │   └── RetrofitClient.kt → Retrofit singleton
│   │   │   │   ├── model/
│   │   │   │   │   ├── Book.kt         → Data class
│   │   │   │   │   ├── Loan.kt         → Data class
│   │   │   │   │   └── BorrowRequest.kt
│   │   │   │   └── repository/
│   │   │   │       └── LibraryRepository.kt → Wraps ApiService
│   │   │   ├── ui/
│   │   │   │   ├── screens/
│   │   │   │   │   ├── BookListScreen.kt    → Book list with search
│   │   │   │   │   ├── BookDetailScreen.kt  → Book detail + Borrow button
│   │   │   │   │   └── MyLoansScreen.kt     → User's active loans
│   │   │   │   ├── viewmodel/
│   │   │   │   │   ├── BookViewModel.kt     → StateFlow for books
│   │   │   │   │   └── LoanViewModel.kt     → StateFlow for loans
│   │   │   │   └── navigation/
│   │   │   │       └── NavGraph.kt          → Compose navigation setup
│   │   │   └── ui/theme/
│   │   │       └── Theme.kt                 → Material3 theme
│   │   └── res/                             → Icons, strings, colors
│   └── build/                               → Build outputs (gitignored)
├── gradle/
├── build.gradle.kts                         → Project-level Gradle
└── settings.gradle.kts
```

## Code Style

ViewModel example showing StateFlow, coroutines, and state management:

```kotlin
class BookViewModel(private val repository: LibraryRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Book>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Book>>> = _uiState.asStateFlow()

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val books = repository.getBooks()
                _uiState.value = if (books.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(books)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    object Empty : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

Compose screen example showing state handling:

```kotlin
@Composable
fun BookListScreen(
    viewModel: BookViewModel,
    onBookClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Empty -> EmptyState("No books available")
        is UiState.Error -> ErrorState(state.message) { viewModel.loadBooks() }
        is UiState.Success -> {
            LazyColumn {
                items(state.data) { book ->
                    BookListItem(book, onClick = { onBookClick(book.id) })
                }
            }
        }
    }
}
```

**Conventions**:
- PascalCase for classes, camelCase for functions/properties
- Sealed classes for UI state (Loading/Empty/Success/Error)
- `StateFlow` for observable state, `MutableStateFlow` private in ViewModels
- `viewModelScope.launch` for coroutines in ViewModels
- Retrofit suspend functions (no callbacks)
- Repository pattern wraps API calls
- Compose navigation with NavHost
- Material3 components (Button, Card, LazyColumn, TopAppBar)
- Handle all three states: loading, empty, error

## Testing Strategy

**Unit tests**: Not included in initial milestone (focus on manual verification).

**Integration verification**:
1. Build succeeds: `./gradlew assembleDebug`
2. WebApi must be running: `dotnet run --project src/CampusLibrary.WebApi`
3. Get API port from WebApi console output
4. Update `RetrofitClient.kt` base URL to `http://10.0.2.2:<port>/api/`
5. Install on emulator: `./gradlew installDebug`
6. Manual app tests:
   - Book list screen loads and shows books from API
   - Tap a book → detail screen shows title, author, ISBN, year
   - Tap "Borrow" button → success toast, book added to loans (hardcoded memberId for now)
   - Navigate to "My Loans" → shows active loans (can filter by memberId or show all for testing)
   - Error states: Stop API, app shows "Cannot connect" message with retry button
   - Empty state: If API returns empty array, show "No books available"

## Boundaries

**Always do**:
- Handle Loading/Empty/Error states on every screen
- Use `viewModelScope` for coroutines (auto-cancelled on ViewModel clear)
- Wrap API calls in try/catch, map exceptions to UiState.Error
- Declare `INTERNET` permission in manifest
- Use `usesCleartextTraffic="true"` only in debug build (not release)
- Run `./gradlew assembleDebug` after every change

**Ask first**:
- Adding user authentication (login, JWT tokens)
- Adding member selection (currently hardcoded memberId for borrow)
- Adding search/filter functionality beyond basic list
- Adding image loading (Coil library, book covers)
- Adding local database (Room for offline support)

**Never do**:
- Hard-code API URLs in production builds (use BuildConfig or local.properties)
- Use `runBlocking` on main thread (causes ANR)
- Ignore loading/error states (always handle all three)
- Skip `INTERNET` permission (app will crash)
- Commit API keys or secrets

## Success Criteria

- [ ] `./gradlew assembleDebug` builds without errors
- [ ] App installs and runs on emulator (API 26+)
- [ ] `INTERNET` permission declared in manifest
- [ ] `usesCleartextTraffic` enabled for debug build (allows http://)
- [ ] Screens implemented:
  - **BookListScreen**: LazyColumn of books, shows loading/empty/error states
  - **BookDetailScreen**: Displays book details (title, author, ISBN, year), "Borrow" button
  - **MyLoansScreen**: LazyColumn of user's active loans (book title, borrowed date)
- [ ] API integration:
  - `ApiService` with endpoints: `getBooks()`, `getBook(id)`, `borrowBook(request)`, `getLoans()`
  - `RetrofitClient` configured with base URL `http://10.0.2.2:<port>/api/`
  - `LibraryRepository` wraps API calls, used by ViewModels
- [ ] MVVM architecture:
  - `BookViewModel` manages book list + detail state
  - `LoanViewModel` manages loan list + borrow action state
  - All ViewModels use `StateFlow<UiState<T>>`
- [ ] Navigation:
  - NavHost with 3 destinations: bookList, bookDetail/{id}, myLoans
  - Bottom navigation or drawer with links to Book List and My Loans
- [ ] Borrow flow works:
  - Tap "Borrow" on BookDetailScreen
  - POST to `/api/loans` with `{ "bookId": x, "memberId": 1 }` (hardcoded member for now)
  - Show success message or error (409 if already borrowed)

## Open Questions

**ASSUMPTIONS I'M MAKING:**

1. **Hardcoded memberId**: Borrow button uses `memberId = 1` (no login screen in this milestone)
2. **My Loans filter**: Either hardcode memberId=1 filter or show all loans (API doesn't have per-member endpoint yet)
3. **API port**: Must be manually configured in `RetrofitClient.kt` after starting WebApi
4. **No offline support**: App requires network, no local caching (Room can be added later)
5. **No images**: Book covers not included (can add Coil library later if needed)
6. **No search**: Book list shows all books, no search/filter UI
7. **Material3**: Use Material Design 3 (not Material 2)
8. **Package name**: `com.example.campuslibraryapp` (student can rename if needed)
9. **Return flow**: Not implemented in Android app (librarians do returns via WebMvc)

→ **Correct me now or I'll proceed with these assumptions.**
