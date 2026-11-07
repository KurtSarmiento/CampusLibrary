# DEVELOPER-DOCS

## Summary
**Asked:** Implement milestone 7 — Android borrow + my loans (per SPEC-android.md and CLAUDE.md milestones 6–7).
**Done:** Wired borrow success to navigate to My Loans, wired borrow error to a toast, added a top-bar "My loans" icon so the loans screen is directly reachable (per SPEC navigation requirement). Borrow (POST /api/loans) and My Loans (GET /api/loans) were implemented in milestone 6; milestone 7 completes the borrow flow UX.

## Projects touched
- ANDROID KOTLIN (primary)

## Files changed

| path | added/modified/deleted | why |
|---|---|---|
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/ui/NavGraph.kt | modified | Borrow success → navigate("myLoans"); added My loans icon button in top bar |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/ui/MainScreen.kt | modified | Removed unused onBorrowSuccess callback param |
| android/CampusLibraryApp/app/src/main/java/com/campuslibrary/MainActivity.kt | modified | Borrow error shows a Toast; onBorrowSuccess removed |
| android/CampusLibraryApp/app/build.gradle.kts | modified | Added material-icons-core dependency for List icon |

## Key code

**NavGraph** — borrow success navigates to myLoans; top bar has a My loans icon:
```kotlin
BookDetailScreen(
    ...
    onBorrowSuccess = { navController.navigate("myLoans") },
    ...
)
TopAppBar(
    title = { Text("CampusLibrary") },
    actions = {
        IconButton(onClick = { navController.navigate("myLoans") }) {
            Icon(imageVector = List, contentDescription = "My loans")
        }
    }
)
```

**MainActivity** — borrow error surfaced via Toast:
```kotlin
onBorrowError = { message ->
    Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
}
```

## Database changes
none (endpoints `POST /api/loans`, `POST /api/loans/{id}/return`, `GET /api/loans` already exist in WebApi; no schema change).

## How it was verified
- Reviewed all changed Android source files for consistency (ViewModels, Repository, ApiService, screens, navigation).
- `./gradlew assembleDebug` **could not be run**: the wrapper (`gradlew`, `gradlew.bat`, `gradle-wrapper.jar`) are 0-byte placeholders and Gradle is not installed on this machine — the APK must be compiled in Android Studio.
- .NET WebApi was already verified building (0 errors) with the loans endpoints in place.

## Known limits / TODO
- Android APK not compiled here (Gradle unavailable) — compile in Android Studio.
- `RetrofitClient.BASE_URL` hardcodes port 5238; confirm WebApi runs there.
- Return flow is not implemented in Android (per SPEC: returns done via WebMvc).
- My Loans shows all loans (no per-member filter) per spec assumption.
