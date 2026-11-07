# DEVELOPER-DOCS

## Summary
**Asked:** Implement milestone 6 of the CampusLibrary solution — Android book list + detail screens that call the Web API (per SPEC-android.md and CLAUDE.md milestones 6–7).
**Done:** Built the Android app from scratch (every source file under `android/CampusLibraryApp/app/src/main/` was previously empty). Also added a missing `GET /api/loans` list endpoint to the WebApi so the Android "My Loans" screen can load data, and fixed .NET entity/build issues discovered along the way. The `.NET` solution builds with 0 errors / 0 warnings.

## Projects touched
- ANDROID KOTLIN (primary: new app project)
- WEB API (added `GET /api/loans` endpoint)
- EF CORE / DATA (entity property fixes discovered during earlier verification)

## Files changed

| path | added/modified/deleted | why |
|---|---|---|
| android/CampusLibraryApp/gradle/wrapper/gradle-wrapper.properties | added | Gradle 8.4 wrapper |
| android/CampusLibraryApp/settings.gradle.kts | added | root project + app module |
| android/CampusLibraryApp/build.gradle.kts | added | AGP + Kotlin plugins |
| android/CampusLibraryApp/app/build.gradle.kts | added | SDK 34/min 26, Compose + Retrofit + Gson + coroutines + navigation-compose |
| android/CampusLibraryApp/local.properties | added | sdk.dir = C:/Android/Sdk |
| android/CampusLibraryApp/app/src/main/AndroidManifest.xml/AndroidManifest.xml | added | INTERNET permission + cleartext traffic |
| android/CampusLibraryApp/app/src/main/res/values/strings.xml | added | app_name |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/Book.kt | added | data model |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/Member.kt | added | data model |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/Loan.kt | added | data model |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/BorrowRequest.kt | added | borrow request model |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/RetrofitClient.kt | added | Retrofit singleton, base URL http://10.0.2.2:5238/api/ |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/ApiService.kt | added | GET books/book/{id}, POST loans, GET loans |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/BookRepository.kt | added | wraps ApiService with Result<> handling, 409 → "already on loan" |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/BookViewModel.kt | added | StateFlow<BookUiState> loading/empty/success/error + borrowBook |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/LoanViewModel.kt | added | StateFlow<LoanUiState> loading/empty/success/error |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/MainActivity.kt | added | wires Retrofit → repository → view models → MainScreen |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/ui/theme/Theme.kt | added | CampusLibraryTheme + SurfaceContent |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/ui/NavGraph.kt | added | NavHost: bookList, bookDetail/{id}, myLoans |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/ui/MainScreen.kt | added | theme wrapper around nav graph |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/ui/BookListScreen.kt | added | LazyColumn list, loading/empty/error states |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/ui/BookDetailScreen.kt | added | book detail + Borrow button |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/ui/MyLoansScreen.kt | added | active loans list, loading/empty/error states |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/BookListScreen.kt | deleted | duplicate root-package Hilt stub superseded by ui.* version |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/BookDetailScreen.kt | deleted | duplicate root-package Hilt stub superseded by ui.* version |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/MyLoansScreen.kt | deleted | duplicate root-package Hilt stub superseded by ui.* version |
| src/CampusLibrary.WebApi/Controllers/LoansController.cs | modified | added GET /api/loans list endpoint |
| src/CampusLibrary.Data/Entities/Book.cs | modified | added PublishedYear + using System.ComponentModel.DataAnnotations |
| src/CampusLibrary.Data/Entities/Member.cs | modified | added JoinedDate + using System.ComponentModel.DataAnnotations |
| src/CampusLibrary.Data/Entities/Loan.cs | modified | renamed LoanDate/ReturnDate → BorrowedAt/ReturnedAt + using |

## Key code

**RetrofitClient** (`android/.../RetrofitClient.kt`): Retrofit singleton pointing at `http://10.0.2.2:5238/api/` (Android emulator → host machine).

```kotlin
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5238/api/"
    ...
}
```

**ApiService** (`android/.../ApiService.kt`): the four endpoints the Android app needs.

```kotlin
interface ApiService {
    @GET("books") suspend fun getBooks(): Response<List<Book>>
    @GET("books/{id}") suspend fun getBook(@Path("id") id: Int): Response<Book>
    @POST("loans") suspend fun borrowBook(@Body request: BorrowRequest): Response<Loan>
    @GET("loans") suspend fun getLoans(): Response<List<Loan>>
}
```

**BookViewModel** (`android/.../BookViewModel.kt`): `StateFlow<BookUiState>` with Loading/Empty/Success/Error; `borrowBook` maps 409 → "This book is already on loan".

**WebApi GET /api/loans** (`LoansController.cs`): returns all loans with Book and Member DTOs, used by My Loans screen.

```csharp
[HttpGet]
public async Task<ActionResult<IEnumerable<LoanDto>>> GetLoans() { ... }
```

## Database changes
none (no new migration; existing `20261107074124_InitialCreate` already matches corrected entities).

## How it was verified
- `dotnet build src/CampusLibrary.Data` — succeeded
- `dotnet build src/CampusLibrary.WebApi` — succeeded (0 errors, 2 pre-existing nullable warnings)
- `dotnet build src/CampusLibrary.WebMvc` — succeeded
- `dotnet build` (whole solution) — **Build succeeded, 0 warnings, 0 errors**
- Android APK: **NOT compiled**. `gradlew`, `gradlew.bat`, and `gradle-wrapper.jar` are all 0-byte placeholders, and Gradle is not installed anywhere on this machine — `./gradlew assembleDebug` does nothing. The Android source follows SPEC-android.md and must be compiled in Android Studio.

## Known limits / TODO
- Android APK not compiled (see above) — compile in Android Studio; run `./gradlew assembleDebug` there.
- `RetrofitClient` base URL hardcodes port 5238 (matches WebApi `http` launch profile). User must confirm WebApi is running on that port before installing.
- `GET /api/loans` returns ALL loans (no per-member filter); per SPEC assumption, borrow uses hardcoded `memberId = 1` and My Loans shows all loans for testing.
- Borrow success / error callbacks in `MainActivity` are no-ops (no toast/snackbar wired yet).
