# FLOWCHARTS

## Borrow flow
```mermaid
flowchart TD
    A[User taps Borrow on book detail] --> B[BookViewModel.borrowBook(bookId, memberId=1)]
    B --> C[Repository POST /api/loans]
    C --> D{Response?}
    D -->|201 Created| E[navigate to My Loans]
    D -->|409 Conflict| F[Toast: "This book is already on loan"]
    D -->|Other / network fail| G[Toast: error message]
    E --> H[My Loans screen loads GET /api/loans]
    H --> I{Loans?}
    I -->|non-empty| J[Show active loans list]
    I -->|empty| K[Show "No active loans"]
    I -->|error| L[Show error + Retry]
```

## My Loans load flow
```mermaid
flowchart TD
    A[User taps My Loans icon or borrow succeeds] --> B[navigate("myLoans")]
    B --> C[LoanViewModel.loadLoans()]
    C --> D[Repository GET /api/loans]
    D --> E{Success?}
    E -->|yes & non-empty| F[Show loans list]
    E -->|yes & empty| G[Show "No active loans"]
    E -->|no| H[Show error + Retry]
    H -->|Retry| C
```

## Sequence diagram (borrow → My Loans)
```mermaid
sequenceDiagram
    participant User
    participant Screen as BookDetailScreen
    participant VM as BookViewModel
    participant Repo as BookRepository
    participant API as Retrofit/ApiService
    participant WebApi as CampusLibrary.WebApi
    participant DB as MSSQL CampusLibraryDb

    User->>Screen: taps Borrow
    Screen->>VM: borrowBook(id, memberId=1)
    VM->>Repo: POST /api/loans
    Repo->>API: HTTP POST
    API->>WebApi: POST /api/loans
    WebApi->>DB: check open loan (ReturnedAt == null)
    DB-->>WebApi: no open loan
    WebApi->>DB: insert Loan row
    DB-->>WebApi: loan created
    WebApi-->>API: 201 LoanDto
    API-->>Repo: Result.Success
    Repo-->>VM: onSuccess(loan)
    VM-->>Screen: onBorrowSuccess callback
    Screen->>Screen: navigate("myLoans")
    Screen->>LoanViewModel: loadLoans()
    LoanViewModel->>Repo: GET /api/loans
    Repo->>API->>WebApi->>DB: read loans
    DB-->>WebApi: loan rows
    WebApi-->>API-->>Repo-->>LoanViewModel-->>Screen
    Screen->>User: shows My Loans list with borrowed book
```

## Database (no change)
No schema changed; no `erDiagram` required.
