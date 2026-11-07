# Flowcharts: EF Core Entities, DbContext, and Database

## 1. Entity Creation Flow

```mermaid
flowchart TD
    Start([Start: Milestone 2]) --> CreateEntities[Create Entity Classes
Book.cs, Member.cs, Loan.cs]
    CreateEntities --> AddDataAnnotations[Add Data Annotations
[Required], [MaxLength], etc.]
    AddDataAnnotations --> AddNavProps[Add Navigation Properties
ICollection<Loan>]
    AddNavProps --> CreateDbContext[Create LibraryDbContext
with DbSet<T> properties]
    CreateDbContext --> ConfigIndexes[Configure Unique Indexes
for ISBN and Email]
    ConfigIndexes --> AddSeedData[Add Seed Data
5 books, 3 members]
    AddSeedData --> BuildData{Build Data Project
Success?}
    
    BuildData -->|No| FixDataErrors[Fix Build Errors]
    FixDataErrors --> BuildData
    
    BuildData -->|Yes| RegisterDbContext[Register DbContext in
WebApi & WebMvc
Program.cs]
    RegisterDbContext --> AddUsing[Add using Microsoft.EntityFrameworkCore]
    AddUsing --> AddDesignPackage[Add EF Core Design
to WebApi project]
    AddDesignPackage --> CreateMigration[Create Migration
InitialCreate]
    CreateMigration --> ApplyMigration[Apply Migration
to Create Database]
    ApplyMigration --> VerifySchema{Database Schema
Created Correctly?}
    
    VerifySchema -->|No| FixSchema[Fix Schema Issues]
    FixSchema --> ApplyMigration
    
    VerifySchema -->|Yes| Complete([Milestone 2 Complete:<br/>Database Created with Schema<br/>and Seed Data])
    
    style CreateEntities fill:#e1f5ff
    style CreateDbContext fill:#e1f5ff
    style ConfigIndexes fill:#fff3e0
    style AddSeedData fill:#fff3e0
    style RegisterDbContext fill:#fff9c4
    style CreateMigration fill:#fff9c4
    style ApplyMigration fill:#fff9c4
    style Complete fill:#c8e6c9
```

## 2. Migration Creation Sequence

```mermaid
sequenceDiagram
    participant Dev as Developer
    participant CLI as dotnet CLI
    participant Data as CampusLibrary.Data
    participant WebApi as CampusLibrary.WebApi
    participant SQL as SQL Server
    
    Dev->>CLI: dotnet ef migrations add InitialCreate -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
    CLI->>Data: Check for pending changes
    Data-->>CLI: Changes detected (new entities)
    CLI->>Data: Generate migration code
    Data->>Data: Create 20261107074124_InitialCreate.cs
    Data->>Data: Create 20261107074124_InitialCreate.Designer.cs
    Data->>Data: Create LibraryDbContextModelSnapshot.cs
    Data-->>CLI: Migration files created
    CLI-->>Dev: Migration created successfully
    
    Dev->>CLI: dotnet ef database update -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi
    CLI->>WebApi: Check connection string
    WebApi-->>CLI: Connection string found
    CLI->>SQL: Connect to (localdb)\MSSQLLocalDB
    SQL-->>CLI: Connection established
    CLI->>SQL: CREATE DATABASE CampusLibraryDb
    SQL-->>CLI: Database created
    CLI->>SQL: Execute migration SQL
    SQL->>SQL: CREATE TABLE Books
    SQL->>SQL: CREATE TABLE Members
    SQL->>SQL: CREATE TABLE Loans
    SQL->>SQL: CREATE UNIQUE INDEX IX_Books_ISBN
    SQL->>SQL: CREATE UNIQUE INDEX IX_Members_Email
    SQL->>SQL: INSERT seed data
    SQL-->>CLI: Migration applied
    CLI-->>Dev: Database update completed
```

## 3. Database Schema Flow

