# Spec: Data Layer

## Objective
Build the EF Core data layer for the CampusLibrary application. This includes creating the entity classes, DbContext, and initial migrations for Books, Members, and Loans.

## Tech Stack
- .NET 8
- Entity Framework Core
- SQL Server 2022 Express or LocalDB

## Commands
- Build: dotnet build src/CampusLibrary.Data
- Test: dotnet test src/CampusLibrary.Data.Tests
- Lint: dotnet format src/CampusLibrary.Data
- Dev: dotnet watch run --project src/CampusLibrary.Data

## Project Structure
```
src/CampusLibrary.Data/
├── Entities/
│   ├── Book.cs
│   ├── Member.cs
│   └── Loan.cs
├── LibraryDbContext.cs
└── Migrations/
```

## Code Style
```csharp
// Example entity class
namespace CampusLibrary.Data.Entities
{
    public class Book
    {
        public int Id { get; set; }
        [Required]
        [MaxLength(100)]
        public string Title { get; set; }
        // ...
    }
}
```

## Testing Strategy
- Unit tests for entities and DbContext
- Integration tests for migrations
- Test coverage: 80%

## Boundaries
- Always: Follow EF Core conventions, use data annotations for validation
- Ask first: Database schema changes, adding dependencies
- Never: Commit secrets, edit vendor directories, remove failing tests without approval

## Success Criteria
- All entity classes are created with proper attributes
- DbContext is configured with DbSets for Books, Members, and Loans
- Initial migration creates the database schema
- Seed data for 5 books and 3 members is included

## Open Questions
- Should we use Fluent API for additional configuration?
- What are the required fields and max lengths for each entity?
- Should we include ISBN as a unique index for Books?