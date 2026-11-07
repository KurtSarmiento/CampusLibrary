# HOW-TO-USE — Android Book List + Detail (Milestone 6)

## Prerequisites
1. **SQL Server / LocalDB** running with the `CampusLibraryDb` database (migration `20261107074124_InitialCreate` applied).
2. **CampusLibrary.WebApi** running in Development mode:
   ```
   cd C:\Users\Kurt Sarmiento\CampusLibrary
   dotnet run --project src/CampusLibrary.WebApi
   ```
   Confirm it is listening on **http://localhost:5238** (the Android emulator reaches the host via `http://10.0.2.2:5238`).
3. **Android Studio** (latest stable) with an emulator (API 26+) or a physical Android device.
4. The Android source under `android/CampusLibraryApp/` opened in Android Studio.

## Step-by-step
1. Start the WebApi and note its port (launchSettings http profile = 5238).
2. Open `android/CampusLibraryApp` in Android Studio and let it sync the Gradle project.
3. Confirm `RetrofitClient.BASE_URL` matches the running API: `http://10.0.2.2:5238/api/`.
4. Run the app on the emulator/device (`app` debug configuration).
5. **Book list** opens automatically. It shows:
   - a loading indicator while fetching,
   - a list of books (title, author, ISBN, year) once loaded,
   - an empty state if no books exist,
   - an error screen with a Retry button if the API is unreachable.
6. Tap **View** on a book → **Book detail** shows title, author, ISBN, year, plus a **Borrow** button.
7. Tap **Borrow** (uses memberId = 1, hardcoded per spec):
   - success → borrow recorded; navigate to **My Loans** to see it,
   - failure → error message shown (e.g. "This book is already on loan" if a 409).
8. **My Loans** screen lists active loans with book title, member name, and borrowed date; shows loading/empty/error states and a Retry button.

## Example
API request (from the app):
```
POST http://10.0.2.2:5238/api/loans
{ "bookId": 1, "memberId": 1 }
```
Success response (201):
```
{ "id": 1, "bookId": 1, "memberId": 1, "borrowedAt": "...", "returnedAt": null,
  "book": { "id": 1, "isbn": "...", "title": "...", ... },
  "member": { "id": 1, "name": "...", ... } }
```
Already-on-loan response (409):
```
{ "message": "Book with ID 1 is already on loan" }
```

## Troubleshooting
| Problem | Fix |
|---|---|
| App shows "Cannot connect" / error screen | Make sure the WebApi is running on port 5238 and `RetrofitClient.BASE_URL` matches; the emulator uses `10.0.2.2`, not `localhost`. |
| "This book is already on loan" | The book has an open loan. Return it first via the WebApi (`POST /api/loans/{id}/return`) or WebMvc Loans page, then borrow again. |
| App won't install / Gradle sync fails | Open the project in Android Studio (Gradle wrapper is empty on this machine); ensure the Android SDK (compileSdk 34) is installed and `local.properties` points to it. |
| No books shown | Seed data may not be loaded — run the EF migration / database update, or check `GET /api/books` in Swagger returns 5 books. |
