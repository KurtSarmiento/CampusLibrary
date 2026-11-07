# Task List: Data Layer

## Entity Classes
- [ ] Task: Create Book entity class
  - Acceptance: Book.cs exists with required properties and attributes
  - Verify: dotnet build src/CampusLibrary.Data
  - Files: src/CampusLibrary.Data/Entities/Book.cs

- [ ] Task: Create Member entity class
  - Acceptance: Member.cs exists with required properties and attributes
  - Verify: dotnet build src/CampusLibrary.Data
  - Files: src/CampusLibrary.Data/Entities/Member.cs

- [ ] Task: Create Loan entity class
  - Acceptance: Loan.cs exists with required properties and attributes
  - Verify: dotnet build src/CampusLibrary.Data
  - Files: src/CampusLibrary.Data/Entities/Loan.cs

## DbContext Configuration
- [ ] Task: Configure DbContext with DbSets
  - Acceptance: LibraryDbContext.cs includes DbSet<Book>, DbSet<Member>, DbSet<Loan>
  - Verify: dotnet build src/CampusLibrary.Data
  - Files: src/CampusLibrary.Data/LibraryDbContext.cs

## Initial Migration
- [ ] Task: Create initial migration
  - Acceptance: Initial migration is created and applied
  - Verify: dotnet ef database update --project src/CampusLibrary.Data --startup-project src/CampusLibrary.WebApi
  - Files: src/CampusLibrary.Data/Migrations/

## Seed Data
- [ ] Task: Implement seed data
  - Acceptance: Seed data for 5 books and 3 members is included in the initial migration
  - Verify: Check the database for seeded data
  - Files: src/CampusLibrary.Data/Migrations/

## Testing
- [ ] Task: Write unit tests for entities
  - Acceptance: Unit tests for Book, Member, and Loan entities pass
  - Verify: dotnet test src/CampusLibrary.Data.Tests
  - Files: src/CampusLibrary.Data.Tests/

- [ ] Task: Write integration tests for migrations
  - Acceptance: Integration tests for the initial migration pass
  - Verify: dotnet test src/CampusLibrary.Data.Tests
  - Files: src/CampusLibrary.Data.Tests/

- [ ] Task: Verify test coverage
  - Acceptance: Test coverage is at least 80%
  - Verify: dotnet test src/CampusLibrary.Data.Tests --collect:"XPlat Code Coverage"
  - Files: src/CampusLibrary.Data.Tests/

## Next Steps
- Implement the tasks in the order specified
- Review and verify each task before proceeding
- Update the spec if any changes are needed