# HOW-TO-USE — Milestone 8 Review Fixes

## Prerequisites
- SQL Server / LocalDB with `CampusLibraryDb` (migration `20261107074124_InitialCreate` applied).
- The new migration `20261107184500_AddUniqueOpenLoanIndex` must be applied to enforce the "at most one open loan" rule at the database level.

## Step-by-step
1. Resolve the .NET environment so `dotnet restore` works (currently fails under SDK 10.0.403 due to NuGet package version wildcards `8.0.*`).
2. Apply the new migration:
   ```
   cd C:\Users\Kurt Sarmiento\CampusLibrary
   dotnet ef database update -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
   ```
   This creates `IX_Loans_BookId_Open` (unique, where `ReturnedAt IS NULL`).
3. Rebuild the solution:
   ```
   dotnet build
   ```
   Expect: Build succeeded, 0 errors, 0 warnings.
4. Verify borrow race: start the API, borrow the same book twice concurrently (e.g. two rapid `POST /api/loans` calls with the same BookId). The second should return `409 Conflict` ("already on loan").
5. Verify delete guard: open a book that has an open loan, try to delete it from the WebMvc admin (`/Books/Delete/{id}`). The delete should be blocked and redirect to the book list.
6. Verify ISBN validation: creating/editing a book with an ISBN longer than 13 characters should fail validation (400 in API, validation message in MVC), not a DB 500.

## Example
Borrow race test (curl, two concurrent requests):
```
curl -X POST http://localhost:5238/api/loans -H "Content-Type: application/json" -d '{"bookId":1,"memberId":1}'
# First: 201 Created
# Second (rapid): 409 Conflict {"message":"Book with ID 1 is already on loan"}
```

## Troubleshooting
| Problem | Fix |
|---|---|
| `dotnet restore` fails with NuGet wildcard error | Environment issue: .NET SDK 10.0.403 doesn't resolve `8.0.*` package versions. Pin package versions in csproj or use a compatible SDK. |
| `dotnet ef` cannot create/apply migration | Same environment issue; fix `dotnet restore` first. |
| Second borrow doesn't return 409 | Confirm the unique filtered index migration was applied to the database. |
| Book deletion destroys an open loan | Confirm the delete guard in `BooksController.DeleteConfirmed` is in place and the WebMvc app was rebuilt. |
