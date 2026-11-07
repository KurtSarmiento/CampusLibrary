# How to Use: CampusLibrary Database (Milestone 2 Complete)

## Prerequisites

### Verified Working ✅
- .NET SDK 8.0 or higher (you have 10.0.403)
- SQL Server LocalDB (`(localdb)\MSSQLLocalDB`)
- Entity Framework Core CLI tools (10.0.12)
- CampusLibraryDb database created and seeded

### What's Available Now
- ✅ Database with 3 tables (Books, Members, Loans)
- ✅ 5 books in the database
- ✅ 3 members in the database
- ✅ DbContext registered in WebApi and WebMvc
- ❌ No API endpoints yet (Milestone 3)
- ❌ No MVC views yet (Milestone 5)

## Step-by-Step: Working with the Database

### 1. Verify Database Exists

**Check database with EF Core:**
```bash
dotnet ef database update -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
# Expected: "No changes were made to the database." (already up to date)
```

**List all migrations:**
```bash
dotnet ef migrations list -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
# Expected: 20261107074124_InitialCreate (Applied)
```

### 2. Query the Database

**Option A: Using SQL Server Management Studio (SSMS)**
1. Open SSMS
2. Connect to: `(localdb)\MSSQLLocalDB`
3. Expand Databases → CampusLibraryDb → Tables
4. Right-click on `dbo.Books` → Select Top 1000 Rows

**Option B: Using Azure Data Studio**
1. Open Azure Data Studio
2. New Connection:
   - Server: `(localdb)\MSSQLLocalDB`
   - Database: `CampusLibraryDb`
   - Authentication: Windows Authentication
3. Click Connect
4. New Query: `SELECT * FROM Books`

**Option C: Using sqlcmd (command line)**
```bash
# List all books
sqlcmd -S "(localdb)\MSSQLLocalDB" -d CampusLibraryDb -Q "SELECT * FROM Books"

# List all members
sqlcmd -S "(localdb)\MSSQLLocalDB" -d CampusLibraryDb -Q "SELECT * FROM Members"

# Check for open loans (should be empty)
sqlcmd -S "(localdb)\MSSQLLocalDB" -d CampusLibraryDb -Q "SELECT * FROM Loans WHERE ReturnedAt IS NULL"
```

### 3. View Seed Data

**Expected Books (5 total):**
| Id | ISBN | Title | Author | PublishedYear |
|----|------|-------|--------|---------------|
| 1 | 9780134685991 | Effective Java | Joshua Bloch | 2018 |
| 2 | 9780132350884 | Clean Code | Robert C. Martin | 2008 |
| 3 | 9780201633610 | Design Patterns | Erich Gamma | 1994 |
| 4 | 9780135957059 | The Pragmatic Programmer | David Thomas | 2019 |
| 5 | 9780137081073 | The Clean Coder | Robert C. Martin | 2011 |

**Expected Members (3 total):**
| Id | Name | Email | JoinedDate |
|----|------|-------|------------|
| 1 | John Doe | john.doe@example.com | 2024-01-15 |
| 2 | Jane Smith | jane.smith@example.com | 2024-02-20 |
| 3 | Bob Johnson | bob.johnson@example.com | 2024-03-10 |

**Expected Loans:**
- Empty (no open loans initially - all books are available)

### 4. Test the DbContext Connection

**Option A: Run the WebApi**
```bash
dotnet run --project src/CampusLibrary.WebApi
# The API should start without database errors
# Expected: "Now listening on: http://localhost:5000"
```

If the API starts successfully, the DbContext is registered and connected properly.

**Option B: Run the WebMvc**
```bash
dotnet run --project src/CampusLibrary.WebMvc
# The MVC app should start without database errors
# Expected: "Now listening on: http://localhost:5xxx"
```

### 5. Manually Test Database Operations (via SQL)

**Insert a test loan:**
```sql
-- Connect to CampusLibraryDb first
USE CampusLibraryDb;

-- Create a loan (John Doe borrows Effective Java)
INSERT INTO Loans (BookId, MemberId, BorrowedAt, ReturnedAt)
VALUES (1, 1, GETDATE(), NULL);

-- Verify the loan was created
SELECT * FROM Loans;
```

**Return the book:**
```sql
-- Mark the loan as returned
UPDATE Loans 
SET ReturnedAt = GETDATE()
WHERE Id = 1;

-- Verify the loan was updated
SELECT * FROM Loans;
```

**Check which books are available (no open loans):**
```sql
SELECT b.Id, b.Title, b.Author
FROM Books b
WHERE NOT EXISTS (
    SELECT 1 FROM Loans l 
    WHERE l.BookId = b.Id AND l.ReturnedAt IS NULL
);
```

### 6. Work with Migrations

**View current migration:**
```bash
cd src/CampusLibrary.Data/Migrations/
ls -la
# Should see: 20261107074124_InitialCreate.cs and related files
```

**Add a new migration (for future schema changes):**
```bash
# Example: If you modify an entity, create a new migration
dotnet ef migrations add <MigrationName> -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi

# Apply the new migration
dotnet ef database update -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
```

