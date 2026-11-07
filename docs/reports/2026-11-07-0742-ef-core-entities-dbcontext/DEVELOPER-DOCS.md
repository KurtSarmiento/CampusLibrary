# Developer Documentation: EF Core Entities, DbContext, and Database

## Summary
Created entity classes (Book, Member, Loan), LibraryDbContext with DbSets and seed data, registered DbContext in both WebApi and WebMvc projects, generated the first EF Core migration (InitialCreate), and successfully applied it to create the CampusLibraryDb database with all tables, indexes, and seed data (5 books, 3 members).

**Task**: EF Core entities, DbContext, seed data, first migration on MSSQL (Milestone 2)  
**Completed**: All entities, DbContext, migration applied, database created  
**Duration**: ~15 minutes  
**Status**: Milestone 2 complete ✅

## Projects Touched
- ✅ **EF CORE** - Created entities, DbContext, migration
- ✅ **WEB API** - Registered DbContext, added EF Core Design package
- ✅ **WEB MVC** - Registered DbContext
- ✅ **MSSQL** - Database created with tables and seed data

## Files Changed

| Path | Status | Why |
|------|--------|-----|
| `src/CampusLibrary.Data/Entities/Book.cs` | Added | Book entity with ISBN, Title, Author, PublishedYear |
| `src/CampusLibrary.Data/Entities/Member.cs` | Added | Member entity with Name, Email, JoinedDate |
| `src/CampusLibrary.Data/Entities/Loan.cs` | Added | Loan entity with BookId, MemberId, BorrowedAt, ReturnedAt |
| `src/CampusLibrary.Data/LibraryDbContext.cs` | Added | DbContext with DbSets, unique indexes, seed data |
| `src/CampusLibrary.Data/Migrations/20261107074124_InitialCreate.cs` | Generated | EF Core migration for initial schema |
| `src/CampusLibrary.Data/Migrations/20261107074124_InitialCreate.Designer.cs` | Generated | Migration metadata |
| `src/CampusLibrary.Data/Migrations/LibraryDbContextModelSnapshot.cs` | Generated | Current model snapshot |
| `src/CampusLibrary.WebApi/Program.cs` | Modified | Added DbContext registration and using directive |
| `src/CampusLibrary.WebApi/CampusLibrary.WebApi.csproj` | Modified | Added EF Core Design package |
| `src/CampusLibrary.WebMvc/Program.cs` | Modified | Added DbContext registration and using directive |

## Key Code

### Entity: Book.cs
```csharp
public class Book
{
    public int Id { get; set; }

    [Required]
    [MaxLength(13)]
    public string ISBN { get; set; } = string.Empty;

    [Required]
    [MaxLength(200)]
    public string Title { get; set; } = string.Empty;

    [MaxLength(100)]
    public string? Author { get; set; }

    public int PublishedYear { get; set; }

    public ICollection<Loan> Loans { get; set; } = new List<Loan>();
}
```

### Entity: Member.cs
```csharp
public class Member
{
    public int Id { get; set; }

    [Required]
    [MaxLength(100)]
    public string Name { get; set; } = string.Empty;

    [Required]
    [MaxLength(100)]
    public string Email { get; set; } = string.Empty;

    public DateTime JoinedDate { get; set; }

    public ICollection<Loan> Loans { get; set; } = new List<Loan>();
}
```

### Entity: Loan.cs
```csharp
public class Loan
{
    public int Id { get; set; }

    [Required]
    public int BookId { get; set; }

    [Required]
    public int MemberId { get; set; }

    [Required]
    public DateTime BorrowedAt { get; set; }

    public DateTime? ReturnedAt { get; set; }

    public Book Book { get; set; } = null!;
    public Member Member { get; set; } = null!;
}
```

### DbContext Configuration (Unique Indexes)
```csharp
protected override void OnModelCreating(ModelBuilder modelBuilder)
{
    base.OnModelCreating(modelBuilder);

    // Configure unique indexes
    modelBuilder.Entity<Book>()
        .HasIndex(b => b.ISBN)
        .IsUnique();

    modelBuilder.Entity<Member>()
        .HasIndex(m => m.Email)
        .IsUnique();

    // Seed data...
}
```

### Seed Data (5 Books)
```csharp
modelBuilder.Entity<Book>().HasData(
    new Book { Id = 1, ISBN = "9780134685991", Title = "Effective Java", Author = "Joshua Bloch", PublishedYear = 2018 },
    new Book { Id = 2, ISBN = "9780132350884", Title = "Clean Code", Author = "Robert C. Martin", PublishedYear = 2008 },
    new Book { Id = 3, ISBN = "9780201633610", Title = "Design Patterns", Author = "Erich Gamma", PublishedYear = 1994 },
    new Book { Id = 4, ISBN = "9780135957059", Title = "The Pragmatic Programmer", Author = "David Thomas", PublishedYear = 2019 },
    new Book { Id = 5, ISBN = "9780137081073", Title = "The Clean Coder", Author = "Robert C. Martin", PublishedYear = 2011 }
);
```