```mermaid
flowchart TD
    Start([Database Schema]) --> Books[Books Table
int Id (PK)
string ISBN (UK)
string Title
string Author
int PublishedYear] --> Members[Members Table
int Id (PK)
string Name
string Email (UK)
datetime JoinedDate]

    Books --> Loans[Loans Table
int Id (PK)
int BookId (FK)
int MemberId (FK)
datetime BorrowedAt
datetime ReturnedAt] --> Members
    
    Loans -.->|FK Constraint| Books
    Loans -.->|FK Constraint| Members
    
    style Books fill:#bbdefb
    style Members fill:#fff59d
    style Loans fill:#c5e1a5
```

## 4. Loan Business Rule Flow

```mermaid
flowchart TD
    Start([Borrow Book Request]) --> CheckAvailability{Book Available?
(No open loans)} -->|Yes| CreateLoan[Create Loan
Set BorrowedAt = Now
ReturnedAt = NULL]
    
    CheckAvailability -->|No| Reject[Reject Request
Return 409 Conflict]
    
    CreateLoan --> SaveLoan[Save Loan to Database]
    SaveLoan --> Success([Book Borrowed Successfully])
    
    ReturnBook([Return Book Request]) --> UpdateLoan[Update Loan
Set ReturnedAt = Now]
    UpdateLoan --> SaveReturn[Save Updated Loan]
    SaveReturn --> Returned([Book Returned Successfully])
    
    style CheckAvailability fill:#fff3e0
    style CreateLoan fill:#fff9c4
    style Success fill:#c8e6c9
    style Returned fill:#c8e6c9
```

## 5. DbContext Registration Flow

```mermaid
flowchart TD
    Start([Register DbContext]) --> OpenProgram[Open Program.cs
in WebApi and WebMvc] --> AddUsing[Add using Microsoft.EntityFrameworkCore]
    AddUsing --> AddService[Add DbContext Service:
builder.Services.AddDbContext]
    AddService --> ConfigConnStr[Configure Connection String
from appsettings.Development.json]
    ConfigConnStr --> BuildCheck{Build
Success?}
    
    BuildCheck -->|No| FixBuild[Fix Build Errors
Check using directive
Verify connection string]
    FixBuild --> BuildCheck
    
    BuildCheck -->|Yes| RunCheck{API/MVC
Starts?}
    
    RunCheck -->|No| FixRun[Check DbContext
Registration
Verify connection string]
    FixRun --> RunCheck
    
    RunCheck -->|Yes| Success([DbContext Registered
Successfully])
    
    style AddUsing fill:#fff9c4
    style AddService fill:#fff9c4
    style ConfigConnStr fill:#fff3e0
    style Success fill:#c8e6c9
```

## 6. Seed Data Insertion Flow

```mermaid
flowchart TD
    Start([Seed Data Insertion]) --> BooksData[5 Books
Effective Java, Clean Code,
Design Patterns,
The Pragmatic Programmer,
The Clean Coder] --> MembersData[3 Members
John Doe, Jane Smith,
Bob Johnson]
    
    BooksData --> InsertBooks[INSERT INTO Books
VALUES (1, '9780134685991', 'Effective Java', 'Joshua Bloch', 2018)
...
VALUES (5, '9780137081073', 'The Clean Coder', 'Robert C. Martin', 2011)]
    
    MembersData --> InsertMembers[INSERT INTO Members
VALUES (1, 'John Doe', 'john.doe@example.com', '2024-01-15')
...
VALUES (3, 'Bob Johnson', 'bob.johnson@example.com', '2024-03-10')]
    
    InsertBooks --> VerifyBooks[SELECT COUNT(*) FROM Books
Should return 5]
    InsertMembers --> VerifyMembers[SELECT COUNT(*) FROM Members
Should return 3]
    
    VerifyBooks -->|5| SuccessBooks([Books Seeded Successfully])
    VerifyMembers -->|3| SuccessMembers([Members Seeded Successfully])
    
    style BooksData fill:#bbdefb
    style MembersData fill:#fff59d
    style InsertBooks fill:#fff9c4
    style InsertMembers fill:#fff9c4
    style SuccessBooks fill:#c8e6c9
    style SuccessMembers fill:#c8e6c9
```