**Rollback to a previous migration (if needed):**
```bash
# List all migrations
dotnet ef migrations list -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi

# Rollback to a specific migration
dotnet ef database update <PreviousMigrationName> -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi

# Rollback all migrations (empty database, keep schema)
dotnet ef database update 0 -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
```

**Remove the last migration (only if NOT applied):**
```bash
dotnet ef migrations remove -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
```

### 7. Reset the Database (Start Fresh)

**Drop and recreate the database:**
```bash
# Drop the database (WARNING: all data will be lost)
dotnet ef database drop -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi

# Recreate and reseed
dotnet ef database update -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
```

This will:
1. Delete CampusLibraryDb
2. Create a fresh CampusLibraryDb
3. Run all migrations
4. Reseed with 5 books and 3 members

## Example: What You Can Do Now

### Query Books with EF Core (C# code example)

Once Milestone 3 is complete, you'll be able to create controllers that use the DbContext like this:

```csharp
public class BooksController : ControllerBase
{
    private readonly LibraryDbContext _context;

    public BooksController(LibraryDbContext context)
    {
        _context = context;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<Book>>> GetBooks()
    {
        return await _context.Books.ToListAsync();
    }
}
```

But for now, the database exists and can be queried via SQL tools.

### Test the Loan Business Rule

**Rule: A book can only have one open loan at a time**

```sql
-- Try to create two open loans for the same book (should violate business rule)
INSERT INTO Loans (BookId, MemberId, BorrowedAt, ReturnedAt)
VALUES (1, 1, GETDATE(), NULL);

INSERT INTO Loans (BookId, MemberId, BorrowedAt, ReturnedAt)
VALUES (1, 2, GETDATE(), NULL);  -- This is allowed at DB level!

-- The database allows it, so the API must enforce the rule in Milestone 4
SELECT * FROM Loans WHERE BookId = 1 AND ReturnedAt IS NULL;
```

**Note:** The database schema does NOT enforce the "one open loan per book" rule. The API controllers (Milestone 4) will need to check this before allowing a borrow operation.

## Troubleshooting

### Issue: "A network-related error occurred"

**Symptom:**
```
A network-related or instance-specific error occurred while establishing a connection to SQL Server.
The server was not found or was not accessible.
```

**Solution:**
1. Verify LocalDB is installed:
   ```bash
   sqllocaldb info
   # Should list: MSSQLLocalDB
   ```

2. Start LocalDB if not running:
   ```bash
   sqllocaldb start MSSQLLocalDB
   ```

3. Check connection string in `appsettings.Development.json`:
   ```json
   "Server=(localdb)\\MSSQLLocalDB;Database=CampusLibraryDb;Trusted_Connection=True;TrustServerCertificate=True;"
   ```

### Issue: "Cannot open database CampusLibraryDb"

**Symptom:**
```
Cannot open database "CampusLibraryDb" requested by the login. The login failed.
```

**Solution:**
The database was never created. Run:
```bash
dotnet ef database update -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
```

### Issue: "No migrations were found"

**Symptom:**
```
No migrations were found with id '20261107074124_InitialCreate'.
```

**Solution:**
The migration files might be missing. Check:
```bash
ls src/CampusLibrary.Data/Migrations/
# Should contain: 20261107074124_InitialCreate.cs
```

If missing, recreate the migration:
```bash
dotnet ef migrations add InitialCreate -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
```

### Issue: "The seed entity for entity type 'Book' cannot be added"

**Symptom:**
Error during migration about duplicate seed data.

**Solution:**
This happens if you run `migrations add` twice. Remove the duplicate migration:
```bash
dotnet ef migrations remove -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
```

Then recreate with a new name:
```bash
dotnet ef migrations add RecreatedInitialSchema -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
```

### Issue: "The INSERT statement conflicted with the UNIQUE constraint"

**Symptom:**
Error when trying to insert data manually.

**Solution:**
You're trying to insert a Book with a duplicate ISBN or a Member with a duplicate Email. Both fields have unique indexes. Check existing data:
```sql
SELECT ISBN FROM Books;
SELECT Email FROM Members;
```

## Next Steps (Milestone 3)

Once you're ready for Milestone 3:

1. **Create DTOs** in `src/CampusLibrary.WebApi/DTOs/`
   - `BookDto.cs` - for request/response
   - `MemberDto.cs` - for request/response
   - `LoanDto.cs` - for response
   - `BorrowRequestDto.cs` - for borrow requests

2. **Create BooksController** in `src/CampusLibrary.WebApi/Controllers/`
   - GET /api/books (list all)
   - GET /api/books/{id} (get one)
   - POST /api/books (create)
   - PUT /api/books/{id} (update)
   - DELETE /api/books/{id} (delete)

3. **Test via Swagger**
   - Start the API: `dotnet run --project src/CampusLibrary.WebApi`
   - Open: `https://localhost:5001/swagger`
   - Test each endpoint

See CLAUDE.md section 6 for the full milestone sequence.
