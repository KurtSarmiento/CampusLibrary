# How to Use — CampusLibrary UI fixes

Written for a classmate or user, not a developer.

## Prerequisites
- SQL Server (LocalDB `(localdb)\MSSQLLocalDB`) with the `CampusLibraryDb` database created and seeded (run via the project's EF Core migrations).
- The Web API running (`src/CampusLibrary.WebApi`) — note its port (default 5238).
- The Web MVC admin site running (`src/CampusLibrary.WebMvc`).
- For Android: an emulator or device on the same machine; the app calls `http://10.0.2.2:<api-port>/api/`.

## Step-by-step

### Web admin (librarian)
1. Start the Web MVC app and open its home page.
2. Use the top navigation bar — it now links to **Books**, **Members**, and **Open Loans** (previously only Home and Privacy existed, so the core features had no visible path).
3. **Books**: see each book's status — a green "Available" badge or a red "On loan to <member>" badge. Click **Create New** to add a book (ISBN/Title/Author/Year with helpful hints). Click **Edit** or **Delete** on a row; a book that is out on loan cannot be deleted.
4. **Members**: see the member list with names and emails. Click **Create New** to add a member (name and email only; the join date is set automatically).
5. **Open Loans**: see every book currently out on loan, who borrowed it, and when. Click **Return** on a row to check the book back in (it becomes available again).

### Android app (member/student)
1. Open the app — the book list shows each title, author, ISBN, year, and an **Available** / **On loan to <member>** status.
2. Tap **View** on a book to open its detail screen.
3. On the detail screen, tap **Select member** and pick a member from the dropdown (this replaces the old behavior where the borrower was hardcoded). Then tap **Borrow**. If the book is already on loan, the Borrow button shows "Already on loan" and cannot be tapped.
4. Tap the list icon in the top bar to open **My Loans**, which shows your active loans. Tap **Return** on a loan to check it back in; the list refreshes automatically.

## Example
- Web Books page row: `978-0-13-468599-1 | Clean Architecture | Robert C. Martin | 2020 | [badge: Available] | [Edit] [Delete]`
- Android book detail: title card → "Available" (green) or "On loan to Sam Rivera" (red) → "Select member" dropdown → **Borrow** button.

## Troubleshooting
| Problem | Fix |
|---------|-----|
| Web nav shows only Home/Privacy, no Books/Members/Loans | You're on a cached/older build. Rebuild and restart the Web MVC app; the links are in `Views/Shared/_Layout.cshtml`. |
| Android Borrow button disabled or says "Already on loan" | The book has an open loan. Return it first (via My Loans on Android or Open Loans on the web), then borrow again. |
| Android borrow fails / cannot reach API | Confirm the API is running and that `RetrofitClient.BASE_URL` (`http://10.0.2.2:5238/api/`) matches the API's actual port; the emulator uses `10.0.2.2` to reach the host machine. |
| `dotnet build` fails with NuGet "Value cannot be null" | Pre-existing environment issue at restore time (confirmed on a clean tree, not caused by these changes). It does not affect the Android build, which passes. |
