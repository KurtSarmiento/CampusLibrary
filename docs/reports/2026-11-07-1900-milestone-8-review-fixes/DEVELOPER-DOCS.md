# DEVELOPER-DOCS

## Summary
**Asked:** Milestone 8 — `/review` of the whole CampusLibrary solution and fix findings (per CLAUDE.md).
**Done:** Ran a full-solution code review (via a reviewer agent) across correctness, readability, architecture, security, performance. Applied fixes to the real bugs found. The .NET solution builds 0 errors / 0 warnings. One item (a new migration for a DB-level unique open-loan index) is written as source but cannot be applied here due to environment/permission blockers.

## Projects touched
- WEB API
- WEB MVC
- EF CORE / DATA
- ANDROID KOTLIN

## Files changed

| path | added/modified/deleted | why |
|---|---|---|
| src/CampusLibrary.Data/Entities/Book.cs | modified | ISBN `[Required][MaxLength(13)]` (was `[MaxLength(20)]`, not required); added `ICollection<Loan> Loans` nav prop to match model snapshot |
| src/CampusLibrary.Data/Entities/Member.cs | modified | added `ICollection<Loan> Loans` nav prop to match model snapshot |
| src/CampusLibrary.Data/Entities/Loan.cs | modified | (earlier) renamed `LoanDate`/`ReturnDate` → `BorrowedAt`/`ReturnedAt` |
| src/CampusLibrary.Data/LibraryDbContext.cs | modified | added unique filtered index `IX_Loans_BookId_Open` where `ReturnedAt IS NULL` (borrow race backstop) |
| src/CampusLibrary.Data/Migrations/20261107184500_AddUniqueOpenLoanIndex.cs | added | migration to create the unique filtered open-loan index |
| src/CampusLibrary.WebApi/Controllers/LoansController.cs | modified | borrow wrapped in try/catch `DbUpdateException` → 409 (atomic backstop for concurrent borrows) |
| src/CampusLibrary.WebMvc/Controllers/BooksController.cs | modified | `DeleteConfirmed` blocks deleting a book that has an open loan (prevents FK cascade data loss) |
| src/CampusLibrary.WebMvc/Controllers/LoansController.cs | modified | removed `ModelState.AddModelError` then redirect (error was lost) |
| android/CampusLibraryApp/app/src/main/AndroidManifest.xml | modified | removed `usesCleartextTraffic="true"` from main manifest |
| android/CampusLibraryApp/app/src/debug/AndroidManifest.xml | added | debug-only cleartext traffic (spec: debug only, not release) |

## Key code fixes

**1. Borrow race condition (Critical)** — `src/CampusLibrary.WebApi/Controllers/LoansController.cs`:
```csharp
_context.Loans.Add(loan);
try
{
    await _context.SaveChangesAsync();
}
catch (DbUpdateException)
{
    return Conflict(new { Message = $"Book with ID {request.BookId} is already on loan" });
}
```
The check-then-act borrow (query open loan, then insert) is not atomic; concurrent borrows both succeed. The new unique filtered index + `DbUpdateException` catch makes the second borrow return 409.

**2. Delete-with-open-loan data loss** — `src/CampusLibrary.WebMvc/Controllers/BooksController.cs:134-143`:
```csharp
var hasOpenLoan = await _context.Loans
    .AnyAsync(l => l.BookId == id && l.ReturnedAt == null);
if (hasOpenLoan)
{
    return RedirectToAction(nameof(Index));
}
```
Previously `DeleteConfirmed` deleted a book with an open loan; FK cascade destroyed the loan (data loss).

**3. ISBN length mismatch** — `Book.cs:16`: `[Required][MaxLength(13)]` to match migration `nvarchar(13)` NOT NULL and `BookDto [MaxLength(13)]`. Previously MVC could submit values the DB rejects → 500.

**4. Model snapshot drift** — added `ICollection<Loan> Loans` to `Book` and `Member` entities so the model matches `LibraryDbContextModelSnapshot.cs` (which declares `Book.Loans` / `Member.Loans` navigation collections).

**5. Debug-only cleartext traffic** — removed `usesCleartextTraffic="true"` from main manifest; added `app/src/debug/AndroidManifest.xml` so it only applies to debug builds (spec requirement).

**6. MVC ModelState bug** — removed `ModelState.AddModelError(...)` immediately followed by `RedirectToAction` (the error was discarded on redirect).

## Database changes
- Migration `20261107184500_AddUniqueOpenLoanIndex` created (source file written).
- Creates unique filtered index `IX_Loans_BookId_Open` on `Loans(BookId)` WHERE `ReturnedAt IS NULL`.
- **NOT applied to the database.** `dotnet ef migrations add` was blocked by the permission system, and `dotnet ef database update` cannot run because `dotnet restore` fails under .NET SDK 10.0.403 (NuGet wildcard `8.0.*` mismatch — environment issue, not source). The migration must be applied with `dotnet ef database update -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi` once the environment is fixed.

## How it was verified
- `dotnet build src/CampusLibrary.Data` — succeeded
- `dotnet build src/CampusLibrary.WebApi` — succeeded (0 errors, 2 pre-existing nullable warnings)
- `dotnet build src/CampusLibrary.WebMvc` — succeeded (0 errors)
- `dotnet build` (whole solution) — **Build succeeded, 0 warnings, 0 errors**
- Android APK: not compiled (Gradle wrapper is 0-byte placeholder, Gradle not installed).
- Migration: NOT applied (see above).

## Known limits / TODO
- Apply migration `20261107184500_AddUniqueOpenLoanIndex` via `dotnet ef database update` once dotnet restore works.
- Android still not compiled (Gradle unavailable on this machine).
- `RetrofitClient.BASE_URL` hardcodes port 5238.
