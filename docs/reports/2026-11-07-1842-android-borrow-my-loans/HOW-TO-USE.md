# HOW-TO-USE — Android Borrow + My Loans (Milestone 7)

## Prerequisites
1. **CampusLibrary.WebApi** running in Development mode on `http://localhost:5238`:
   ```
   cd C:\Users\Kurt Sarmiento\CampusLibrary
   dotnet run --project src/CampusLibrary.WebApi
   ```
2. SQL Server / LocalDB with `CampusLibraryDb` (migration applied, 5 books and 3 members seeded).
3. Android Studio with the `android/CampusLibraryApp` project synced, emulator (API 26+) or device.

## Step-by-step
1. Start the WebApi and open Swagger at `http://localhost:5238/swagger`.
2. Confirm at least one book is available (`GET /api/books` returns books; none should have an open loan).
3. Run the app on the emulator/device from Android Studio.
4. **Book list**: shows all books. Tap **View** on a book.
5. **Book detail**: shows title, author, ISBN, year. Tap **Borrow** (uses memberId = 1, hardcoded):
   - Success → app navigates to **My Loans** screen; the borrowed book appears there.
   - Already on loan → a toast shows "This book is already on loan".
   - Other/network error → a toast shows the error message.
6. **My Loans**: reachable from the top-bar icon at any time, or automatically after a successful borrow. Shows active loans with book title, member name, and borrowed date. Loading/empty/error states shown; Retry button reloads.
7. To return a book, use the WebApi (`POST /api/loans/{id}/return`) or the WebMvc admin site — returns are not done from the Android app per spec.

## Example
Borrow request:
```
POST http://10.0.2.2:5238/api/loans
{ "bookId": 1, "memberId": 1 }
```
Success (201) → app navigates to My Loans.
Already on loan (409) → toast: "This book is already on loan".

## Troubleshooting
| Problem | Fix |
|---|---|
| Borrow shows toast error | Confirm WebApi is running on port 5238 and `RetrofitClient.BASE_URL` matches (`http://10.0.2.2:5238/api/`). |
| "This book is already on loan" | Return the book via WebApi or WebMvc first, then borrow again. |
| My Loans icon does nothing | Ensure the app compiled with the material-icons-core dependency; rebuild in Android Studio. |