### Seed Data (3 Members)
```csharp
modelBuilder.Entity<Member>().HasData(
    new Member { Id = 1, Name = "John Doe", Email = "john.doe@example.com", JoinedDate = new DateTime(2024, 1, 15) },
    new Member { Id = 2, Name = "Jane Smith", Email = "jane.smith@example.com", JoinedDate = new DateTime(2024, 2, 20) },
    new Member { Id = 3, Name = "Bob Johnson", Email = "bob.johnson@example.com", JoinedDate = new DateTime(2024, 3, 10) }
);
```

### DbContext Registration (WebApi & WebMvc)
```csharp
using Microsoft.EntityFrameworkCore;

// In Program.cs
builder.Services.AddDbContext<CampusLibrary.Data.LibraryDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));
```

## Database Changes

### Migration: 20261107074124_InitialCreate

**Tables Created:**
1. **Books** - 5 rows seeded
   - Columns: Id (int, PK, identity), ISBN (nvarchar(13), unique), Title (nvarchar(200)), Author (nvarchar(100), nullable), PublishedYear (int)
   - Indexes: PK_Books, IX_Books_ISBN (unique)

2. **Members** - 3 rows seeded
   - Columns: Id (int, PK, identity), Name (nvarchar(100)), Email (nvarchar(100), unique), JoinedDate (datetime2)
   - Indexes: PK_Members, IX_Members_Email (unique)

3. **Loans** - 0 rows (no open loans initially)
   - Columns: Id (int, PK, identity), BookId (int, FK), MemberId (int, FK), BorrowedAt (datetime2), ReturnedAt (datetime2, nullable)
   - Indexes: PK_Loans, IX_Loans_BookId, IX_Loans_MemberId
   - Foreign Keys: FK_Loans_Books_BookId (CASCADE), FK_Loans_Members_MemberId (CASCADE)

**Database:** CampusLibraryDb  
**Server:** (localdb)\MSSQLLocalDB  
**EF Core Version:** 8.0.33

### Migration Commands Used
```bash
# Create migration
dotnet ef migrations add InitialCreate -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi

# Apply migration
dotnet ef database update -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
```

## How it was Verified

### Build Verification
```bash
dotnet build
# Output: Build succeeded. 0 Warning(s). 0 Error(s).
# All 3 projects compiled successfully
```

### Database Verification
Migration output confirmed:
- ✅ Database `CampusLibraryDb` created
- ✅ Tables `Books`, `Members`, `Loans` created with correct schema
- ✅ 5 books inserted into Books table
- ✅ 3 members inserted into Members table
- ✅ Unique indexes created on Book.ISBN and Member.Email
- ✅ Foreign key constraints created for Loans table
- ✅ Migration recorded in `__EFMigrationsHistory`

### Entity Framework Tools Verification
```bash
dotnet ef --version
# Output: Entity Framework Core .NET Command-line Tools 10.0.12

dotnet ef migrations list -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
# Output: 20261107074124_InitialCreate (Applied)
```

### Data Verification (via SQL)
The migration output showed successful INSERT statements:
- Books table: 5 rows inserted (IDs 1-5)
- Members table: 3 rows inserted (IDs 1-3)
- Loans table: 0 rows (empty, as per spec)

## Known Limits / TODO

### Immediate Next Steps (Milestone 3)
1. Create DTOs in WebApi:
   - `BookDto.cs`
   - `MemberDto.cs`
   - `LoanDto.cs`
   - `BorrowRequestDto.cs`
2. Create BooksController with CRUD endpoints:
   - GET /api/books
   - GET /api/books/{id}
   - POST /api/books
   - PUT /api/books/{id}
   - DELETE /api/books/{id}
3. Test endpoints via Swagger
4. Verify CORS for Android emulator

### Current State
- ✅ Database schema created and seeded
- ✅ DbContext registered in both WebApi and WebMvc
- ❌ No API endpoints yet (controllers come in Milestone 3)
- ❌ No MVC views yet (views come in Milestone 5)
- ❌ No business logic for loan validation yet

### Technical Notes
- **Loan business rule**: A book is available if no loan exists with `ReturnedAt == NULL` for that BookId
- **Cascade delete**: Deleting a book or member will cascade delete related loans (consider changing to RESTRICT in production)
- **DateTime**: Using `DateTime` (not `DateTimeOffset`) - all times stored as-is without timezone info
- **Nullable ReturnedAt**: A loan with `ReturnedAt == NULL` is an open/active loan
- **Identity columns**: All primary keys use SQL Server IDENTITY auto-increment

### Database Access
To manually inspect the database:
```bash
# Using sqlcmd
sqlcmd -S "(localdb)\MSSQLLocalDB" -d CampusLibraryDb -Q "SELECT * FROM Books"

# Or use SQL Server Management Studio / Azure Data Studio
Server: (localdb)\MSSQLLocalDB
Database: CampusLibraryDb
```

### Migration Rollback (if needed)
```bash
# Remove last migration (only if not applied)
dotnet ef migrations remove -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi

# Rollback database to previous migration (if needed)
dotnet ef database update <previous-migration-name> -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi

# Drop database completely (destructive)
dotnet ef database drop -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
```