## 7. Database Verification Flow

```mermaid
flowchart TD
    Start([Verify Database]) --> Connect[Connect to
(localdb)\MSSQLLocalDB] --> UseDB[USE CampusLibraryDb] --> CheckTables[Check Tables Exist:
Books, Members, Loans] --> CheckIndexes[Check Unique Indexes:
IX_Books_ISBN, IX_Members_Email] --> CheckSeed[Check Seed Data:
5 books, 3 members] --> CheckLoans[Check Loans Table:
Empty (no open loans)] --> CheckFKs[Check Foreign Keys:
FK_Loans_Books_BookId,
FK_Loans_Members_MemberId] --> Success([Database Verified
Successfully])
    
    style CheckTables fill:#fff3e0
    style CheckIndexes fill:#fff3e0
    style CheckSeed fill:#fff3e0
    style CheckLoans fill:#fff3e0
    style CheckFKs fill:#fff3e0
    style Success fill:#c8e6c9
```

## 8. Entity Relationship Diagram

```mermaid
erDiagram
    BOOKS ||--o{ LOANS : "has"
    MEMBERS ||--o{ LOANS : "borrows"
    
    BOOKS {
        int Id PK
        string ISBN UK "Max 13, Required"
        string Title "Max 200, Required"
        string Author "Max 100, Nullable"
        int PublishedYear
    }
    
    MEMBERS {
        int Id PK
        string Name "Max 100, Required"
        string Email UK "Max 100, Required"
        datetime JoinedDate
    }
    
    LOANS {
        int Id PK
        int BookId FK
        int MemberId FK
        datetime BorrowedAt "Required"
        datetime ReturnedAt "Nullable - NULL means open loan"
    }
```

## 9. Migration Rollback Flow

```mermaid
flowchart TD
    Start([Rollback Migration]) --> ListMigrations[List All Migrations
20261107074124_InitialCreate
(Applied)] --> ChooseTarget[Choose Target Migration
(0 for empty database)] --> ExecuteRollback[Execute Rollback
to Target Migration] --> VerifyRollback[Verify Schema
Matches Target Migration] --> Success([Rollback Complete
Database Updated])
    
    style ListMigrations fill:#fff3e0
    style ChooseTarget fill:#fff9c4
    style ExecuteRollback fill:#fff9c4
    style VerifyRollback fill:#fff3e0
    style Success fill:#c8e6c9
```

## 10. Database Reset Flow

```mermaid
flowchart TD
    Start([Reset Database]) --> DropDB[Drop CampusLibraryDb
IF EXISTS] --> CreateDB[Create Empty Database] --> ApplyMigrations[Apply All Migrations
to Recreate Schema] --> Success([Database Reset
Complete with Schema
and Seed Data])
    
    style DropDB fill:#ffccbc
    style CreateDB fill:#fff3e0
    style ApplyMigrations fill:#fff9c4
    style Success fill:#c8e6c9
```

## 11. Loan Business Rule Enforcement Flow

```mermaid
flowchart TD
    Start([Borrow Request]) --> CheckOpenLoans[Check for Open Loans
for BookId
WHERE ReturnedAt IS NULL] --> CountLoans{Open Loans
Exist?} -->|Yes| Reject[Reject Request
Return 409 Conflict]
    
    CountLoans -->|No| CreateLoan[Create Loan
Set BorrowedAt = Now
ReturnedAt = NULL] --> SaveLoan[Save Loan to Database] --> Success([Book Borrowed
Successfully])
    
    style CheckOpenLoans fill:#fff3e0
    style CountLoans fill:#fff9c4
    style Reject fill:#ffccbc
    style CreateLoan fill:#fff9c4
    style Success fill:#c8e6c9
```
